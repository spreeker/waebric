package org.cwi.waebric.checker;

import java.io.File;
import java.util.List;

import org.cwi.waebric.checker.exception.NonExistingModuleException;
import org.cwi.waebric.checker.exception.SemanticException;
import org.cwi.waebric.parser.ast.DefaultNodeVisitor;
import org.cwi.waebric.parser.ast.module.Import;
import org.cwi.waebric.parser.ast.module.Module;
import org.cwi.waebric.parser.ast.module.ModuleId;
import org.cwi.waebric.util.ModuleRegister;

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
		module.getIdentifier().accept(this);
		for(Import imprt: module.getImports()) {
			imprt.accept(this);
		}
	}
	
	@Override
	public void visit(ModuleId identifier) {
		String path = ModuleRegister.getPath(identifier);
		File file = new File(path);
		if(! file.isFile()) {
			exceptions.add(new NonExistingModuleException(identifier));
		}
	}
	
}