package org.cwi.waebric.scanner.exception;


public class UnknownTokenException extends ScannerException {

	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = 4799729281862409302L;

	/**
	 * 
	 * @param symbol
	 * @param line
	 */
	public UnknownTokenException(Object symbol, int line) {
		super("Cannot tokenize '" + symbol.toString() + "' at line " + line + ".");
	}

}