package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.basic.NatCon;
import org.cwi.waebric.parser.ast.markup.Argument;
import org.cwi.waebric.parser.ast.markup.Arguments;
import org.cwi.waebric.parser.ast.markup.Attribute;
import org.cwi.waebric.parser.ast.markup.Attributes;
import org.cwi.waebric.parser.ast.markup.Designator;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.exception.SyntaxException;
import org.cwi.waebric.scanner.token.WaebricToken;
import org.cwi.waebric.scanner.token.WaebricTokenIterator;
import org.cwi.waebric.scanner.token.WaebricTokenSort;

/**
 * module languages/waebric/syntax/Markup
 * 
 * @author Jeroen van Schagen
 * @date 25-05-2009
 */
class MarkupParser extends AbstractParser {

	private final ExpressionParser expressionParser;
	
	public MarkupParser(WaebricTokenIterator tokens, List<SyntaxException> exceptions) {
		super(tokens, exceptions);
		
		// Construct sub parsers
		expressionParser = new ExpressionParser(tokens, exceptions);
	}
	
	/**
	 * @throws SyntaxException 
	 * @see Markup
	 */
	public Markup parseMarkup() throws SyntaxException {
		// Parse designator
		Designator designator = parseDesignator();
		
		// Determine mark-up type
		Markup markup = null;
		if(tokens.hasNext() && tokens.peek(1).getLexeme().equals(WaebricSymbol.LPARANTHESIS)) {
			Arguments arguments = parseArguments();
			markup = new Markup.Call(arguments);
		} else {
			markup = new Markup.Tag();
		}

		// Store designator
		markup.setDesignator(designator);
		return markup;
	}
	
	/**
	 * @see Designator
	 * @throws SyntaxException 
	 */
	public Designator parseDesignator() throws SyntaxException {
		Designator designator = new Designator();
		
		// Parse identifier
		next(WaebricTokenSort.IDCON, "designator identifier", "identifier");
		designator.setIdentifier(new IdCon(tokens.current().getLexeme().toString()));
		
		// Parse attributes
		designator.setAttributes(parseAttributes());
		
		return designator;
	}
	
	/**
	 * @see Attributes
	 * @throws SyntaxException 
	 */
	public Attributes parseAttributes() throws SyntaxException {
		Attributes attributes = new Attributes();
		
		while(tokens.hasNext() && isAttribute(tokens.peek(1))) {
			attributes.add(parseAttribute());
			
			// Look-ahead for separator
			if(tokens.hasNext() && tokens.peek(1).getLexeme().equals(WaebricSymbol.COMMA)) {
				tokens.next(); // Continue parsing and skip comma symbol
			} else {
				break; // Quit parsing attributes
			}
		}
		
		return attributes;
	}
	
	public static boolean isAttribute(WaebricToken token) {
		if(token.getSort() != WaebricTokenSort.CHARACTER) { return false; }
		char symbol = token.getLexeme().toString().charAt(0);
		return symbol == '#' || symbol == '.' || symbol == '$' || symbol ==':' || symbol == '@';
	}

	/**
	 * @see Attribute
	 * @throws SyntaxException 
	 */
	public Attribute parseAttribute() throws SyntaxException {
		next(WaebricTokenSort.CHARACTER, "Attribute", "{ # . $ : @ } Identifier");
		char symbol = tokens.current().getLexeme().toString().charAt(0);
		
		if(symbol == '#' || symbol == '.' || symbol == '$' || symbol ==':') { // Identifier attribute
			next(WaebricTokenSort.IDCON, "Identifier attribute", "{ # , $ : } Identifier");
			Attribute.AttributeIdCon attribute = new Attribute.AttributeIdCon(symbol);
			attribute.setIdentifier(new IdCon(tokens.current().getLexeme().toString()));
			return attribute;
		} else if(symbol == '@') { // Natural attribute
			next(WaebricTokenSort.NATCON, "Natural attribute", "@ Number");
			NatCon number = new NatCon(tokens.current().getLexeme().toString());
			
			// Look-ahead for % (Double natural attribute)
			if(tokens.hasNext() && tokens.peek(1).getLexeme().equals(WaebricSymbol.PERCENT_SIGN)) {
				tokens.next(); // Skip percent sign
				next(WaebricTokenSort.NATCON, "Natural attribute", "@ Number % Number");
				NatCon second = new NatCon(tokens.current().getLexeme().toString());

				Attribute.AttributeDoubleNatCon attribute = new Attribute.AttributeDoubleNatCon();
				attribute.setNumber(number);
				attribute.setSecondNumber(second);
				return attribute;
			} else { // Regular natural attribute
				Attribute.AttributeNatCon attribute = new Attribute.AttributeNatCon();
				attribute.setNumber(number);
				return attribute;
			}
		} else {
			reportUnexpectedToken(tokens.current(), "Attribute symbol", "@ . : $ #");
		}

		return null; // Return empty node, as no valid attribute was found
	}

	/**
	 * @throws SyntaxException 
	 * @see Arguments
	 */
	public Arguments parseArguments() throws SyntaxException {
		next(WaebricSymbol.LPARANTHESIS, "Argument opening \"(\"", "\"(\" Arguments \")\"");
		
		Arguments arguments = new Arguments();
		while(tokens.hasNext()) {
			if(tokens.peek(1).getLexeme().equals(WaebricSymbol.RPARANTHESIS)) {
				break; // End of arguments reached, break while
			}
			
			// Parse argument
			arguments.add(parseArgument());
			
			// While not end of arguments, comma separator is expected
			if(tokens.hasNext() && ! tokens.peek(1).getLexeme().equals(WaebricSymbol.RPARANTHESIS)) {
				next(WaebricSymbol.COMMA, "Arguments separator", "Argument \",\" Argument");
			}
		}
		
		next(WaebricSymbol.RPARANTHESIS, "Argument closure \")\"", "\"(\" Arguments \")\"");
		return arguments;
	}
	
	/**
	 * @see Argument
	 * @throws SyntaxException 
	 */
	public Argument parseArgument() throws SyntaxException {
		Argument argument = null;
		
		if(tokens.hasNext(2) && tokens.peek(2).getLexeme().equals(WaebricSymbol.EQUAL_SIGN)) {
			// Argument with variable recognised (=)
			next(WaebricTokenSort.IDCON, "Attr identifier", "IdCon \"=\" Expression");
			argument = new Argument.Attr(new IdCon(tokens.current().getLexeme().toString()));
			tokens.next(); // Skip equals sign
		} else {
			// Regular expression-based argument
			argument = new Argument.Regular();
		}
		
		// Parse expression
		argument.setExpression(expressionParser.parseExpression());
		return argument;
	}

}