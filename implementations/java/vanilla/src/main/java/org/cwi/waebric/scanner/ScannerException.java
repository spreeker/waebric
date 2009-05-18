package org.cwi.waebric.scanner;

public class ScannerException extends Exception {

	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = -4788615799929127224L;

	/**
	 * 
	 * @param message
	 */
	public ScannerException(String message) {
		super(message);
	}
	
	/**
	 * 
	 * @param symbol
	 * @param line
	 * @param t
	 */
	public ScannerException(Object symbol, int line, Throwable t) {
		super("A problem occured while scanning '" + symbol.toString() + "', line " + line+ ".", t);
	}
	
}