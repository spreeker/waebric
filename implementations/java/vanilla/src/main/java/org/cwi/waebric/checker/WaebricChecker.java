package org.cwi.waebric.checker;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.AbstractSyntaxTree;

/**
 * Verify the semantics of an abstract syntax tree.
 * 
 * @author Jeroen van Schagen
 * @date 09-06-2009
 */
public class WaebricChecker {
	
	/**
	 * Checker components.
	 */
	private final List<IWaebricCheck> checks;
	
	/**
	 * Construct checker.
	 * @param ast Abstract syntax tree
	 */
	public WaebricChecker() {
		this.checks = new ArrayList<IWaebricCheck>();
		
		// Module check should be executed first, as it fills cache
		checks.add(new ModuleCheck()); 
		checks.add(new FunctionCheck());
		checks.add(new VarCheck());
	}
	
	public List<SemanticException> checkAST(AbstractSyntaxTree ast) {
		List<SemanticException> exceptions = new ArrayList<SemanticException>();

		for(IWaebricCheck check : checks) {
			check.checkAST(ast, exceptions); // Perform check
		}

		return exceptions;
	}
	
}