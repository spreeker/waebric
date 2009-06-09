package org.cwi.waebric.checker;

import org.cwi.waebric.parser.ast.markup.Markup;

public class UndefinedFunctionException extends SemanticException {

	/**
	 * Generated serial ID
	 */
	private static final long serialVersionUID = -4467095005921534334L;

	public UndefinedFunctionException(Markup markup) {
		super(markup.toString() + " is an undefined function.");
	}
	
}
