package org.cwi.waebric.parser;

/**
 * Syntax exceptions are thrown when a token stream does not match
 * the language grammar.
 * 
 * @author Jeroen van Schagen
 * @date 05-06-2009
 */
public abstract class SyntaxException extends Exception {

	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = -4642489292667119148L;

	/**
	 * Construct exception based on message.
	 * @param message
	 */
	public SyntaxException(String message) {
		super(message);
	}

}