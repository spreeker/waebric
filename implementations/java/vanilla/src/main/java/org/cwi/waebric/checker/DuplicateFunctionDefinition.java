package org.cwi.waebric.checker;

import org.cwi.waebric.parser.ast.module.function.FunctionDef;


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
		super("Function \"" + def.getIdentifier().getToken().getLexeme().toString()
				+ "\" at line " + def.getIdentifier().getToken().getLine()
				+ " has a duplicate definition.");
	}
	
}