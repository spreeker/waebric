package org.cwi.waebric.checker;

import org.cwi.waebric.parser.ast.module.ModuleId;

/**
 * If for an import directive import m no corresponding file m.wae 
 * can be found, this a an error. [The import directive is skipped]
 * 
 * @author Jeroen van Schagen
 * @date 09-06-2009
 */
public class NonExistingModuleException extends SemanticException {

	/**
	 * Generated serial ID
	 */
	private static final long serialVersionUID = -4503945323554024642L;

	public NonExistingModuleException(ModuleId id) {
		super("Module identifier \"" + id.toString() 
				+ "\" at line " + id.get(0).getToken().getLine()
				+ ", refers to a non-existing module.");
	}
	
}