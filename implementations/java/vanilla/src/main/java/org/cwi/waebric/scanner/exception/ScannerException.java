package org.cwi.waebric.scanner.exception;

public abstract class ScannerException extends Exception {

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
	
}