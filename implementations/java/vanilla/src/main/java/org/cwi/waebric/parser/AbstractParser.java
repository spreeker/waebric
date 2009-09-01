package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.lexer.token.Token;
import org.cwi.waebric.lexer.token.TokenIterator;
import org.cwi.waebric.lexer.token.WaebricTokenSort;

/**
 * Abstract parser stores token stream and exceptions. Based on these fields
 * token comparison and iterating functionality is provided.
 * 
 * @author Jeroen van Schagen
 * @data 19-05-2009
 */
public abstract class AbstractParser {

	/**
	 * Syntax exceptions
	 */
	protected final List<SyntaxException> exceptions;
	
	/**
	 * Token stream
	 */
	protected final TokenIterator tokens;
	
	/**
	 * Construct abstract parser.
	 * @param tokens
	 * @param exceptions
	 */
	public AbstractParser(TokenIterator tokens, List<SyntaxException> exceptions) {
		this.exceptions = exceptions;
		this.tokens = tokens;
	}
	
	/**
	 * Compare current token to expected lexeme, when no current token is active
	 * retrieve first token from stream.
	 * @param lexeme Expected token lexeme.
	 * @param name Name of expected token, used for error reporting.
	 * @param syntax Syntax of expected token, used for error reporting.
	 * @throws MissingTokenException Token stream is empty, thus no current
	 * @throws UnexpectedTokenException Unexpected token is found
	 */
	protected void current(Object lexeme, String name, String syntax) throws SyntaxException {
		if(tokens.current() == null) { next(lexeme, name, syntax); }
		else { expect(tokens.current(), lexeme, name, syntax); }
	}
	
	/**
	 * Compare current token to expected sort, when no current token is active
	 * retrieve first token from stream.
	 * @param sort Expected token sort
	 * @param name Name of expected token, used for error reporting
	 * @param syntax Syntax of expected token, used for error reporting
	 * @throws MissingTokenException Token stream is empty, thus no current
	 * @throws UnexpectedTokenException Unexpected token is found
	 */
	protected void current(WaebricTokenSort sort, String name, String syntax) throws SyntaxException {
		if(tokens.current() == null) { next(sort, name, syntax); }
		else { expect(tokens.current(), sort, name, syntax); }
	}
	
	/**
	 * Check if token matches expected lexeme.
	 * @param token Token to verify
	 * @param lexeme Expected token lexeme
	 * @param name Name of expected token, used for error reporting
	 * @param syntax Syntax of expected token, used for error reporting
	 * @throws UnexpectedTokenException Unexpected token is found
	 */
	protected void expect(Token token, Object lexeme, 
			String name, String syntax) throws UnexpectedTokenException {
		if(! token.getLexeme().equals(lexeme)) {
			reportUnexpectedToken(token, name, syntax);
		}
	}
	
	/**
	 * Check if token matches expected sort.
	 * @param token Token to verify
	 * @param sort Expected token sort
	 * @param name Name of expected token
	 * @param syntax Syntax of expected token, used for error reporting
	 * @throws UnexpectedTokenException Unexpected token is found
	 */
	protected void expect(Token token, WaebricTokenSort sort, String name, String syntax) 
			throws UnexpectedTokenException {
		if(token.getSort() != sort) {
			reportUnexpectedToken(token, name, syntax);
		}
	}
	
	/**
	 * Retrieve next token from iterator and check sort.
	 * @param lexeme Expected token sort
	 * @param name Name of expected token, used for error reporting
	 * @param syntax Syntax of expected token, used for error reporting
	 * @throws MissingTokenException No next token available
	 * @throws UnexpectedTokenException Unexpected token is found
	 */
	protected void next(Object lexeme, String name, String syntax) throws SyntaxException {
		next(name, syntax);
		expect(tokens.current(), lexeme, name, syntax);
	}
	
	/**
	 * Retrieve next token from iterator.
	 * @param name Name of expected token, used for error reporting
	 * @param syntax Syntax of expected token, used for error reporting
	 * @throws MissingTokenException No next token available
	 */
	protected void next(String name, String syntax) throws MissingTokenException {
		if(tokens.hasNext()) { tokens.next(); } 
		else { reportMissingToken(tokens.current(), name, syntax); }
	}
	
	/**
	 * Retrieve next token from iterator and check sort.
	 * @param sort Expected token sort
	 * @param name Name of expected token, used for error reporting
	 * @param syntax Syntax of expected token, used for error reporting
	 * @throws MissingTokenException No next token available
	 * @throws UnexpectedTokenException Unexpected token is found
	 */
	protected void next(WaebricTokenSort sort, String name, String syntax) throws SyntaxException {
		next(name, syntax);
		expect(tokens.current(), sort, name, syntax);
	}
	
	/**
	 * Report missing token, exception is stored and thrown.
	 * @param previous
	 * @param name
	 * @param syntax
	 * @throws MissingTokenException
	 */
	protected void reportMissingToken(Token previous, String name, String syntax)
			throws MissingTokenException {
		MissingTokenException e;
		if(previous == null) {
			e = new MissingTokenException(name, syntax);
		} else {
			e = new MissingTokenException(previous, name, syntax);
		}
		
		exceptions.add(e); // Store exception
		throw e; // Throw exception
	}
	
	/**
	 * Report unexpected token, exception is stored and thrown.
	 * @param token
	 * @param name
	 * @param syntax
	 * @throws UnexpectedTokenException
	 */
	protected void reportUnexpectedToken(Token token, String name, String syntax) 
			throws UnexpectedTokenException {
		UnexpectedTokenException e = new UnexpectedTokenException(token, name, syntax);
		exceptions.add(e); // Store exception
		throw e; // Throw exception
	}

}