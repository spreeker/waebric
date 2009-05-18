package org.cwi.waebric.scanner;

public class ScannerException extends Exception {

	private static final long serialVersionUID = -4788615799929127224L;

	public ScannerException(String symbol, int line, int chr) {
		super("Invalid symbol '" + symbol + "' (line: " + line + ", character: " + chr + " ).");
	}
	
}