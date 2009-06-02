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
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.token.WaebricToken;
import org.cwi.waebric.scanner.token.WaebricTokenIterator;
import org.cwi.waebric.scanner.token.WaebricTokenSort;

/**
 * Markup
 * 
 * module languages/waebric/syntax/Markup
 * 
 * @author Jeroen van Schagen
 * @date 25-05-2009
 */
class MarkupParser extends AbstractParser {

	private final ExpressionParser expressionParser;
	
	public MarkupParser(WaebricTokenIterator tokens, List<ParserException> exceptions) {
		super(tokens, exceptions);
		
		// Construct sub parsers
		expressionParser = new ExpressionParser(tokens, exceptions);
	}
	
	/**
	 * @see Markup
	 * @param markup
	 */
	public Markup parseMarkup() {
		Markup markup = null;
		
		// Parse designator
		Designator designator = parseDesignator();
		
		// Parse arguments
		if(tokens.hasNext() && tokens.peek(1).getLexeme().equals(WaebricSymbol.LPARANTHESIS)) {
			Arguments arguments = parseArguments();
			markup = new Markup.MarkupWithArguments(arguments);
		} else {
			markup = new Markup.MarkupWithoutArguments();
		}

		// Store designator
		markup.setDesignator(designator);
		
		return markup;
	}
	
	/**
	 * @see Designator
	 * @param designator
	 */
	public Designator parseDesignator() {
		Designator designator = new Designator();
		
		// Parse identifier
		if(next("designator identifier", "identifier", WaebricTokenSort.IDCON)) {
			IdCon identifier = new IdCon(current.getLexeme().toString());
			designator.setIdentifier(identifier);
		} else {
			return null; // Faulty parse, return empty node
		}
		
		// Parse attributes
		Attributes attributes = parseAttributes();
		designator.setAttributes(attributes);
		
		return designator;
	}
	
	/**
	 * @see Attributes
	 * @param attributes
	 */
	public Attributes parseAttributes() {
		Attributes attributes = new Attributes();
		
		while(tokens.hasNext()) {
			// Parse attribute
			Attribute attribute = parseAttribute();
			attributes.add(attribute);
			
			// Look-ahead for separator
			if(tokens.hasNext() && tokens.peek(1).getLexeme().equals(WaebricSymbol.COMMA)) {
				tokens.next(); // Continue parsing and skip comma symbol
			} else {
				break; // Quit parsing attributes
			}
		}
		
		return attributes;
	}

	/**
	 * @see Attribute
	 * @param attribute
	 */
	public Attribute parseAttribute() {
		if(tokens.hasNext() && tokens.peek(1).getSort() == WaebricTokenSort.CHARACTER) {
			WaebricToken peek = tokens.peek(1); // Retrieve symbol token
			char c = peek.getLexeme().toString().charAt(0);
			if(c == '#' || c == '.' || c == '$' || c ==':') { // Identifier attribute
				tokens.next(); // Skip attribute symbol
				Attribute.AttributeIdCon attribute = new Attribute.AttributeIdCon(c);
				
				if(next("identifier attribute", "symbol identifier", WaebricTokenSort.IDCON)) {
					IdCon identifier = new IdCon(current.getLexeme().toString());
					attribute.setIdentifier(identifier);
					return attribute;
				}
				
				return null; // Failed parse, return empty node
			} else if(c == '@') { // Natural attribute
				tokens.next(); // Skip attribute symbol
				if(next("numeral attribute", "@ number", WaebricTokenSort.NATCON)) {
					NatCon number = new NatCon(current.getLexeme().toString());
					
					// Double natural attribute
					if(tokens.hasNext() && tokens.peek(1).getLexeme().equals(WaebricSymbol.PERCENT_SIGN)) {
						tokens.next(); // Skip percent sign
						if(next("second numeral attribute", "number % number", WaebricTokenSort.NATCON)) {
							NatCon second = new NatCon(current.getLexeme().toString());
							Attribute.AttributeDoubleNatCon attribute = new Attribute.AttributeDoubleNatCon();
							attribute.setNumber(number);
							attribute.setSecondNumber(second);
							return attribute;
						}
					}
					
					// Regular natural attribute
					Attribute.AttributeNatCon attribute = new Attribute.AttributeNatCon();
					attribute.setNumber(number);
					return attribute;
				} else {
					return null; // Failed parse, return empty node
				}
			}
		}

		return null; // Return empty node, as no valid attribute was found
	}

	/**
	 * @see Arguments
	 * @param arguments
	 */
	public Arguments parseArguments() {
		Arguments arguments = new Arguments();
		
		// Parse arguments opening token "("
		if(! next("arguments opening paranthesis", "\"(\" arguments", WaebricSymbol.LPARANTHESIS)) {
			return null; // Incorrect arguments syntax, quit argument parse
		}
		
		while(tokens.hasNext()) {
			if(tokens.peek(1).getLexeme().equals(WaebricSymbol.RPARANTHESIS)) {
				break; // End of arguments reached, break while
			}
			
			// Parse argument
			Argument argument = parseArgument();
			arguments.add(argument);
			
			// While not end of arguments, comma separator is expected
			if(tokens.hasNext() && ! tokens.peek(1).getLexeme().equals(WaebricSymbol.RPARANTHESIS)) {
				next("arguments separator", "argument \",\" argument", WaebricSymbol.COMMA);
			}
		}
		
		// Parse arguments closing token ")"
		if(! next("arguments closing paranthesis", "arguments \")\"", WaebricSymbol.RPARANTHESIS)) {
			return null; // Incorrect arguments syntax, return empty node
		}
		
		return arguments;
	}
	
	/**
	 * @see Argument
	 * @param argument
	 */
	public Argument parseArgument() {
		Argument argument = null;
		
		if(tokens.hasNext(2) && tokens.peek(2).getLexeme().equals(WaebricSymbol.EQUAL_SIGN)) {
			Var var = parseVar("", "");
			argument = new Argument.ArgumentWithVar(var);
			tokens.next(); // Skip equals sign
		} else {
			argument = new Argument.ArgumentWithoutVar();
		}
		
		// Parse expression
		Expression expression = parseExpression("argument", "var \"=\" expression");
		argument.setExpression(expression);
		
		return argument;
	}
	
	/**
	 * @see Var
	 * @see org.cwi.waebric.parser.ExpressionParser
	 * @param var
	 */
	public Var parseVar(String name, String syntax) {
		return expressionParser.parseVar(name, syntax);
	}
	
	/**
	 * @see Expression
	 * @see org.cwi.waebric.parser.ExpressionParser
	 * @return expression
	 */
	public Expression parseExpression(String name, String syntax) {
		return expressionParser.parseExpression(name, syntax);
	}

}