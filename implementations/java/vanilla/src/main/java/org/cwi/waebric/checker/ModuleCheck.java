package org.cwi.waebric.checker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import org.cwi.waebric.parser.WaebricParser;
import org.cwi.waebric.parser.ast.module.Import;
import org.cwi.waebric.parser.ast.module.Module;
import org.cwi.waebric.parser.ast.module.ModuleId;
import org.cwi.waebric.parser.ast.module.Modules;
import org.cwi.waebric.scanner.WaebricScanner;

/**
 * Check module nodes for semantic violations.
 * @see NonExistingModuleException
 * @author Jeroen van Schagen
 * @date 09-06-2009
 */
class ModuleCheck implements IWaebricCheck {

	/**
	 * Checker instance.
	 */
	private final WaebricChecker checker;
	
	/**
	 * Construct function check component based on checker instance,
	 * using the checker modules can be cached for faster performance.
	 * @param checker
	 */
	public ModuleCheck(WaebricChecker checker) {
		this.checker = checker;
	}
	
	public void checkAST(Modules modules, List<SemanticException> exceptions) {
		for(Module module: modules) {
			// Check module definition
			String path = getPath(module.getIdentifier());
			File file = new File(path);
			if(! file.isFile()) { // Check of expected file exists
				exceptions.add(new NonExistingModuleException(module.getIdentifier()));
			}
			
			checker.cacheModule(module.getIdentifier(), modules); // Cache module
			
			// Check imported modules
			for(Import imprt: module.getImports()) {
				if(! checker.hasCached(imprt.getIdentifier())) {
					// Only check modules that havn't been cached yet
					checkModuleId(imprt.getIdentifier(), exceptions);
				}
			}
		}
	}
	
	/**
	 * Check if file exists and cache its result.
	 * @param identifier
	 * @param exceptions
	 */
	public void checkModuleId(ModuleId identifier, List<SemanticException> exceptions) {
		if(identifier.size() == 0) { return; } // Invalid identifier, quit cache for efficiency
		if(checker.hasCached(identifier)) { return; } // Already checked module.

		try {
			// Attempt to process file
			FileReader reader = new FileReader(getPath(identifier));
			WaebricScanner scanner = new WaebricScanner(reader);
			scanner.tokenizeStream(); // Tokenize stream
			WaebricParser parser = new WaebricParser(scanner);
			parser.parseTokens(); // Parse file
			
			// Retrieve modules
			Modules modules = parser.getAbstractSyntaxTree().getRoot();
			checker.cacheModule(identifier, modules); // Cache dependent modules
			checkAST(modules, exceptions); // Check dependent modules
		} catch(FileNotFoundException e) {
			exceptions.add(new NonExistingModuleException(identifier));
			checker.cacheModule(identifier, new Modules()); // Cache non-existing module as empty modules node
		}
	}
	
	/**
	 * Construct path, based on module identifier.
	 * @param identifier Module identifier
	 * @return Path
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