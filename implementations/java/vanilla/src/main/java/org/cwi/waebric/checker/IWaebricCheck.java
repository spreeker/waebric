package org.cwi.waebric.checker;

import java.util.List;

import org.cwi.waebric.parser.ast.module.Modules;

/**
 * Checks are components of the Waebric checker, which each check
 * various parts of the abstract syntax tree for semantic violations.
 * @author Jeroen van Schagen
 * @date 09-06-2009
 */
public interface IWaebricCheck {

	/**
	 * Check abstract syntax tree for semantic violations.
	 * @param modules Root of abstract syntax tree
	 * @param exceptions Collection of semantic violations
	 */
	public void checkAST(Modules modules, List<SemanticException> exceptions);
	
}
