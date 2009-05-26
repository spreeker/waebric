package org.cwi.waebric.parser.exception;

import org.cwi.waebric.scanner.token.Token;

public class UnexpectedTokenException extends ParserException {

	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = -3943077437342399851L;

	public UnexpectedTokenException(Token found, String name, String expected) {
		super("Unexpected token found: " + found.toString() + ". Was expecting a " + name +
				", use the expected (" + expected + ")."); 
	}
	
}