package org.cwi.waebric;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.cwi.waebric.parser.WaebricParser;
import org.cwi.waebric.parser.ast.AbstractSyntaxTree;
import org.cwi.waebric.parser.ast.module.Import;
import org.cwi.waebric.parser.ast.module.Module;
import org.cwi.waebric.parser.ast.module.ModuleId;
import org.cwi.waebric.scanner.WaebricScanner;

/**
 * Cache modules to enable optimize program processing.
 * @author Jeroen van Schagen
 * @date 10-06-2009
 */
public class ModuleCache {
	
	/**
	 * Instance
	 */
	private static ModuleCache instance;

	/**
	 * Cached modules
	 */
	private final Map<ModuleId, AbstractSyntaxTree> cache;
	
	/**
	 * Construct cache.
	 */
	private ModuleCache() {
		cache = new HashMap<ModuleId, AbstractSyntaxTree>();
	}
	
	/**
	 * Attempt to parse and cache a module based on its identifier.
	 * @param identifier Module identifier, maps on relative file position.
	 * @throws FileNotFoundException Invalid file
	 */
	public AbstractSyntaxTree cacheModule(ModuleId identifier) throws FileNotFoundException {
		if(identifier.size() == 0) { return null; } // Invalid identifier, quit cache for performance
		if(hasCached(identifier)) { return cache.get(identifier); } // Already checked module.
		
		// Attempt to process file
		FileReader reader = new FileReader(getPath(identifier));
		WaebricScanner scanner = new WaebricScanner(reader);
		scanner.tokenizeStream(); // Scan module
		WaebricParser parser = new WaebricParser(scanner);
		parser.parseTokens(); // Parse module
		
		// Retrieve modules
		AbstractSyntaxTree tree = parser.getAbstractSyntaxTree();
		cacheModule(identifier, tree); // Cache dependent modules
		return tree;
	}
	
	/**
	 * Store a module and its abstract syntax tree in cache.
	 * @param identifier Module identifier
	 * @param ast Abstract syntax tree
	 */
	public void cacheModule(ModuleId identifier, AbstractSyntaxTree ast) {
		cache.put(identifier, ast);
	}
	
	/**
	 * Check if cache already contains module with specified identifier.
	 * @param identifier Module identifier
	 * @return
	 */
	public boolean hasCached(ModuleId identifier) {
		return cache.containsKey(identifier);
	}
	
	/**
	 * Retrieve module from cache.
	 * @param identifier
	 * @return Module contents
	 */
	public AbstractSyntaxTree requestModule(ModuleId identifier) {
		return cache.get(identifier);
	}
	
	/**
	 * Load all modules dependent to the contents of an abstract
	 * syntax tree. Modules can be made dependent to each other
	 * using of the import directive.
	 * @param ast Abstract Syntax Tree
	 */
	public void loadDependancies(AbstractSyntaxTree ast) {
		for(Module module: ast.getRoot()) {
			for(Import dependancy: module.getImports()) {
				try {
					AbstractSyntaxTree sub = ModuleCache.getInstance().cacheModule(dependancy.getIdentifier());
					ast.getRoot().addAll(sub.getRoot()); // Attach dependent AST to specified AST
				} catch (FileNotFoundException e) {
					// Skip invalid import directives
				}
			}
		}
	}
	
	/**
	 * Clear module cache.
	 */
	public void clearCache() {
		cache.clear();
	}
	
	/**
	 * Construct path, based on module identifier.
	 * @param identifier Module identifier
	 * @return Path
	 */
	public static String getPath(ModuleId identifier) {
		String path = "";
		for(int i = 0; i < identifier.size(); i++) {
			if(i > 0) { path += "/"; }
			path += identifier.get(i).getLiteral().toString();
		}
		path += ".wae";
		return path;
	}
	
	/**
	 * Retrieve default instance of module cache.
	 * @see <a href="http://en.wikipedia.org/wiki/Singleton_pattern">Singleton pattern</a>
	 * @return Default instance
	 */
	public static ModuleCache getInstance() {
		if(instance == null) { instance = new ModuleCache(); }
		return instance;
	}
	
	/**
	 * Retrieve new instance of module cache.
	 * @return New instance
	 */
	public static ModuleCache newInstance() {
		return new ModuleCache();
	}

}