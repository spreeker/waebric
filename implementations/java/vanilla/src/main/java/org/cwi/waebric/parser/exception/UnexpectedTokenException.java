package org.cwi.waebric.parser.exception;

import org.cwi.waebric.scanner.token.Token;

public class UnexpectedTokenException extends ParserException {

	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = -3943077437342399851L;

	public UnexpectedTokenException(Token found, String name,String expected) {
		super(found.getLexeme() + " at line " + found.getLine() + " is not a valid " + 
				name + ", " + expected + " was expected.");
	}
	
}