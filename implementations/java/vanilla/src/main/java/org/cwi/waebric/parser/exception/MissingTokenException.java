package org.cwi.waebric.parser.exception;

import org.cwi.waebric.lexer.token.Token;

/**
 * Missing token exceptions are thrown when the token stream ends prematurely.
 * 
 * @author Jeroen van Schagen
 * @date 05-06-2009
 */
public class MissingTokenException extends SyntaxException {

	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = 8718366239518956356L;

	/**
	 * 
	 * @param previous
	 * @param name
	 * @param formula
	 */
	public MissingTokenException(Token previous, String name, String formula) {
		super("Missing '" + name + "' token after: " + previous.toString() + ", " +
				"attach the expected: (" + formula + ").");
	}
	
	/**
	 * 
	 * @param name
	 * @param formula
	 */
	public MissingTokenException(String name, String formula) {
		super("Missing '" + name + "' token, attach the expected: (" + formula + ").");
	}
	
}