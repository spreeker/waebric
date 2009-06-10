package org.cwi.waebric.checker;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.ast.module.Import;
import org.cwi.waebric.parser.ast.module.Module;
import org.cwi.waebric.parser.ast.module.ModuleId;
import org.cwi.waebric.parser.ast.module.Modules;
import org.cwi.waebric.parser.ast.module.function.Formals;
import org.cwi.waebric.parser.ast.module.function.FunctionDef;
import org.cwi.waebric.parser.ast.module.site.Mapping;
import org.cwi.waebric.parser.ast.module.site.Site;
import org.cwi.waebric.parser.ast.statement.Statement;

/**
 * Check function definition nodes for semantic violations.
 * @see ArityMismatchException
 * @see DuplicateFunctionDefinition
 * @see UndefinedFunctionException
 * @author Jeroen van Schagen
 * @date 09-06-2009
 */
class FunctionCheck implements IWaebricCheck {
	
	/**
	 * Checker instance.
	 */
	private final WaebricChecker checker;
	
	/**
	 * Construct function check component based on checker instance,
	 * using the checker cached modules can be retrieved. The function
	 * checker only functions correctly if all related modules have
	 * been cached.
	 * @param checker
	 */
	public FunctionCheck(WaebricChecker checker) {
		this.checker = checker;
	}
	
	public void checkAST(Modules modules, List<SemanticException> exceptions) {
		for(Module module : modules) {
			// Retrieve function definitions
			List<FunctionDef> definitions = getFunctionDefinitions(module, exceptions);
			
			for(Site site: module.getSites()) {
				for(Mapping mapping: site.getMappings()) {
					// Check all calls made from site mappings
					checkCalls(mapping.getMarkup(), definitions, exceptions);
				}
			}
			
			for(FunctionDef def: module.getFunctionDefinitions()) {
				for(Statement statement: def.getStatements()) {
					// Check all calls made within function statements
					checkCalls(statement, definitions, exceptions);
				}
			}
		}
	}
	
	/**
	 * Check a mark-up call based on a collection of function definitions.
	 * @param node Mark-up call
	 * @param definitions Defined functions
	 * @param exceptions Exceptions
	 */
	public void checkCalls(AbstractSyntaxNode node, List<FunctionDef> definitions, 
			List<SemanticException> exceptions) {
		if(node instanceof Markup.Call) {
			Markup.Call call = (Markup.Call) node; // Cast node to correct type
			if(containsFunction(call.getDesignator().getIdentifier(), definitions)) {
				FunctionDef definition = getFunction(call.getDesignator().getIdentifier(), definitions);
				
				// Compare arguments
				int expectedArguments = 0; // Determine expected formals
				if(definition.getFormals() instanceof Formals.RegularFormal) {
					Formals.RegularFormal formals = (Formals.RegularFormal) definition.getFormals();
					expectedArguments = formals.getIdentifiers().size();
				}
				
				if(call.getArguments().size() != expectedArguments) {
					// Call refers to argument with different amount of arguments
					exceptions.add(new ArityMismatchException(call));
				}
			} else {
				// Call refers to undefined function
				exceptions.add(new UndefinedFunctionException(call));
			}
		}
		
		for(AbstractSyntaxNode child: node.getChildren()) {
			checkCalls(child, definitions, exceptions); // Also check children nodes
		}
	}
	
	/**
	 * Retrieve function definitions of module and all related modules.
	 * @param module Root module
	 * @param exceptions Exceptions
	 * @return
	 */
	public List<FunctionDef> getFunctionDefinitions(Module module, List<SemanticException> exceptions) {
		List<ModuleId> collected = new ArrayList<ModuleId>();
		return getFunctionDefinitions(module, collected, exceptions);
	}
	
	/**
	 * Retrieve function definitions of module and all related modules.
	 * @param module Root module
	 * @param collected List of already collected modules
	 * @param exceptions Exceptions
	 * @return
	 */
	public List<FunctionDef> getFunctionDefinitions(Module module, 
			List<ModuleId> collected, List<SemanticException> exceptions) {
		List<FunctionDef> definitions = new ArrayList<FunctionDef>();
		
		// Attach function definitions to collection
		for(FunctionDef function : module.getFunctionDefinitions()) {
			if(containsFunction(function.getIdentifier(), definitions)) {
				exceptions.add(new DuplicateFunctionDefinition(function));
			} else {
				definitions.add(function);
			}
		}
		
		// Retrieve functions from dependent modules
		for(Import imprt : module.getImports()) {
			// Check if module functions have been collected
			if(! collected.contains(imprt.getIdentifier())) {
				// Retrieve module contents from cache
				for(Module sub : checker.requestModule(imprt.getIdentifier())) {
					collected.add(sub.getIdentifier());
					definitions.addAll(getFunctionDefinitions(sub, collected, exceptions));
				}
			}
		}
		
		return definitions;
	}
	
	/**
	 * Check if a function with the same identifier is already defined.
	 * @param identifier Function name
	 * @param definitions Collection of definitions
	 * @return
	 */
	private boolean containsFunction(IdCon identifier, List<FunctionDef> definitions) {
		return getFunction(identifier, definitions) != null;
	}
	
	/**
	 * Retrieve function based on identifier.
	 * @param identifier Function name
	 * @param definitions Collection of definitions
	 * @return
	 */
	private FunctionDef getFunction(IdCon identifier, List<FunctionDef> definitions) {
		for(FunctionDef def : definitions) {
			if(def.getIdentifier().equals(identifier)) {
				return def;
			}
		}
		
		return null;
	}

	/**
	 * If a function is called with an incorrect number of arguments 
	 * (as follows from its definition), this is an error.
	 * 
	 * @author Jeroen van Schagen
	 * @date 09-06-2009
	 */
	public class ArityMismatchException extends SemanticException {

		/**
		 * Generated serial ID
		 */
		private static final long serialVersionUID = -954167103131401047L;

		public ArityMismatchException(Markup.Call call) {
			super(call.toString() + " is an arity mismatch");
		}
		
	}
	
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
	
	/**
	 * If for a markup-call (f) no function definition can be found in 
	 * the current module or one of its (transitive) imports, and, if 
	 * f is not a tag defined in the XHTML 1.0 Transitional standard, 
	 * then this is an error. [f will be interpreted as if it was part 
	 * of XHTML 1.0 transitional.]
	 * 
	 * @author Jeroen van Schagen
	 * @date 09-06-2009
	 */
	public class UndefinedFunctionException extends SemanticException {

		/**
		 * Generated serial ID
		 */
		private static final long serialVersionUID = -4467095005921534334L;

		public UndefinedFunctionException(Markup.Call call) {
			super(call.toString() + " is an undefined function.");
		}
		
	}
	
}