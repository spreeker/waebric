package org.cwi.waebric.scanner.processor;

/**
 * Lexical are exceptions that occurred during reading of the
 * character stream(s).
 * 
 * @author Jeroen van Schagen
 * @date 05-06-2009
 */
public abstract class LexicalException extends Exception {

	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = -3356430733017825483L;

	/**
	 * Construct exception
	 * 
	 * @param msg Message
	 */
	public LexicalException(String msg) {
		super(msg);
	}
	
}