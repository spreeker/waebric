package org.cwi.waebric.checker;

import org.cwi.waebric.parser.ast.markup.Markup;

/**
 * If for a markup-call (f) no function definition can be found in 
 * the current module or one of its (transitive) imports, and, if 
 * f is not a tag defined in the XHTML 1.0 Transitional standard, 
 * then this is an error. [f will be interpreted as if it was part 
 * of XHTML 1.0 transitional.]
 * 
 * @author Jeroen van Schagen
 * @date 09-06-2009
 */
public class UndefinedFunctionException extends SemanticException {

	/**
	 * Generated serial ID
	 */
	private static final long serialVersionUID = -4467095005921534334L;

	public UndefinedFunctionException(Markup.Call call) {
		super("Call \"" + call.getDesignator().getIdentifier().getToken().getLexeme().toString()
				+ "\" at line " + call.getDesignator().getIdentifier().getToken().getLine()
				+ ", is made to an undefined function.");
	}
	
}