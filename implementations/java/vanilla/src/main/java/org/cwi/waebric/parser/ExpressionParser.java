package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.basic.NatCon;
import org.cwi.waebric.parser.ast.expressions.Expression;
import org.cwi.waebric.parser.ast.expressions.KeyValuePair;
import org.cwi.waebric.parser.ast.expressions.SymbolCon;
import org.cwi.waebric.parser.ast.expressions.Var;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.parser.exception.UnclosedCollectionException;
import org.cwi.waebric.scanner.token.Token;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.cwi.waebric.scanner.token.TokenSort;

/**
 * module languages/waebric/syntax/Expressions
 * @author schagen
 *
 */
public class ExpressionParser extends AbstractParser {

	public ExpressionParser(TokenIterator tokens, List<ParserException> exceptions) {
		super(tokens, exceptions);
	}
	
	public void visit(Expression expression) {
		// Delegate parse to sub function
		if(expression instanceof Expression.VarExpression) {
			visit((Expression.VarExpression) expression);
		} else if(expression instanceof Expression.NatExpression) {
			visit((Expression.NatExpression) expression);
		} else if(expression instanceof Expression.TextExpression) {
			visit((Expression.TextExpression) expression);
		} else if(expression instanceof Expression.SymbolExpression) {
			visit((Expression.SymbolExpression) expression);
		} else if(expression instanceof Expression.ExpressionWithIdCon) {
			visit((Expression.ExpressionWithIdCon) expression);
		} else if(expression instanceof Expression.ExpressionCollection) {
			visit((Expression.ExpressionCollection) expression);
		} else if(expression instanceof Expression.KeyValuePairCollection) {
			visit((Expression.KeyValuePairCollection) expression);
		}
	}
	
	private void visit(Expression.VarExpression expression) {
		Var var = new Var();
		visit(var);
		expression.setVar(var);
	}
	
	private void visit(Expression.NatExpression expression) {
		current = tokens.next(); // Retrieve current value
		NatCon natural = new NatCon(current.getLexeme().toString());
		expression.setNatural(natural);
	}
	
	private void visit(Expression.TextExpression expression) {
		// TODO
	}
	
	private void visit(Expression.SymbolExpression expression) {
		SymbolCon symbol = new SymbolCon();
		visit(symbol);
		expression.setSymbol(symbol);
	}
	
	private void visit(Expression.ExpressionWithIdCon expression) {
		// Parse sub expression
		Expression subExpression = newExpression(tokens.peek(1));
		visit(subExpression);
		expression.setExpression(subExpression);

		// Parse period separator
		next("period", "" + WaebricSymbol.PERIOD);

		// Parse identifier
		if(next("period", TokenSort.IDENTIFIER)) {
			IdCon identifier = new IdCon(current.getLexeme().toString());
			expression.setIdentifier(identifier); // Store identifier
		}
	}
	
	private void visit(Expression.ExpressionCollection expression) {
		next("expression collection opening bracket", "" + WaebricSymbol.LBRACKET);
		
		while(tokens.hasNext()) {
			if(tokens.peek(1).getLexeme().equals(WaebricSymbol.RBRACKET)) {
				break; // End of expression collection found, break while
			}
			
			// Parse sub expression
			Expression subExpression = newExpression(current);
			visit(subExpression);
			expression.addExpression(subExpression);
			
			// Parse comma separator
			if(tokens.hasNext() && tokens.peek(1).getLexeme().equals(WaebricSymbol.COMMA)) {
				tokens.next(); // Skip comma separator
			} else {
				break; // No more separator, quit parsing
			}
		}
		
		if(! current.getLexeme().equals(WaebricSymbol.COMMA)) {
			exceptions.add(new UnclosedCollectionException(current, "expression", "}"));
		}
	}
	
	private void visit(Expression.KeyValuePairCollection expression) {
		next("key value pair collection opening bracket", "" + WaebricSymbol.LCBRACKET);
		
		while(tokens.hasNext()) {
			if(tokens.peek(1).getLexeme().equals(WaebricSymbol.RCBRACKET)) {
				break; // End of expression collection found, break while
			}
			
			// Parse key value pair
			KeyValuePair pair = new KeyValuePair();
			visit(pair);
			expression.addKeyValuePair(pair);
			
			// Parse comma separator
			if(tokens.hasNext() && tokens.peek(1).getLexeme().equals(WaebricSymbol.COMMA)) {
				tokens.next(); // Skip comma separator
			} else {
				break; // No more separator, quit parsing
			}
		}
		
		if(! current.getLexeme().equals(WaebricSymbol.COMMA)) {
			exceptions.add(new UnclosedCollectionException(current, "key value pair", "}"));
		}
	}
	
	private void visit(KeyValuePair pair) {
		// Parse identifier
		if(next("identifier", TokenSort.IDENTIFIER)) {
			IdCon identifier = new IdCon(current.getLexeme().toString());
			pair.setIdentifier(identifier);
		}
		
		// Parse separator
		next("colon", "" + WaebricSymbol.COLON);
		
		// Parse expression
		Expression expression = newExpression(tokens.peek(1));
		visit(expression);
		pair.setExpression(expression);
	}

	private void visit(SymbolCon symbol) {
		next("symbol opening quote", "" + WaebricSymbol.SQUOTE);
		// TODO: Figure out how to stop loop
	}

	public void visit(Var var) {
		if(next("identifier", TokenSort.IDENTIFIER)) {
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