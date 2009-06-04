package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.StringLiteral;
import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.statement.predicate.Predicate;
import org.cwi.waebric.parser.ast.statement.predicate.Type;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.token.WaebricTokenIterator;

class PredicateParser extends AbstractParser {

	private final ExpressionParser expressionParser;
	
	public PredicateParser(WaebricTokenIterator tokens, List<ParserException> exceptions) {
		super(tokens, exceptions);
		
//		if(tokens.current() == null && tokens.hasNext()) {
//			tokens.next(); // Iterate to first element
//		}
		
		// Construct sub parser
		expressionParser = new ExpressionParser(tokens, exceptions);
	}

	/**
	 * @see Predicate
	 * @return Predicate
	 */
	public Predicate parsePredicate() {
		Predicate predicate;
		
		if(tokens.hasNext() && tokens.peek(1).getLexeme().equals(WaebricSymbol.EXCLAMATION_SIGN)) {
			// Parse "!" predicates
			tokens.next(); // Accept "!" and move to next token
			Predicate.NotPredicate notpredicate = new Predicate.NotPredicate();
			notpredicate.setPredicate(parsePredicate()); // Parse sub-predicate
			predicate = notpredicate;
		} else {
			// Parse expression based predicates
			Expression expression = parseExpression("predicate", "expression or expression \".\" type \"?\"");
			
			// Determine predicate type based on lookahead
			if(tokens.hasNext() && tokens.peek(1).getLexeme().equals(WaebricSymbol.PERIOD)) {
				Predicate.ExpressionTypePredicate exppredicate = new Predicate.ExpressionTypePredicate();
				exppredicate.setExpression(expression);
				tokens.next(); // Accept "." and move to next token
				exppredicate.setType(parseType()); // Parse type
				next("type predicate closure symbol \"?\"", "type \"?\"", WaebricSymbol.QUESTION_SIGN);
				predicate = exppredicate;
			} else {
				Predicate.ExpressionPredicate exptpredicate = new Predicate.ExpressionPredicate();
				exptpredicate.setExpression(expression); // Store expression
				predicate = exptpredicate; // Return predicate without type
			}
		}
		
		if(tokens.hasNext(2) && tokens.peek(1).getLexeme().equals(WaebricSymbol.AMPERSAND) && tokens.peek(2).getLexeme().equals(WaebricSymbol.AMPERSAND)) {
			Predicate.AndPredicate andpredicate = new Predicate.AndPredicate();
			tokens.next(); tokens.next(); // Accept '&&' tokens and jump to next predicate
			andpredicate.setLeft(predicate);
			andpredicate.setRight(parsePredicate());
			return andpredicate;
		} else if(tokens.hasNext() && tokens.peek(1).getLexeme().equals(WaebricSymbol.VERTICAL_BAR) && tokens.peek(2).getLexeme().equals(WaebricSymbol.VERTICAL_BAR)) {
			Predicate.OrPredicate orpredicate = new Predicate.OrPredicate();
			tokens.next(); tokens.next(); // Accept '||' tokens and jump to next predicate
			orpredicate.setLeft(predicate);
			orpredicate.setRight(parsePredicate());
			return orpredicate;
		}

		return predicate;
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