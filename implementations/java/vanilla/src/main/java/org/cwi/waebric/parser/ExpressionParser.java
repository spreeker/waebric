package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.StringLiteral;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.basic.NatCon;
import org.cwi.waebric.parser.ast.basic.SymbolCon;
import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.expression.KeyValuePair;
import org.cwi.waebric.parser.ast.expression.Text;
import org.cwi.waebric.parser.ast.expression.Var;
import org.cwi.waebric.parser.exception.MissingTokenException;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.WaebricScanner;
import org.cwi.waebric.scanner.token.WaebricToken;
import org.cwi.waebric.scanner.token.WaebricTokenIterator;
import org.cwi.waebric.scanner.token.WaebricTokenSort;

/**
 * Expressions
 * 
 * module languages/waebric/syntax/Expressions
 * 
 * @author Jeroen van Schagen
 * @date 25-05-2009
 */
class ExpressionParser extends AbstractParser {

	public ExpressionParser(WaebricTokenIterator tokens, List<ParserException> exceptions) {
		super(tokens, exceptions);
	}
	
	public Expression parseExpression(String name, String syntax) {
		if(! tokens.hasNext()) {
			exceptions.add(new MissingTokenException(name, syntax));
			return null;
		}
		
		WaebricToken peek = tokens.peek(1); // Determine expression type based on look-ahead
		if(peek.getLexeme().equals(WaebricSymbol.LBRACKET)) {
			// List expressions start with a [
			return parseListExpression();
		} else if(peek.getLexeme().equals(WaebricSymbol.LCBRACKET)) {
			// Record expressions start with a {
			return parseRecordExpression();
		} else if(peek.getSort().equals(WaebricTokenSort.SYMBOLCON)) {
			// Symbol expressions consist of a symbol
			return parseSymbolExpression();
		} else if(peek.getSort().equals(WaebricTokenSort.NATCON)) {
			// Natural expressions consist of a natural
			return parseNatExpression();
		} else if(peek.getSort().equals(WaebricTokenSort.QUOTE)) {
			// Textual expressions consist of a text
			return parseTextExpression();
		} else if(peek.getSort().equals(WaebricTokenSort.IDCON)) {
			// Variable expressions consist of variable
			return parseVarExpression();
		} else { // Identifier expressions are only remaining alternative
			return parseIdConExpression();
		}
	}
	
	/**
	 * @see Expression.VarExpression
	 * @param expression
	 */
	public Expression.VarExpression parseVarExpression() {
		Expression.VarExpression expression = new Expression.VarExpression();
		Var var = parseVar("var expression", "expression");
		expression.setVar(var);
		return expression;
	}
	
	/**
	 * @see Expression.NatExpression
	 * @param expression
	 */
	public Expression.NatExpression parseNatExpression() {
		if(next("natural expression", "natural number", WaebricTokenSort.NATCON)) {
			Expression.NatExpression expression = new Expression.NatExpression();
			NatCon natural = new NatCon(current.getLexeme().toString());
			expression.setNatural(natural);
			return expression;
		}
		
		return null;
	}
	
	/**
	 * @see Expression.TextExpression
	 * @param expression
	 */
	public Expression.TextExpression parseTextExpression() {
		if(next("text expression", "\"text\"", WaebricTokenSort.QUOTE)) {
			if(WaebricScanner.isTextChars(current.getLexeme().toString())) {
				Expression.TextExpression expression = new Expression.TextExpression();
				Text text = new Text(new StringLiteral(current.getLexeme().toString()));
				expression.setText(text);
				return expression;
			} else {
				reportUnexpectedToken(current, "text expression", "\"text\"");
			}
		}
		
		return null;
	}
	
	/**
	 * @see Expression.SymbolExpression
	 * @param expression
	 */
	public Expression.SymbolExpression parseSymbolExpression() {
		if(next("symbol expression", "\'symbol", WaebricTokenSort.SYMBOLCON)) {
			Expression.SymbolExpression expression = new Expression.SymbolExpression();
			SymbolCon symbol = new SymbolCon(new StringLiteral(current.getLexeme().toString()));
			expression.setSymbol(symbol);
			return expression;
		}
		
		return null;
	}
	
	/**
	 * @see Expression.IdConExpression
	 * @param expression
	 */
	public Expression.IdConExpression parseIdConExpression() {
		Expression.IdConExpression expression = new Expression.IdConExpression();
		
		// Parse sub expression
		Expression subExpression = parseExpression("expression", "expression \".\" identifier");
		expression.setExpression(subExpression);

		// Parse period separator
		if(! next("period", "expression \".\" identifier", WaebricSymbol.PERIOD)) {
			return null; // Incorrect expression syntax, quit parsing
		}

		// Parse identifier
		if(next("period", "expression \".\" identifier", WaebricTokenSort.IDCON)) {
			IdCon identifier = new IdCon(current.getLexeme().toString());
			expression.setIdentifier(identifier); // Store identifier
		} else {
			return null; // Incorrect expression syntax, return empty node
		}
		
		return expression;
	}
	
	/**
	 * @see Expression.ListExpression
	 * @param expression
	 */
	public Expression.ListExpression parseListExpression() {
		Expression.ListExpression expression = new Expression.ListExpression();
		
		if(! next("list opening", "\"[\" expressions", WaebricSymbol.LBRACKET)) {
			return null; // Incorrect list syntax, quit parsing
		}
		
		while(tokens.hasNext()) {
			if(tokens.peek(1).getLexeme().equals(WaebricSymbol.RBRACKET)) {
				break; // End of expression collection found, break while
			}
			
			// Parse sub expression
			Expression subExpression = parseExpression("expression", "\"[\" expression \"]\"");
			expression.addExpression(subExpression);
			
			// While not end of expressions, comma separator is expected
			if(tokens.hasNext() && ! tokens.peek(1).getLexeme().equals(WaebricSymbol.RBRACKET)) {
				next("list element separator", "expression \",\" expression", WaebricSymbol.COMMA);
			}
		}
		
		if(! next("list closure", "expressions \"]\"", WaebricSymbol.RBRACKET)) {
			return null; // Incorrect list syntax, return empty node
		}
		
		return expression;
	}
	
	/**
	 * @see Expression.RecordExpression
	 * @param expression
	 */
	public Expression.RecordExpression parseRecordExpression() {
		Expression.RecordExpression expression = new Expression.RecordExpression();
		
		if(! next("record opening", "\"{\" pairs", WaebricSymbol.LCBRACKET)) {
			return null; // Incorrect record syntax, quit parsing
		}
		
		while(tokens.hasNext()) {
			if(tokens.peek(1).getLexeme().equals(WaebricSymbol.RCBRACKET)) {
				break; // End of expression collection found, break while
			}
			
			// Parse key value pair
			KeyValuePair pair = parseKeyValuePair();
			expression.addKeyValuePair(pair);
			
			// While not end of pairs, comma separator is expected
			if(tokens.hasNext() && ! tokens.peek(1).getLexeme().equals(WaebricSymbol.RCBRACKET)) {
				next("record element separator", "key value pair \",\" key value pair", WaebricSymbol.COMMA);
			}
		}
		
		if(! next("record closure", "pairs \"}\"", WaebricSymbol.RCBRACKET)) {
			return null; // Incorrect record syntax, return empty node
		}
		
		return expression;
	}
	
	/**
	 * @see KeyValuePair
	 * @param pair
	 */
	public KeyValuePair parseKeyValuePair() {
		KeyValuePair pair = new KeyValuePair();
		
		// Parse identifier
		if(next("identifier", "identifier \":\" expression", WaebricTokenSort.IDCON)) {
			IdCon identifier = new IdCon(current.getLexeme().toString());
			pair.setIdentifier(identifier);
		} else {
			return null; // Incorrect identifier syntax, quit parsing
		}
		
		// Parse separator
		if(! next("colon", "identifier \":\" expression", WaebricSymbol.COLON)) {
			return null; // Incorrect pair syntax, quit parsing
		}
		
		// Parse expression
		Expression expression = parseExpression("expression", "identifier \":\" expression");
		pair.setExpression(expression);
		
		return pair;
	}
	
	/**
	 * @see Var
	 * @param var
	 */
	public Var parseVar(String name, String syntax) {
		if(next(name, syntax,WaebricTokenSort.IDCON)) {
			Var var = new Var(current.getLexeme().toString());
			return var;
		}
		
		return null;
	}

}