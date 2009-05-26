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
	 * Created to prevent code overflows in parse functionality
	 * @param name
	 * @param sort
	 * @return
	 */
	protected boolean next(String name, String expected, TokenSort sort) {
		if(tokens.hasNext()) {
			current = tokens.next();
			if(current.getSort() == sort) {
				return true; // Successfully recognized token
			} else {
				exceptions.add(new UnexpectedTokenException(current, name, expected));
			}
		} else {
			exceptions.add(new MissingTokenException(current, name, expected));
		}
		
		return false;
	}
	
	/**
	 * Created to prevent code overflows in parse functionality
	 * @param name
	 * @param lexeme
	 * @return
	 */
	protected boolean next(String name, String expected, String lexeme) {
		if(tokens.hasNext()) {
			current = tokens.next();
			if(current.getLexeme().toString().equals(lexeme)) {
				return true; // Successfully recognized token
			} else {
				exceptions.add(new UnexpectedTokenException(current, name, expected));
			}
		} else {
			exceptions.add(new MissingTokenException(current, name, expected));
		}
		
		return false;
	}
	
}