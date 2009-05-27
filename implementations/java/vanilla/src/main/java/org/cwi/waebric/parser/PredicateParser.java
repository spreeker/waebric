package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.parser.ast.predicates.Predicate;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.token.Token;
import org.cwi.waebric.scanner.token.TokenIterator;

public class PredicateParser extends AbstractParser {

	private final ExpressionParser expressionParser;
	
	public PredicateParser(TokenIterator tokens, List<ParserException> exceptions) {
		super(tokens, exceptions);
		
		// Construct sub parser
		expressionParser = new ExpressionParser(tokens, exceptions);
	}

	/**
	 * Recognise and construct predicate sort based on look-ahead information.
	 * 
	 * @param previous Previous token
	 * @param expected Expected syntax
	 * @return Predicate
	 */
	public Predicate parsePredicate(Token previous, String expected) {
		return null;
	}
	
}
