package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.StringLiteral;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.basic.NatCon;
import org.cwi.waebric.parser.ast.expressions.Expression;
import org.cwi.waebric.parser.ast.expressions.KeyValuePair;
import org.cwi.waebric.parser.ast.expressions.SymbolCon;
import org.cwi.waebric.parser.ast.expressions.Var;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.token.Token;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.cwi.waebric.scanner.token.TokenSort;

/**
 * module languages/waebric/syntax/Expressions
 * @author schagen
 *
 */
class ExpressionParser extends AbstractParser {

	public ExpressionParser(TokenIterator tokens, List<ParserException> exceptions) {
		super(tokens, exceptions);
	}
	
	/**
	 * 
	 * @param expression
	 */
	public void parse(Expression expression) {
		// Delegate parse to sub function
		if(expression instanceof Expression.VarExpression) {
			parse((Expression.VarExpression) expression);
		} else if(expression instanceof Expression.NatExpression) {
			parse((Expression.NatExpression) expression);
		} else if(expression instanceof Expression.TextExpression) {
			parse((Expression.TextExpression) expression);
		} else if(expression instanceof Expression.SymbolExpression) {
			parse((Expression.SymbolExpression) expression);
		} else if(expression instanceof Expression.ExpressionWithIdCon) {
			parse((Expression.ExpressionWithIdCon) expression);
		} else if(expression instanceof Expression.ExpressionCollection) {
			parse((Expression.ExpressionCollection) expression);
		} else if(expression instanceof Expression.KeyValuePairCollection) {
			parse((Expression.KeyValuePairCollection) expression);
		}
	}
	
	/**
	 * 
	 * @param expression
	 */
	private void parse(Expression.VarExpression expression) {
		Var var = new Var();
		parse(var);
		expression.setVar(var);
	}
	
	/**
	 * 
	 * @param expression
	 */
	private void parse(Expression.NatExpression expression) {
		if(next("natural expression", "natural number", TokenSort.NUMBER)) {
			NatCon natural = new NatCon(current.getLexeme().toString());
			expression.setNatural(natural);
		}
	}
	
	/**
	 * 
	 * @param expression
	 */
	private void parse(Expression.TextExpression expression) {
		if(next("text expression", "\"text\"", TokenSort.TEXT)) {
			expression.setText(new StringLiteral(current.getLexeme().toString()));
		}
	}
	
	/**
	 * 
	 * @param expression
	 */
	private void parse(Expression.SymbolExpression expression) {
		SymbolCon symbol = new SymbolCon();
		parse(symbol);
		expression.setSymbol(symbol);
	}
	
	/**
	 * 
	 * @param expression
	 */
	private void parse(Expression.ExpressionWithIdCon expression) {
		// Parse sub expression
		Expression subExpression = newExpression(tokens.peek(1));
		parse(subExpression);
		expression.setExpression(subExpression);

		// Parse period separator
		next("period", "expression \".\" identifier", WaebricSymbol.PERIOD);

		// Parse identifier
		if(next("period", "expression \".\" identifier", TokenSort.IDENTIFIER)) {
			IdCon identifier = new IdCon(current.getLexeme().toString());
			expression.setIdentifier(identifier); // Store identifier
		}
	}
	
	/**
	 * 
	 * @param expression
	 */
	private void parse(Expression.ExpressionCollection expression) {
		next("expression collection opening", "\"[\" expressions", WaebricSymbol.LBRACKET);
		
		while(tokens.hasNext()) {
			if(tokens.peek(1).getLexeme().equals(WaebricSymbol.RBRACKET)) {
				break; // End of expression collection found, break while
			}
			
			// Parse sub expression
			Expression subExpression = newExpression(tokens.peek(1));
			parse(subExpression);
			expression.addExpression(subExpression);
			
			// While not end of expressions, comma separator is expected
			if(tokens.hasNext() && ! tokens.peek(1).getLexeme().equals(WaebricSymbol.RBRACKET)) {
				next("expressions separator", "expression \",\" expression", WaebricSymbol.COMMA);
			}
		}
		
		next("expression collection closure", "expressions \"]\"", WaebricSymbol.RBRACKET);
	}
	
	/**
	 * 
	 * @param expression
	 */
	private void parse(Expression.KeyValuePairCollection expression) {
		next("key value pair collection opening", "\"{\" pairs", WaebricSymbol.LCBRACKET);
		
		while(tokens.hasNext()) {
			if(tokens.peek(1).getLexeme().equals(WaebricSymbol.RCBRACKET)) {
				break; // End of expression collection found, break while
			}
			
			// Parse key value pair
			KeyValuePair pair = new KeyValuePair();
			parse(pair);
			expression.addKeyValuePair(pair);
			
			// While not end of pairs, comma separator is expected
			if(tokens.hasNext() && ! tokens.peek(1).getLexeme().equals(WaebricSymbol.RCBRACKET)) {
				next("key value pair separator", "key value pair \",\" key value pair", WaebricSymbol.COMMA);
			}
		}
		
		next("key value pair collection closure", "pairs \"}\"", WaebricSymbol.RCBRACKET);
	}
	
	/**
	 * 
	 * @param pair
	 */
	private void parse(KeyValuePair pair) {
		// Parse identifier
		if(next("identifier", "identifier \":\" expression", TokenSort.IDENTIFIER)) {
			IdCon identifier = new IdCon(current.getLexeme().toString());
			pair.setIdentifier(identifier);
		}
		
		// Parse separator
		next("colon", "identifier \":\" expression", WaebricSymbol.COLON);
		
		// Parse expression
		Expression expression = newExpression(tokens.peek(1));
		parse(expression);
		pair.setExpression(expression);
	}

	/**
	 * 
	 * @param symbol
	 */
	private void parse(SymbolCon symbol) {
		next("symbol opening quote", "\"'\" characters", WaebricSymbol.SQUOTE);
		// TODO: Figure out how to stop loop
	}

	/**
	 * 
	 * @param var
	 */
	public void parse(Var var) {
		if(next("identifier", "identifier",TokenSort.IDENTIFIER)) {
			var.setIdentifier(new IdCon(current.getLexeme().toString()));
		}
	}
	
	/**
	 * Stored in separate method as both mark-up and statement use
	 * expressions in their syntax.
	 * @param token
	 * @return
	 */
	public static Class<? extends Expression> getExpressionType(Token token) {
		if(token.getLexeme().equals(WaebricSymbol.LBRACKET)) {
			// Expression collections start with a [
			return Expression.ExpressionCollection.class;
		} else if(token.getLexeme().equals(WaebricSymbol.LCBRACKET)) {
			// Key value pair collections start with a {
			return Expression.KeyValuePairCollection.class;
		} else if(token.getLexeme().equals(WaebricSymbol.SQUOTE)) {
			// Symbol cons start with a '
			return Expression.SymbolExpression.class;
		} else if(token.getSort().equals(TokenSort.NUMBER)) {
			// Natural expressions consist of a natural
			return Expression.NatExpression.class;
		} else if(token.getSort().equals(TokenSort.TEXT)) {
			// Textual expressions consist of a text
			return Expression.TextExpression.class;
		} else if(token.getSort().equals(TokenSort.IDENTIFIER)) {
			// Variable expressions consist of variable
			return Expression.VarExpression.class;
		} else { // Only remaining alternative: Expression "." IdCon -> Expression
			return Expression.ExpressionWithIdCon.class;
		}
	}
	
	/**
	 * 
	 * @param token
	 * @return
	 */
	public static Expression newExpression(Token token) {
		try {
			return getExpressionType(token).newInstance();
		} catch (InstantiationException e) {
			System.err.println("Critical error retrieving expression from " + token.toString());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.err.println("Critical error retrieving expression from " + token.toString());
			e.printStackTrace();
		}
		
		return null;
	}

}