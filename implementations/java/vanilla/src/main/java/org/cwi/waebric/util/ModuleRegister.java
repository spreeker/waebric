package org.cwi.waebric.util;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cwi.waebric.lexer.LexicalException;
import org.cwi.waebric.lexer.WaebricScanner;
import org.cwi.waebric.parser.SyntaxException;
import org.cwi.waebric.parser.WaebricParser;
import org.cwi.waebric.parser.ast.module.Import;
import org.cwi.waebric.parser.ast.module.Module;
import org.cwi.waebric.parser.ast.module.ModuleId;
import org.cwi.waebric.parser.ast.module.Modules;

/**
 * Module register allows users a clean interface to load the AST of modules based on
 * their identifier. Additionally, loaded modules will be cached to improve performance.
 * @author Jeroen van Schagen
 * @date 10-06-2009
 */
public class ModuleRegister {
	
	/**
	 * Instance
	 */
	private static ModuleRegister instance;

	/**
	 * Cached modules
	 */
	private final Map<ModuleId, Module> cache;
	
	/**
	 * Construct cache.
	 */
	private ModuleRegister() {
		cache = new HashMap<ModuleId, Module>();
	}
	
	/**
	 * Attempt to load a module from the file system based on an identifier.
	 * @param identifier Module identifier, maps on relative file position.
	 * @throws IOException 
	 */
	public Module requestModule(ModuleId identifier) {
		if(hasCached(identifier)) { return cache.get(identifier); } // Already checked module.
		if(identifier.size() == 0) { return null; } // Invalid identifier, quit cache for performance

		String path = getPath(identifier);
		try {
			FileReader reader = new FileReader(path);
			
			WaebricScanner scanner = new WaebricScanner(reader);
			List<LexicalException> le = scanner.tokenizeStream(); // Scan module
			if(le.size() > 0) { return null; }
			
			WaebricParser parser = new WaebricParser(scanner.iterator());
			List<SyntaxException> se = parser.parseTokens(); // Parse module
			if(se.size() > 0) { return null; }
			
			// Retrieve modules
			Module module = parser.getAbstractSyntaxTree().getRoot();
			if(module != null) { cacheModule(identifier, module); } // Cache dependent modules
			return module;
		} catch(IOException e) {
			return null;
		}
	}
	
	/**
	 * Store a module and its AST in cache. By caching parsed modules they only
	 * have to be parsed once, which increases efficiency. It does however
	 * require a bit more memory to store all loaded modules, when the memory
	 * consumption gets too high use the clearCache function.
	 * @param identifier Module identifier
	 * @param modules Abstract syntax tree
	 */
	public void cacheModule(ModuleId identifier, Module module) {
		cache.put(identifier, module);
	}
	
	/**
	 * Verify that a module is stored in the cache.
	 * @param identifier Module identifier
	 * @return
	 */
	public boolean hasCached(ModuleId identifier) {
		return cache.containsKey(identifier);
	}
	
	/**
	 * Retrieve all transitive dependent modules.
	 * @param modules Modules from which dependent modules are retrieved.
	 * @return 
	 * @throws  
	 */
	public Modules loadDependencies(Module module) {
		Modules container = new Modules();
		loadDependancies(module, container);
		return container;
	}
	
	/**
	 * 
	 * @param module
	 * @param container
	 */
	private void loadDependancies(Module module, Modules container) {
		if(! container.contains(module.getIdentifier())) {
			container.add(module); // Store module in container
			
			// Store dependent modules
			for(Import imprt: module.getImports()) {
				if(! container.contains(imprt.getIdentifier())) {
					try {
						// Retrieve AST of imported module
						Module dependancy = requestModule(imprt.getIdentifier()); 
						if(dependancy != null) { loadDependancies(dependancy, container); }
					} catch (Exception e) {
						e.printStackTrace();
					}
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
			path += identifier.get(i).getToken().getLexeme().toString();
		}
		path += ".wae";
		return path;
	}
	
	/**
	 * Retrieve default instance of module cache.
	 * @see <a href="http://en.wikipedia.org/wiki/Singleton_pattern">Singleton pattern</a>
	 * @return Default instance
	 */
	public static ModuleRegister getInstance() {
		return instance == null ? newInstance() : instance;
	}
	
	/**
	 * Retrieve new instance of module cache.
	 * @return New instance
	 */
	public static ModuleRegister newInstance() {
		instance = new ModuleRegister();
		return instance;
	}
	
}