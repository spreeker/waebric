package org.cwi.waebric.parser.exception;

import org.cwi.waebric.lexer.token.Token;

/**
 * Unexpected token exceptions are thrown when a coming
 * token does not match the expected token.
 * 
 * @author Jeroen van Schagen
 * @date 05-06-2009
 */
public class UnexpectedTokenException extends SyntaxException {

	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = -3943077437342399851L;

	public UnexpectedTokenException(Token actual, String expected, String syntax) {
		super("Unexpected token found: " + actual.toString() + ". Was expecting a " + expected +
				", follow the specified syntax (" + syntax + ")."); 
	}
	
}