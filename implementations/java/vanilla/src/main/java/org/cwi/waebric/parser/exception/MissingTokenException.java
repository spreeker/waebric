package org.cwi.waebric.parser.exception;

import org.cwi.waebric.scanner.token.Token;

public class MissingTokenException extends ParserException {

	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = 8718366239518956356L;

	public MissingTokenException(Token previous, String name, String expected) {
		super(name + " is missing after " + previous.toString() + ", attach the expected: " + expected);
	}
	
}