package org.cwi.waebric.scanner;

public class ScannerException extends Exception {

	private static final long serialVersionUID = -4788615799929127224L;

	public ScannerException(Object symbol, int line, Throwable t) {
		super("Invalid symbol '" + symbol.toString() + "' (line: " + line+ ").", t);
	}
	
}