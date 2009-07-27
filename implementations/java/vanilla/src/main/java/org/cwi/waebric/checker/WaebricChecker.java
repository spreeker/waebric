package org.cwi.waebric.checker;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.ModuleRegister;
import org.cwi.waebric.checker.DeclarationChecker.DuplicateFunctionDefinition;
import org.cwi.waebric.interpreter.Environment;
import org.cwi.waebric.parser.ast.AbstractSyntaxTree;
import org.cwi.waebric.parser.ast.module.Module;
import org.cwi.waebric.parser.ast.module.function.FunctionDef;

/**
 * Walk the abstract syntax tree and looking for semantic violations.
 * @author Jeroen van Schagen
 * @date 09-06-2009
 */
public class WaebricChecker {

	public List<SemanticException> checkAST(AbstractSyntaxTree ast) {
		List<SemanticException> exceptions = new ArrayList<SemanticException>();
		Environment environment = new Environment();
		
		// Retrieve all function definitions
		List<Module> dependancies = ModuleRegister.getInstance().loadDependencies(ast.getRoot());
		for(Module component: dependancies) {
			for(FunctionDef function: component.getFunctionDefinitions()) {
				if(environment.isDefinedFunction(function.getIdentifier().getName())) {
					// Function is already defined, store exception
					exceptions.add(new DuplicateFunctionDefinition(function));
				} else { environment.defineFunction(function); }
			}
		}
		
		ModuleChecker moduleChecker = new ModuleChecker(exceptions);
		DeclarationChecker declarationChecker = new DeclarationChecker(exceptions, environment);
		
		// Check module and dependencies
		for(Module component: dependancies) {
			moduleChecker.visit(component);
			declarationChecker.visit(component);
		}
		
		return exceptions;
	}
	
}