package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.StringLiteral;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.basic.NatCon;
import org.cwi.waebric.parser.ast.basic.StrCon;
import org.cwi.waebric.parser.ast.basic.SymbolCon;
import org.cwi.waebric.parser.ast.expressions.Expression;
import org.cwi.waebric.parser.ast.expressions.KeyValuePair;
import org.cwi.waebric.parser.ast.expressions.Var;
import org.cwi.waebric.parser.exception.MissingTokenException;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.token.Token;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.cwi.waebric.scanner.token.TokenSort;

/**
 * Expressions
 * 
 * module languages/waebric/syntax/Expressions
 * 
 * @author Jeroen van Schagen
 * @date 25-05-2009
 */
class ExpressionParser extends AbstractParser {

	public ExpressionParser(TokenIterator tokens, List<ParserException> exceptions) {
		super(tokens, exceptions);
	}
	
	public Expression parseExpression(String name, String syntax) {
		if(! tokens.hasNext()) {
			exceptions.add(new MissingTokenException(name, syntax));
			return null;
		}
		
		Token peek = tokens.peek(1); // Determine expression type based on look-ahead
		if(peek.getLexeme().equals(WaebricSymbol.LBRACKET)) {
			// List expressions start with a [
			return parseListExpression();
		} else if(peek.getLexeme().equals(WaebricSymbol.LCBRACKET)) {
			// Record expressions start with a {
			return parseRecordExpression();
		} else if(peek.getSort().equals(TokenSort.SYMBOLCON)) {
			// Symbol expressions consist of a symbol
			return parseSymbolExpression();
		} else if(peek.getSort().equals(TokenSort.NATCON)) {
			// Natural expressions consist of a natural
			return parseNatExpression();
		} else if(peek.getSort().equals(TokenSort.STRCON)) {
			// Textual expressions consist of a text
			return parseTextExpression();
		} else if(peek.getSort().equals(TokenSort.IDCON)) {
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
		if(next("natural expression", "natural number", TokenSort.NATCON)) {
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
		if(next("text expression", "\"text\"", TokenSort.STRCON)) {
			Expression.TextExpression expression = new Expression.TextExpression();
			StrCon text = new StrCon(new StringLiteral(current.getLexeme().toString()));
			expression.setText(text);
			return expression;
		}
		
		return null;
	}
	
	/**
	 * @see Expression.SymbolExpression
	 * @param expression
	 */
	public Expression.SymbolExpression parseSymbolExpression() {
		if(next("symbol expression", "\'symbol", TokenSort.SYMBOLCON)) {
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
		if(next("period", "expression \".\" identifier", TokenSort.IDCON)) {
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
		if(next("identifier", "identifier \":\" expression", TokenSort.IDCON)) {
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
		if(next(name, syntax,TokenSort.IDCON)) {
			Var var = new Var();
			var.setIdentifier(new IdCon(current.getLexeme().toString()));
			return var;
		}
		
		return null;
	}

}