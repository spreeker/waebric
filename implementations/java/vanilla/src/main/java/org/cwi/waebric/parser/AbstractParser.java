package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.scanner.token.Token;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.cwi.waebric.scanner.token.TokenSort;
import org.cwi.waebric.parser.exception.MissingTokenException;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.parser.exception.UnexpectedTokenException;

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
	
	/**
	 * Retrieve next token from iterator.
	 * 
	 * @throws MissingTokenException Thrown when no more tokens are available
	 * 
	 * @param name Name of expected token, used for error reporting
	 * @param expected Grammar of expected token, used for error reporting
	 * @return Contains next
	 */
	protected boolean next(String name, String expected) {
		if(tokens.hasNext()) {
			current = tokens.next();
			return true;
		} else {
			exceptions.add(new MissingTokenException(current, name, expected));
			return false;
		}
	}
	
	/**
	 * Retrieve next token from iterator and check sort.
	 * 
	 * @throws MissingTokenException Thrown when no more tokens are available
	 * @throws UnexpectedTokenException Thrown when next token does not match expected sort
	 * 
	 * @param name Name of expected token, used for error reporting
	 * @param expected Grammar of expected token, used for error reporting
	 * @param sort Expected token sort, used for type checking
	 * @return Contains next
	 */
	protected boolean next(String name, String expected, TokenSort sort) {
		if(next(name, expected)) {
			if(current.getSort() == sort) {
				return true; // Correct token
			} else {
				exceptions.add(new UnexpectedTokenException(current, name, expected));
			}
		}
		
		return false;
	}
	
	/**
	 * Retrieve next token from iterator and check lexeme.
	 * 
	 * @throws MissingTokenException Thrown when no more tokens are available
	 * @throws UnexpectedTokenException Thrown when next token does not match expected lexeme
	 * 
	 * @param name Name of expected token, used for error reporting
	 * @param expected Grammar of expected token, used for error reporting
	 * @param lexeme Expected token lexeme, used for type checking
	 * @return Contains next
	 */
	protected boolean next(String name, String expected, String lexeme) {
		if(next(name, expected)) {
			if(current.getLexeme().toString().equals(lexeme)) {
				return true; // Correct token
			} else {
				exceptions.add(new UnexpectedTokenException(current, name, expected));
			}
		}
		
		return false;
	}
	
}