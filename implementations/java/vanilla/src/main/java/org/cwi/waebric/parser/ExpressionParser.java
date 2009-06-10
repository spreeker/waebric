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
import org.cwi.waebric.parser.exception.SyntaxException;
import org.cwi.waebric.scanner.WaebricScanner;
import org.cwi.waebric.scanner.token.WaebricToken;
import org.cwi.waebric.scanner.token.WaebricTokenIterator;
import org.cwi.waebric.scanner.token.WaebricTokenSort;

/**
 * module languages/waebric/syntax/Expressions
 * 
 * @author Jeroen van Schagen
 * @date 25-05-2009
 */
class ExpressionParser extends AbstractParser {

	/**
	 * Construct expression parser
	 * @param tokens
	 * @param exceptions
	 */
	public ExpressionParser(WaebricTokenIterator tokens, List<SyntaxException> exceptions) {
		super(tokens, exceptions);
	}
	
	/**
	 * Parse expression
	 * @throws SyntaxException 
	 */
	public Expression parseExpression() throws SyntaxException {
		if(tokens.hasNext()) {
			Expression expression = null;
			
			// Determine expression type based on look-ahead
			if(tokens.peek(1).getSort() == WaebricTokenSort.SYMBOLCON) {
				// Symbol expressions consist of a symbol
				expression = parseSymbolExpression();
			} else if(tokens.peek(1).getSort() == WaebricTokenSort.NATCON) {
				// Natural expressions consist of a natural
				expression =  parseNatExpression();
			} else if(tokens.peek(1).getSort() == WaebricTokenSort.QUOTE) {
				// Textual expressions consist of a text
				expression =  parseTextExpression();
			} else if(tokens.peek(1).getSort() == WaebricTokenSort.IDCON) {
				// Variable expressions consist of variable
				expression = parseVarExpression();
			} else if(tokens.peek(1).getLexeme().equals(WaebricSymbol.LBRACKET)) {
				// List expressions start with a [
				expression = parseListExpression();
			} else if(tokens.peek(1).getLexeme().equals(WaebricSymbol.LCBRACKET)) {
				// Record expressions start with a {
				expression = parseRecordExpression();
			} 
			
			if(tokens.hasNext(2) 
					&& tokens.peek(1).getLexeme().equals(WaebricSymbol.PERIOD) 
					&& tokens.peek(2).getSort() == WaebricTokenSort.IDCON) {
				// Parse field expression
				Expression.Field field = new Expression.Field();
				field.setExpression(expression);
				next(WaebricSymbol.PERIOD, "Period separator", "Expression \".\" IdCon -> Expression");
				next(WaebricTokenSort.IDCON, "Identifier", "Expression \".\" IdCon -> Expression");
				field.setIdentifier(new IdCon(tokens.current().getLexeme().toString()));
				return field; // Return field
			} else if(tokens.hasNext() && tokens.peek(1).getLexeme().equals(WaebricSymbol.PLUS)) {
				// Parse cat expression
				Expression.CatExpression cat = new Expression.CatExpression();
				cat.setLeft(expression); // Store parsed expression
				tokens.next(); // Accept '+' and go to next token
				cat.setRight(parseExpression()); // Parse right expression
				return cat; // Return cat expression
			}
			
			// Verify that a valid expression has been parsed
			if(expression == null) { reportUnexpectedToken(tokens.peek(1), "expression", "expression"); } 
			return expression; // Return "simple" expression
		}
		
		return null;
	}
	
	/**
	 * Check if a token is an expression.
	 * @param token
	 * @return
	 */
	public static boolean isExpression(WaebricToken token) {
		if(token == null || token.getLexeme().equals("")) { return false; }
		
		if(token.getSort() == WaebricTokenSort.CHARACTER) {
			return
				token.getLexeme().equals(WaebricSymbol.LBRACKET) ||
				token.getLexeme().equals(WaebricSymbol.LCBRACKET);
		} else {
			// All remaining sorts are allowed, except keywords
			return token.getSort() != WaebricTokenSort.KEYWORD;
		}
	}
	
	/**
	 * @see Expression.VarExpression
	 * @throws SyntaxException 
	 */
	public Expression.VarExpression parseVarExpression() throws SyntaxException {
		Expression.VarExpression expression = new Expression.VarExpression();
		
		try {
			next(WaebricTokenSort.IDCON, "Variable", "Var -> Expression");
			expression.setVar(new IdCon(tokens.current().getLexeme().toString()));
		} catch(SyntaxException e) {
			reportUnexpectedToken(tokens.current(), 
					"Var expression", "Var -> Expression");
		}
		
		return expression;
	}
	
	/**
	 * @see Expression.NatExpression
	 * @throws SyntaxException 
	 */
	public Expression.NatExpression parseNatExpression() throws SyntaxException {
		next(WaebricTokenSort.NATCON, "Natural expression", "NatCon -> Expression");
		Expression.NatExpression expression = new Expression.NatExpression();
		expression.setNatural(new NatCon(tokens.current().getLexeme().toString()));
		return expression;
	}
	
	/**
	 * @throws SyntaxException 
	 * @see Expression.TextExpression
	 */
	public Expression.TextExpression parseTextExpression() throws SyntaxException {
		next(WaebricTokenSort.QUOTE, "Textual expression","Text -> Expression");
		if(WaebricScanner.isTextChars(tokens.current().getLexeme().toString())) {
			Expression.TextExpression expression = new Expression.TextExpression();
			Text text = new Text(new StringLiteral(tokens.current().getLexeme().toString()));
			expression.setText(text);
			return expression;
		} else {
			reportUnexpectedToken(tokens.current(), "Text expression", "Text -> Expression");
		}
	
		return null;
	}
	
	/**
	 * @throws SyntaxException 
	 * @see Expression.SymbolExpression
	 */
	public Expression.SymbolExpression parseSymbolExpression() throws SyntaxException {
		next(WaebricTokenSort.SYMBOLCON, "symbol expression", "\'symbol");
		Expression.SymbolExpression expression = new Expression.SymbolExpression();
		SymbolCon symbol = new SymbolCon(new StringLiteral(tokens.current().getLexeme().toString()));
		expression.setSymbol(symbol);
		return expression;
	}
	
	/**
	 * @see Expression.ListExpression
	 * @throws SyntaxException 
	 */
	public Expression.ListExpression parseListExpression() throws SyntaxException {
		next(WaebricSymbol.LBRACKET, "List opening \"[\"", "\"[\" { Expression }* \"]\" -> Expression");
		
		Expression.ListExpression expression = new Expression.ListExpression();
		while(tokens.hasNext()) {
			if(tokens.peek(1).getLexeme().equals(WaebricSymbol.RBRACKET)) {
				break; // ] marks the end of collection, quit loop
			}
			
			try {
				// Parse sub expression
				expression.addExpression(parseExpression());
			} catch(SyntaxException e) {
				reportUnexpectedToken(tokens.current(), 
						"Expression", "\"[\" { Expression }* \"]\" -> Expression");
			}
			
			// While not end of expressions, comma separator is expected
			if(tokens.hasNext() && ! tokens.peek(1).getLexeme().equals(WaebricSymbol.RBRACKET)) {
				next(WaebricSymbol.COMMA, "List element separator", "Expression \",\" Expression");
			}
		}
		
		next(WaebricSymbol.RBRACKET, "List closure \"]\"", "\"[\" { Expression }* \"]\" -> Expression");
		return expression;
	}
	
	/**
	 * @see Expression.RecordExpression
	 * @throws SyntaxException 
	 */
	public Expression.RecordExpression parseRecordExpression() throws SyntaxException {
		next(WaebricSymbol.LCBRACKET, "Record opening \"{\"", "\"{\" { KeyValuePair \",\" }* \"}\"");
		
		Expression.RecordExpression expression = new Expression.RecordExpression();
		while(tokens.hasNext()) {
			if(tokens.peek(1).getLexeme().equals(WaebricSymbol.RCBRACKET)) {
				break; // End of expression collection found, break while
			}
			
			try {
				// Parse key value pair
				expression.addKeyValuePair(parseKeyValuePair());
			} catch(SyntaxException e) {
				reportUnexpectedToken(tokens.current(), 
						"Key value pair", "\"{\" { KeyValuePair \",\" }* \"}\"");
			}
			
			// While not end of expressions, comma separator is expected
			if(tokens.hasNext() && ! tokens.peek(1).getLexeme().equals(WaebricSymbol.RCBRACKET)) {
				next(WaebricSymbol.COMMA, "List element separator", "Expression \",\" Expression");
			}
		}
		
		next(WaebricSymbol.RCBRACKET, "Record closure \"}\"", "\"{\" { KeyValuePair \",\" }* \"}\"");
		return expression;
	}
	
	/**
	 * @see KeyValuePair
	 * @param pair
	 */
	public KeyValuePair parseKeyValuePair() throws SyntaxException {
		KeyValuePair pair = new KeyValuePair();
		
		// Parse identifier
		next(WaebricTokenSort.IDCON, "Identifier", "IdCon \":\" Expression -> KeyValuePair");
		IdCon identifier = new IdCon(tokens.current().getLexeme().toString());
		pair.setIdentifier(identifier);
		
		next(WaebricSymbol.COLON, "Colon separator", "IdCon \":\" Expression -> KeyValuePair");
		
		try {
			// Parse expression
			pair.setExpression(parseExpression());
		} catch(SyntaxException e) {
			reportUnexpectedToken(tokens.current(), "Expression", 
					"IdCon \":\" Expression -> KeyValuePair");
		}
		
		return pair;
	}
	
}