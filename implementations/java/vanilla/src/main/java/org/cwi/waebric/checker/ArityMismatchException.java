package org.cwi.waebric.checker;

import org.cwi.waebric.parser.ast.markup.Markup;

/**
 * If a function is called with an incorrect number of arguments 
 * (as follows from its definition), this is an error.
 * 
 * @author Jeroen van Schagen
 * @date 09-06-2009
 */
public class ArityMismatchException extends SemanticException {

	/**
	 * Generated serial ID
	 */
	private static final long serialVersionUID = -954167103131401047L;

	public ArityMismatchException(Markup.Call call) {
		super("Call \"" + call.getDesignator().getIdentifier().getToken().getLexeme().toString()
				+ "\" at line " + call.getDesignator().getIdentifier().getToken().getLine()
				+ ", is an arity mismatch.");
	}
	
}