package org.cwi.waebric.checker.module;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

import org.cwi.waebric.checker.IWaebricCheck;
import org.cwi.waebric.checker.SemanticException;
import org.cwi.waebric.parser.WaebricParser;
import org.cwi.waebric.parser.ast.module.Module;
import org.cwi.waebric.parser.ast.module.ModuleId;
import org.cwi.waebric.parser.ast.module.Modules;
import org.cwi.waebric.scanner.WaebricScanner;

public class ModuleCheck implements IWaebricCheck {

	/**
	 * Cache of dependent module(s)
	 */
	private final Map<ModuleId, Modules> moduleCache;
	
	/**
	 * Construct module check and store memory address for
	 * module cache map, in which modules can be stored
	 * during their check.
	 * @param moduleCache
	 */
	public ModuleCheck(Map<ModuleId, Modules> moduleCache) {
		this.moduleCache = moduleCache;
	}
	
	@Override
	public void checkAST(Modules modules, List<SemanticException> exceptions) {
		for(Module module: modules) {
			// Check module definition
			String path = getPath(module.getIdentifier());
			File file = new File(path);
			if(! file.isFile()) { // Check of expected file exists
				exceptions.add(new NonExistingModuleException(module.getIdentifier()));
			}
			
			moduleCache.put(module.getIdentifier(), modules); // Cache module
			
			// Check imported modules
			for(int i = 0; i < module.getImportCount(); i++) {
				checkModule(module.getImport(i).getIdentifier(), exceptions);
			}
		}
	}
	
	/**
	 * Check if file exists and cache its result.
	 * @param identifier
	 * @param exceptions
	 */
	public void checkModule(ModuleId identifier, List<SemanticException> exceptions) {
		if(identifier.size() == 0) { return; } // Invalid identifier, quit cache for efficiency
		if(moduleCache.containsKey(identifier)) { return; } // Already checked module.

		try {
			// Attempt to process file
			FileReader reader = new FileReader(getPath(identifier));
			WaebricScanner scanner = new WaebricScanner(reader);
			WaebricParser parser = new WaebricParser(scanner);
			parser.parseTokens(); // Parse file
			
			// Retrieve modules
			Modules modules = parser.getAbstractSyntaxTree().getRoot();
			moduleCache.put(identifier, modules); // Cache parsed module
		} catch(FileNotFoundException e) {
			exceptions.add(new NonExistingModuleException(identifier));
			moduleCache.put(identifier, new Modules()); // Cache non-existing module as empty modules node
		}
	}
	
	/**
	 * Construct path, based on module identifier.
	 * @param identifier
	 * @return
	 */
	public String getPath(ModuleId identifier) {
		String path = "";
		for(int i = 0; i < identifier.size(); i++) {
			if(i > 0) { path += "/"; }
			path += identifier.get(i).getLiteral().toString();
		}
		path += ".wae";
		return path;
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
			super(id.toString() + " is a non-existing module.");
		}
		
	}
	
}