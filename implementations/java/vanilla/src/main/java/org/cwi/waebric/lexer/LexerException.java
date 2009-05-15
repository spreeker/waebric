package org.cwi.waebric.lexer;

public class LexerException extends Exception {

	private static final long serialVersionUID = -4788615799929127224L;

	public LexerException(String symbol, int index) {
		super("Invalid symbol '" + symbol + "' (index: " + index + ").");
	}
	
}