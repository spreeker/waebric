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
		if(expression instanceof Expression.VarExpression) {

		} else if(expression instanceof Expression.NatExpression) {

		} else if(expression instanceof Expression.TextExpression) {

		} else if(expression instanceof Expression.SymbolExpression) {

		} else if(expression instanceof Expression.ExpressionWithIdCon) {
			
		} else if(expression instanceof Expression.ExpressionCollection) {
			
		} else if(expression instanceof Expression.KeyValuePairCollection) {

		}
	}
	
	public void visit(Expression.VarExpression expression) {
		Var var = new Var();
		visit(var);
		((Expression.VarExpression) expression).setVar(var);
	}
	
	public void visit(Expression.NatExpression expression) {
		current = tokens.next(); // Retrieve current value
		NatCon natural = new NatCon(current.getLexeme().toString());
		((Expression.NatExpression) expression).setNatural(natural);
	}
	
	public void visit(Expression.TextExpression expression) {
		// TODO
	}
	
	public void visit(Expression.SymbolExpression expression) {
		SymbolCon symbol = new SymbolCon();
		visit(symbol);
		((Expression.SymbolExpression) expression).setSymbol(symbol);
	}
	
	public void visit(Expression.ExpressionWithIdCon expression) {
		current = tokens.next(); // Retrieve current value
		
		// Parse sub expression
		Expression subExpression = getExpression(current);
		visit(subExpression);
		((Expression.ExpressionWithIdCon) expression).setExpression(subExpression);

		// Process period separator
		if(tokens.hasNext() && tokens.peek(1).getLexeme().equals(WaebricSymbol.PERIOD)) {
			tokens.next();
		} else {
			exceptions.add(new ParserException(current.toString() + " was found while a " +
					"period was expected, use expression \".\" identifier"));
		}
		
		// Parse identifier
		if(tokens.hasNext()) {
			current = tokens.next();
			if(current.getSort() == TokenSort.IDENTIFIER) {
				IdCon identifier = new IdCon(current.getLexeme().toString());
				((Expression.ExpressionWithIdCon) expression).setIdentifier(identifier);
			} else {
				exceptions.add(new ParserException(current.toString() + " is not an identifier" +
						"use expression \".\" identifier"));
			}
		} else {
			exceptions.add(new ParserException(current.toString() + " misses an identifier, " +
				"use expression \".\" identifier"));
		}
	}
	
	public void visit(Expression.ExpressionCollection expression) {
		tokens.next(); // Skip left bracket
		
		while(tokens.hasNext()) {
			if(tokens.peek(1).getLexeme().equals(WaebricSymbol.RBRACKET)) {
				break; // End of expression collection found, break while
			}
			
			// Parse sub expression
			Expression subExpression = getExpression(current);
			visit(subExpression);
			((Expression.ExpressionCollection) expression).addExpression(subExpression);
		}
	}
	
	public void visit(Expression.KeyValuePairCollection expression) {
		tokens.next(); // Skip left curly bracket
		
		while(tokens.hasNext()) {
			if(tokens.peek(1).getLexeme().equals(WaebricSymbol.RCBRACKET)) {
				break; // End of expression collection found, break while
			}
			
			// Parse key value pair
			KeyValuePair pair = new KeyValuePair();
			visit(pair);
			((Expression.KeyValuePairCollection) expression).addKeyValuePair(pair);
		}
	}
	
	private void visit(KeyValuePair pair) {
		// TODO Auto-generated method stub
		
	}

	private void visit(SymbolCon symbol) {
		// TODO Auto-generated method stub
		
	}

	public void visit(Var var) {
		current = tokens.next();
		
		if(! current.getSort().equals(TokenSort.IDENTIFIER)) {
			exceptions.add(new ParserException(current.toString() + " is not a valid variable, " +
					"variables need to start with a letter and contain no layout symbols."));
			return; // Stop function from filling variable with invalid data
		}
		
		var.setIdentifier(new IdCon(current.getLexeme().toString()));
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
	
	public static Expression getExpression(Token token) {
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