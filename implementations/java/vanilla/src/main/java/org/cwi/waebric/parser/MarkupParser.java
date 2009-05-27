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
import org.cwi.waebric.parser.exception.UnexpectedTokenException;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.cwi.waebric.scanner.token.TokenSort;

public class MarkupParser extends AbstractParser {

	private final ExpressionParser expressionParser;
	
	public MarkupParser(TokenIterator tokens, List<ParserException> exceptions) {
		super(tokens, exceptions);
		
		// Initialize sub parsers
		expressionParser = new ExpressionParser(tokens, exceptions);
	}
	
	/**
	 * 
	 * @param markup
	 */
	public void visit(Markup markup) {
		// Parse designator
		Designator designator = new Designator();
		visit(designator);
		markup.setDesignator(designator);
		
		// Parse arguments
		if(markup instanceof MarkupWithArguments) {
			visit(((MarkupWithArguments) markup).getArguments());
		}
	}
	
	/**
	 * 
	 * @param designator
	 */
	public void visit(Designator designator) {
		if(next("designator identifier", "identifier", TokenSort.IDENTIFIER)) {
			// Parse identifier
			IdCon identifier = new IdCon(current.getLexeme().toString());
			designator.setIdentifier(identifier);
		}
		
		// Parse potential attributes
		visit(designator.getAttributes());
	}
	
	/**
	 * 
	 * @param attributes
	 */
	public void visit(Attributes attributes) {
		while(tokens.hasNext()) {
			// Look-ahead for attribute symbol
			String peek = tokens.peek(1).getLexeme().toString();
			if(peek.length() != 1) { break; }
			char symbol = peek.charAt(0);
			
			// Determine attribute type based on retrieved symbol
			Attribute attribute = null; 
			if(symbol == WaebricSymbol.AT_SIGN) {
				if(tokens.hasNext(3) && tokens.peek(3).getLexeme().equals(WaebricSymbol.PERCENT_SIGN)) {
					attribute = new Attribute.AttributeDoubleNatCon();
				} else {
					attribute = new Attribute.AttributeNatCon();
				}
			} else if(isAttributeSign(symbol)) {
				attribute = new Attribute.AttributeIdCon(symbol);
			}  else {
				return; // Non attribute symbol found, quit attribute parsing
			}
			
			// Parse attribute
			visit(attribute);
			attributes.add(attribute);
			
			if(! tokens.hasNext() || ! tokens.peek(1).getLexeme().equals(WaebricSymbol.COMMA)) {
				break; // Cannot detect comma separator, quit parsing attributes
			} else {
				tokens.next(); // Skip parsing comma separator
			}
		}
	}
	
	/**
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isAttributeSign(char c) {
		return c == '#' || c == '.' || c == '$' || c == ':' || c == '@';
	}
	
	/**
	 * 
	 * @param attribute
	 */
	public void visit(Attribute attribute) {
		// Parse attribute symbol
		if(next("attribute symbol", "symbol", TokenSort.SYMBOL)) {
			if(! isAttributeSign(current.getLexeme().toString().charAt(0))) {
				exceptions.add(new UnexpectedTokenException(
						current, "attribute symbol", "symbols { # . $ : @ }"));
			}
		}
		
		// Parse name
		current = tokens.next();
		if(attribute instanceof AttributeIdCon) {
			// Identifier attribute
			IdCon identifier = new IdCon(current.getLexeme().toString());
			((AttributeIdCon) attribute).setIdentifier(identifier);
		} else if(attribute instanceof AttributeNatCon) {
			try {
				// Natural attribute
				NatCon number = new NatCon(current.getLexeme().toString());
				((AttributeNatCon) attribute).setNumber(number);
			} catch(NumberFormatException e) {
				exceptions.add(new UnexpectedTokenException(
						current, "numeral attribute", "\"@\" number"));
			}
		}
		
		// Double natural attribute
		if(attribute instanceof AttributeDoubleNatCon) {
			// Process second separator "%"
			next("double natural attribute separator", "%", "" + WaebricSymbol.PERCENT_SIGN);
			
			try {
				// Parse second value
				current = tokens.next();
				NatCon second = new NatCon(current.getLexeme().toString());
				((AttributeDoubleNatCon) attribute).setSecondNumber(second);
			} catch(NumberFormatException e) {
				exceptions.add(new UnexpectedTokenException(
						current, "numeral attribute", "\"@\" number \"%\" number"));
			}
		}
	}
	
	/**
	 * 
	 * @param arguments
	 */
	public void visit(Arguments arguments) {
		// Parse arguments opening token "("
		next("arguments opening paranthesis", "\"(\" arguments", "" + WaebricSymbol.LPARANTHESIS);
		
		while(tokens.hasNext()) {
			if(tokens.peek(1).getLexeme().equals(WaebricSymbol.RPARANTHESIS)) {
				break; // End of arguments reached, break while
			}
			
			Argument argument = null; // Determine argument type based on lookahead
			if(tokens.hasNext(2) && tokens.peek(2).getLexeme().equals(WaebricSymbol.EQUAL_SIGN)) {
				argument = new Argument.ArgumentWithVar();
			} else {
				argument = new Argument.ArgumentWithoutVar();
			}
			
			// Parse argument
			visit(argument);
			arguments.add(argument);
			
			// While not end of arguments, comma separator is expected
			if(tokens.hasNext() && ! tokens.peek(1).getLexeme().equals(WaebricSymbol.RPARANTHESIS)) {
				next("arguments separator", "argument \",\" argument", "" + WaebricSymbol.COMMA);
			}
		}
		
		// Parse arguments closing token ")"
		next("arguments closing paranthesis", "arguments \")\"", "" + WaebricSymbol.RPARANTHESIS);
	}
	
	public void visit(Argument argument) {
		if(argument instanceof Argument.ArgumentWithVar) {
			// Parse variable
			Var var = new Var();
			visit(var); 
			((Argument.ArgumentWithVar) argument).setVar(var);
			
			tokens.next(); // Skip equals sign
		}
		
		// Parse expression
		Expression expression = ExpressionParser.newExpression(tokens.peek(1));
		visit(expression);
		argument.setExpression(expression);
	}
	
	/**
	 * @see org.cwi.waebric.parser.ExpressionParser
	 * @param var
	 */
	public void visit(Var var) {
		expressionParser.visit(var);
	}
	
	/**
	 * @see org.cwi.waebric.parser.ExpressionParser
	 * @param expression
	 */
	public void visit(Expression expression) {
		expressionParser.visit(expression);
	}

}