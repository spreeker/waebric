package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.StringLiteral;
import org.cwi.waebric.parser.ast.expressions.Expression;
import org.cwi.waebric.parser.ast.predicates.Predicate;
import org.cwi.waebric.parser.ast.predicates.Type;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.token.TokenIterator;

class PredicateParser extends AbstractParser {

	private final ExpressionParser expressionParser;
	
	public PredicateParser(TokenIterator tokens, List<ParserException> exceptions) {
		super(tokens, exceptions);
		
		// Construct sub parser
		expressionParser = new ExpressionParser(tokens, exceptions);
	}

	/**
	 * @see Predicate
	 * @return Predicate
	 */
	public Predicate parsePredicate() {
		// Parse expression
		Expression expression = parseExpression("predicate", "expression or expression \".\" type \"?\"");

		// Determine predicate type based on next token
		if(tokens.hasNext() && tokens.peek(1).getLexeme().equals(WaebricSymbol.PERIOD)) {
			Predicate.PredicateWithType predicate = new Predicate.PredicateWithType();
			predicate.setExpression(expression); // Store expression
			
			tokens.next(); // Skip period
			
			Type type = parseType(); // Parse type
			predicate.setType(type); // Store type
			
			next("predicate closure token \"?\"", "predicate \".\" type \"?\"", 
					WaebricSymbol.QUESTION_SIGN); // Parse question sign
			
			return predicate; // Return predicate with type
		} else {
			Predicate.PredicateWithoutType predicate = new Predicate.PredicateWithoutType();
			predicate.setExpression(expression); // Store expression
			return predicate; // Return predicate without type
		}
	}
	
	/**
	 * @see Type
	 * @return Type
	 */
	public Type parseType() {
		if(next(" predicate type definition", "predicate \".\" type \"?\"")) {
			final String lexeme = current.getLexeme().toString();
			if(lexeme.equals("list") || lexeme.equals("record") || lexeme.equals("string")) {
				Type type = new Type();
				type.setType(new StringLiteral(lexeme));
				return type;
			} else {
				reportUnexpectedToken(current, "type definition", "\"list\", \"record\" or \"string\"");
			}
		}
		
		return null;
	}
	
	/**
	 * @see Expression
	 * @param name
	 * @param syntax
	 * @return
	 */
	public Expression parseExpression(String name, String syntax) {
		// Delegate parse to expression parser
		return expressionParser.parseExpression(name, syntax);
	}
	
}