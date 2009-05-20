package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.scanner.token.Token;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.cwi.waebric.parser.exception.ParserException;

/**
 * Parser abstraction, which lays the foundation for all
 * concrete (sub) parser implementations.
 * 
 * @author Jeroen van Schagen
 * @data 19-05-2009
 */
public abstract class AbstractParser {

	protected final List<ParserException> exceptions;
	protected final TokenIterator tokens;
	protected Token current;
	
	public AbstractParser(TokenIterator tokens, List<ParserException> exceptions) {
		this.exceptions = exceptions;
		this.tokens = tokens;
	}
	
}