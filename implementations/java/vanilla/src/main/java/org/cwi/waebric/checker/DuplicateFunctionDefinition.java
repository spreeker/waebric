package org.cwi.waebric.checker;

import org.cwi.waebric.parser.ast.module.FunctionDef;

/**
 * Multiple function definitions with the same name are disallowed.
 * 
 * @author Jeroen van Schagen
 * @date 09-06-2009
 */
public class DuplicateFunctionDefinition extends SemanticException {

	/**
	 * Generated serial ID
	 */
	private static final long serialVersionUID = -8833578229100261366L;

	public DuplicateFunctionDefinition(FunctionDef def) {
		super(def.toString() + " is a duplicate function definition");
	}
	
}
