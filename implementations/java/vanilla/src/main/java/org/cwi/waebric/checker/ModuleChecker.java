package org.cwi.waebric.checker;

import java.io.File;
import java.util.List;

import org.cwi.waebric.ModuleRegister;
import org.cwi.waebric.parser.ast.DefaultNodeVisitor;
import org.cwi.waebric.parser.ast.module.Import;
import org.cwi.waebric.parser.ast.module.Module;
import org.cwi.waebric.parser.ast.module.ModuleId;
import org.cwi.waebric.parser.ast.module.Modules;

public class ModuleChecker extends DefaultNodeVisitor {

	/**
	 * Exceptions
	 */
	private final List<SemanticException> exceptions;

	/**
	 * Construct checker
	 * @param exceptions
	 */
	public ModuleChecker(List<SemanticException> exceptions) {
		this.exceptions = exceptions;
	}
	
	@Override
	public void visit(Module module) {
		Modules dependancies = ModuleRegister.getInstance().loadDependancies(module).getRoot();
		for(Module dependancy: dependancies) {
			for(Import imprt: dependancy.getImports()) {
				imprt.accept(this);
			}
		}
	}
	
	@Override
	public void visit(Import imprt) {
		String path = ModuleRegister.getPath(imprt.getIdentifier());
		File file = new File(path);
		if(! file.isFile()) {
			exceptions.add(new NonExistingModuleException(imprt.getIdentifier()));
		}
	}
	
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
	
}