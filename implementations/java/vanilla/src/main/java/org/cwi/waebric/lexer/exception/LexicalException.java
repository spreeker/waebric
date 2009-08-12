package org.cwi.waebric.lexer.exception;

/**
 * Lexical exceptions are created during the scan phase.
 * @author Jeroen van Schagen
 * @date 23-06-2009
 */
public class LexicalException extends Exception {

	/**
	 * Generated ID
	 */
	private static final long serialVersionUID = 4401248624083268103L;
	
	public LexicalException(String message) {
		super(message);
	}
	
}