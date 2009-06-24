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

	public List<SemanticException> checkAST(AbstractSyntaxTree ast) {
		List<SemanticException> exceptions = new ArrayList<SemanticException>();
		new DeclarationChecker(exceptions).visit(ast.getRoot());
		return exceptions;
	}
	
}