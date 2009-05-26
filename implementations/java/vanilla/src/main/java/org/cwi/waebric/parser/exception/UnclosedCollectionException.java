package org.cwi.waebric.parser.exception;

import org.cwi.waebric.scanner.token.Token;

public class UnclosedCollectionException extends ParserException {

	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = -5101751455895996992L;

	public UnclosedCollectionException(Token last, String name, String expected) {
		super("An unclosed " + name + " collection was found at " + last.getLine() + ", use " + expected);
	}
	
}
