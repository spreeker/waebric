package org.cwi.waebric.checker;

import org.cwi.waebric.parser.ast.expression.Var;

public class UndefinedVariableException extends SemanticException {

	/**
	 * Generated serial ID
	 */
	private static final long serialVersionUID = 3043727441105977011L;

	public UndefinedVariableException(Var var) {
		super(var.toString() + " is not defined.");
	}
	
}
