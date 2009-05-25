package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.basic.NatCon;
import org.cwi.waebric.parser.ast.expressions.Expression;
import org.cwi.waebric.parser.ast.expressions.Var;
import org.cwi.waebric.parser.ast.markup.Argument;
import org.cwi.waebric.parser.ast.markup.Arguments;
import org.cwi.waebric.parser.ast.markup.Attribute;
import org.cwi.waebric.parser.ast.markup.Attributes;
import org.cwi.waebric.parser.ast.markup.Designator;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.ast.markup.Attribute.AttributeDoubleNatCon;
import org.cwi.waebric.parser.ast.markup.Attribute.AttributeIdCon;
import org.cwi.waebric.parser.ast.markup.Attribute.AttributeNatCon;
import org.cwi.waebric.parser.ast.markup.Markup.MarkupWithArguments;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.cwi.waebric.scanner.token.TokenSort;

public class MarkupParser extends AbstractParser {

	private final ExpressionParser expressionParser;
	
	public MarkupParser(TokenIterator tokens, List<ParserException> exceptions) {
		super(tokens, exceptions);
		
		// Initialize sub parsers
		expressionParser = new ExpressionParser(tokens, exceptions);
	}
	
	public void visit(Markup markup) {
		// Parse designator
		Designator designator = new Designator();
		visit(designator);
		markup.setDesignator(designator);
		
		// Parse arguments
		if(markup instanceof MarkupWithArguments) {
			visit(markup.getArguments());
		}
	}
	
	public void visit(Designator designator) {
		// Parse identifier
		current = tokens.next();
		if(current.getSort() == TokenSort.IDENTIFIER) {
			IdCon identifier = new IdCon(current.getLexeme().toString());
			designator.setIdentifier(identifier);
		} else {
			exceptions.add(new ParserException(current.toString() + " is not " +
					"a valid designator identifier."));
		}
		
		// Parse attributes
		visit(designator.getAttributes());
	}
	
	public void visit(Attributes attributes) {
		while(tokens.hasNext()) {
			String lexeme = tokens.peek(1).getLexeme().toString();
			
			// Parse attribute
			if(isAttribute(lexeme)) {
				Attribute attribute = null;
				if(lexeme.charAt(0) == WaebricSymbol.AT_SIGN) {
					int index = lexeme.indexOf(WaebricSymbol.PERCENT_SIGN);
					if(index != -1) {
						attribute = new Attribute.AttributeDoubleNatCon();
					} else {
						attribute = new Attribute.AttributeNatCon();
					}
				} else {
					// Initiate regular attribute based on first character
					attribute = new Attribute.AttributeIdCon(lexeme.charAt(0));
				}
				visit(attribute);
				attributes.add(attribute);
			} else {
				break; // Out of attributes, break while
			}
		}
	}
	
	public static boolean isAttribute(String lexeme) {
		return lexeme.matches("(#\\w+)|(\\.\\w+)|(\\$\\w+)|(:\\w+)|(@\\d+%\\d+)|(@\\d+)");
	}
	
	public void visit(Attribute attribute) {
		current = tokens.next(); // Retrieve attribute
		String lexeme = current.getLexeme().toString();
		
		if(attribute instanceof AttributeIdCon) {
			IdCon identifier = new IdCon(lexeme.substring(1));
			((AttributeIdCon) attribute).setIdentifier(identifier);
		} else if(attribute instanceof AttributeNatCon) {
			try {
				int number = Integer.parseInt(lexeme.substring(1));
				((AttributeNatCon) attribute).setNumber(new NatCon(number));
			} catch(NumberFormatException e) {
				exceptions.add(new ParserException(current.toString() + " is not a valid " +
						"numeral attribute, use @number"));
			}
		} else if(attribute instanceof AttributeDoubleNatCon) {
			try {
				int index = lexeme.indexOf(WaebricSymbol.PERCENT_SIGN);
				
				// Parse first number
				int first = Integer.parseInt(lexeme.substring(1, index-1));
				((AttributeDoubleNatCon) attribute).setNumber(new NatCon(first));
				
				// Parse second number
				int second = Integer.parseInt(lexeme.substring(index+1, lexeme.length()-1));
				((AttributeDoubleNatCon) attribute).setSecondNumber(new NatCon(second));
			} catch(NumberFormatException e) {
				exceptions.add(new ParserException(current.toString() + " is not a valid " +
				"numeral attribute, use @number%number"));
			}
		}
	}
	
	public void visit(Arguments arguments) {
		current = tokens.next(); // Skip left parenthesis
		
		while(tokens.hasNext()) {
			if(tokens.peek(1).getLexeme().equals(WaebricSymbol.RPARANTHESIS)) {
				current = tokens.next(); // Skip right parenthesis
				break; // End of arguments reached, break while
			}
			
			// Parse argument
			Argument argument = null;
			if(tokens.hasNext(2) && tokens.peek(2).equals(WaebricSymbol.EQUAL_SIGN)) {
				argument = new Argument.ArgumentWithVar();
			} else {
				argument = new Argument.ArgumentWithoutVar();
			}
			visit(argument);
			arguments.add(argument);
			
			current = tokens.next(); // Skip comma separator
		}
		
		if(! current.getLexeme().equals(WaebricSymbol.RPARANTHESIS)) {
			exceptions.add(new ParserException(current.toString() + " missing arguments " +
					"closure token, use \")\""));
		}
	}
	
	public void visit(Argument argument) {
		current = tokens.next(); // Retrieve argument
		
		if(argument instanceof Argument.ArgumentWithVar) {
			Var var = new Var();
			visit(var); // Parse variable
			((Argument.ArgumentWithVar) argument).setVar(var);
			
			tokens.next(); // Skip equals sign
		}
		
		// Parse expression
		try {
			Expression expression = ExpressionParser.getExpressionType(tokens.peek(1)).newInstance();
			visit(expression);
			argument.setExpression(expression);
		} catch (InstantiationException e) {
			e.printStackTrace(); // Should never occur
		} catch (IllegalAccessException e) {
			e.printStackTrace(); // Should never occur
		}
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
	 * @see org.cwi.waebric.parser.ExpressionParser
	 * @param expression
	 */
	public void visit(Expression expression) {
		expressionParser.visit(expression);
	}

}