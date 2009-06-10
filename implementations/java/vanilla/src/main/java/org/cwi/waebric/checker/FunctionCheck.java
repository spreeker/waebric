package org.cwi.waebric.checker;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.ModuleCache;
import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.AbstractSyntaxTree;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.ast.module.Import;
import org.cwi.waebric.parser.ast.module.Module;
import org.cwi.waebric.parser.ast.module.ModuleId;
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

	public void checkAST(AbstractSyntaxTree tree, List<SemanticException> exceptions) {
		for(Module module : tree.getRoot()) {
			// Retrieve all defined functions
			List<FunctionDef> definitions = getFunctionDefinitions(module, exceptions);
			
			// Check mapping calls
			for(Site site: module.getSites()) {
				for(Mapping mapping: site.getMappings()) {
					checkCall(mapping.getMarkup(), definitions, exceptions);
				}
			}
			
			// Check statement calls
			for(FunctionDef def: module.getFunctionDefinitions()) {
				for(Statement statement: def.getStatements()) {
					checkCall(statement, definitions, exceptions);
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
	public void checkCall(AbstractSyntaxNode node, List<FunctionDef> definitions, 
			List<SemanticException> exceptions) {
		if(node instanceof Markup.Call) {
			Markup.Call call = (Markup.Call) node;
			
			// Check if call is made to a defined function
			if(containsFunction(call.getDesignator().getIdentifier(), definitions)) {
				FunctionDef definition = getFunction(call.getDesignator().getIdentifier(), definitions);
				
				// Determine expected arguments
				int expectedArguments = 0;
				if(definition.getFormals() instanceof Formals.RegularFormal) {
					Formals.RegularFormal formals = (Formals.RegularFormal) definition.getFormals();
					expectedArguments = formals.getIdentifiers().size();
				}
				
				// Compare call arguments to expectation
				if(call.getArguments().size() != expectedArguments) {
					exceptions.add(new ArityMismatchException(call));
				}
			} else {
				exceptions.add(new UndefinedFunctionException(call));
			}
		}
		
		// Recursively check node children
		for(AbstractSyntaxNode child: node.getChildren()) {
			checkCall(child, new ArrayList<FunctionDef>(definitions), exceptions);
		}
	}
	
	/**
	 * Retrieve function definitions of module and all related modules.
	 * @param module Root module
	 * @param exceptions Currently occurred exceptions
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
	 * @param exceptions Currently occurred exceptions
	 * @return
	 */
	public List<FunctionDef> getFunctionDefinitions(Module module, 
			List<ModuleId> collected, List<SemanticException> exceptions) {
		// Collection function definitions
		List<FunctionDef> definitions = new ArrayList<FunctionDef>();
		for(FunctionDef function : module.getFunctionDefinitions()) {
			if(containsFunction(function.getIdentifier(), definitions)) {
				exceptions.add(new DuplicateFunctionDefinition(function));
			} else {
				definitions.add(function);
			}
		}
		
		// Recursively retrieve function definitions of imported modules
		for(Import imprt : module.getImports()) {
			try {
				AbstractSyntaxTree ast = ModuleCache.getInstance().cacheModule(imprt.getIdentifier());
				for(Module sub : ast.getRoot()) {
					collected.add(sub.getIdentifier());
					definitions.addAll(getFunctionDefinitions(sub, collected, exceptions));
				}
			} catch (FileNotFoundException e) {
				// Invalid file name, these are checked by module check
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
			if(def.getIdentifier().equals(identifier)) { return def; }
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