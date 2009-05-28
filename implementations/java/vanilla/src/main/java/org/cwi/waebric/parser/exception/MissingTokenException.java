package org.cwi.waebric.parser.exception;

import org.cwi.waebric.scanner.token.Token;

public class MissingTokenException extends ParserException {

	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = 8718366239518956356L;

	public MissingTokenException(Token previous, String name, String formula) {
		super("Missing '" + name + "' token after: " + previous.toString() + ", " +
				"attach the expected: (" + formula + ").");
	}
	
	public MissingTokenException(String name, String formula) {
		super("Missing '" + name + "' token, attach the expected: (" + formula + ").");
	}
	
}