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
import org.cwi.waebric.parser.ast.statements.Statement;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.token.Token;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.cwi.waebric.scanner.token.TokenSort;

public class MarkupParser extends AbstractParser {

	private final ExpressionParser expressionParser;
	private final StatementParser statementParser;
	
	public MarkupParser(TokenIterator tokens, List<ParserException> exceptions) {
		super(tokens, exceptions);
		
		// Initialise sub parsers
		expressionParser = new ExpressionParser(tokens, exceptions);
		statementParser = new StatementParser(tokens, exceptions);
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
			Token peek = tokens.peek(1);
			
			// Parse attribute
			if(isAttribute(peek.getLexeme().toString())) {
				Attribute attribute = null; // Initialise later
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
		current = tokens.next();
		String lexeme = current.getLexeme().toString();
		int second = -1; // Storing "%" char index
		
		if(attribute == null) { // Determine attribute type
			if(lexeme.charAt(0) == WaebricSymbol.AT_SIGN) {
				second = lexeme.indexOf("" + WaebricSymbol.PERCENT_SIGN);
				if(second != -1) {
					attribute = new Attribute.AttributeDoubleNatCon();
				} else {
					attribute = new Attribute.AttributeNatCon();
				}
			} else {
				attribute = new Attribute.AttributeIdCon(lexeme.charAt(0));
			}
		}
		
		if(attribute instanceof AttributeIdCon) {
			IdCon identifier = new IdCon(lexeme.substring(1));
			((AttributeIdCon) attribute).setIdentifier(identifier);
		} else if(attribute instanceof AttributeNatCon) {
			NatCon number = new NatCon(Integer.parseInt(lexeme.substring(1)));
			((AttributeNatCon) attribute).setNumber(number);
		} else if(attribute instanceof AttributeDoubleNatCon) {
			NatCon number = new NatCon(Integer.parseInt(
					lexeme.substring(1, second-1)));
			((AttributeDoubleNatCon) attribute).setNumber(number);
			NatCon secondNumber = new NatCon(Integer.parseInt(
					lexeme.substring(second+1, lexeme.length()-1)));
			((AttributeDoubleNatCon) attribute).setSecondNumber(secondNumber);
		}
	}
	
	public void visit(Arguments arguments) {
		current = tokens.next(); // Skip left parenthesis
		
		while(tokens.hasNext()) {
			if(tokens.peek(1).getLexeme().equals(WaebricSymbol.RPARANTHESIS)) {
				current = tokens.next(); // Skip right parenthesis
				break; // End of arguments reached, break while
			}
			
			Argument argument = null; // TODO: Determine which argument type
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
		
		
	}
	
	public void visit(Var var) {
		
	}
	
	/**
	 * @see org.cwi.waebric.parser.ExpressionParser
	 * @param expression
	 */
	public void visit(Expression expression) {
		expressionParser.visit(expression);
	}
	
	/**
	 * @see org.cwi.waebric.parser.StatementParser
	 * @param statement
	 */
	public void visit(Statement statement) {
		statementParser.visit(statement);
	}

}