//package org.cwi.waebric.checker;
//
//import java.io.IOException;
//import java.util.List;
//
//import org.cwi.waebric.ModuleRegister;
//import org.cwi.waebric.parser.ast.AbstractSyntaxTree;
//import org.cwi.waebric.parser.ast.module.Import;
//import org.cwi.waebric.parser.ast.module.Module;
//import org.cwi.waebric.parser.ast.module.ModuleId;
//
///**
// * Check module nodes for semantic violations.
// * @see NonExistingModuleException
// * @author Jeroen van Schagen
// * @date 09-06-2009
// */
//class ModuleCheck implements IWaebricCheck {
//	
//	public void checkAST(AbstractSyntaxTree tree, List<SemanticException> exceptions) {
//		for(Module module: tree.getRoot()) {
//			ModuleRegister.getInstance().cacheModule(module.getIdentifier(), tree);
//			
//			// Apply recursion on all, not cached, transitive imported modules
//			for(Import imprt: module.getImports()) {
//				if(! ModuleRegister.getInstance().hasCached(imprt.getIdentifier())) {
//					checkModuleId(imprt.getIdentifier(), exceptions);
//				}
//			}
//		}
//	}
//	
//	/**
//	 * Check if file exists and cache its result.
//	 * @param identifier
//	 * @param exceptions
//	 */
//	public void checkModuleId(ModuleId identifier, List<SemanticException> exceptions) {
//		try {
//			// Attempt to process file
//			AbstractSyntaxTree tree = ModuleRegister.getInstance().loadModule(identifier);
//			if(tree != null) { checkAST(tree, exceptions); } // Check dependent modules
//		} catch(IOException e) {
//			exceptions.add(new NonExistingModuleException(identifier));
//			ModuleRegister.getInstance().cacheModule(identifier, new AbstractSyntaxTree());
//		}
//	}
//
//	/**
//	 * If for an import directive import m no corresponding file m.wae 
//	 * can be found, this a an error. [The import directive is skipped]
//	 * 
//	 * @author Jeroen van Schagen
//	 * @date 09-06-2009
//	 */
//	public class NonExistingModuleException extends SemanticException {
//
//		/**
//		 * Generated serial ID
//		 */
//		private static final long serialVersionUID = -4503945323554024642L;
//
//		public NonExistingModuleException(ModuleId id) {
//			super("Module identifier \"" + id.toString() 
//					+ "\" at line " + id.get(0).getToken().getLine()
//					+ ", refers to a non-existing module.");
//		}
//		
//	}
//	
//}