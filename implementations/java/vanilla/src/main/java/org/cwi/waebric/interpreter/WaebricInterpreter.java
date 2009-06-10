package org.cwi.waebric.interpreter;

import org.cwi.waebric.ModuleCache;
import org.cwi.waebric.parser.ast.AbstractSyntaxTree;

/**
 * Interpreter converts the Abstract Syntax Tree of a Waebric
 * program into XHTML code.
 * 
 * @author Jeroen van Schagen
 * @date 10-06-2009
 */
public class WaebricInterpreter {

	public void interpretProgram(AbstractSyntaxTree tree) {
		ModuleCache.getInstance().loadDependancies(tree); // Retrieve all dependent modules
	}
	
}