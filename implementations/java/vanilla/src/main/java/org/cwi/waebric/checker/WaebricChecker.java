package org.cwi.waebric.checker;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.AbstractSyntaxTree;

/**
 * Walk the abstract syntax tree and looking for semantic violations.
 * @author Jeroen van Schagen
 * @date 09-06-2009
 */
public class WaebricChecker {

	public List<SemanticException> checkAST(AbstractSyntaxTree ast) {
		List<SemanticException> exceptions = new ArrayList<SemanticException>();
		new ModuleChecker(exceptions).visit(ast.getRoot()); // Check modules
		new DeclarationChecker(exceptions).visit(ast.getRoot()); // Check declarations
		return exceptions;
	}
	
}