package org.cwi.waebric.checker.exception;

/**
 * Apart from syntactic correctness, a WAEBRIC implementation also 
 * performs modest semantic correctness checks. The interpreter and 
 * compiler, however, will always produce output, regardless of errors.
 * 
 * @author Jeroen van Schagen
 * @date 09-06-2009
 */
public abstract class SemanticException extends Exception {

	/**
	 * Generated serial ID
	 */
	private static final long serialVersionUID = -8925365158130849611L;
	
	/**
	 * Construct exception based on message.
	 * @param msg
	 */
	public SemanticException(String msg) {
		super(msg);
	}

}