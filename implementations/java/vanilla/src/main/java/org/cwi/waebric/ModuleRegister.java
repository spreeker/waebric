package org.cwi.waebric;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.cwi.waebric.parser.WaebricParser;
import org.cwi.waebric.parser.ast.AbstractSyntaxTree;
import org.cwi.waebric.parser.ast.module.Import;
import org.cwi.waebric.parser.ast.module.Module;
import org.cwi.waebric.parser.ast.module.ModuleId;
import org.cwi.waebric.parser.ast.module.Modules;
import org.cwi.waebric.scanner.WaebricScanner;
import org.cwi.waebric.scanner.token.TokenIterator;

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
	private final Map<ModuleId, AbstractSyntaxTree> cache;
	
	/**
	 * Construct cache.
	 */
	private ModuleRegister() {
		cache = new HashMap<ModuleId, AbstractSyntaxTree>();
	}
	
	/**
	 * Attempt to load a module from the file system based on an identifier.
	 * @param identifier Module identifier, maps on relative file position.
	 * @throws IOException 
	 */
	public AbstractSyntaxTree loadModule(ModuleId identifier) throws IOException {
		if(hasCached(identifier)) { return cache.get(identifier); } // Already checked module.
		if(identifier.size() == 0) { return null; } // Invalid identifier, quit cache for performance

		// Attempt to process file
		FileReader reader = new FileReader(getPath(identifier));
		WaebricScanner scanner = new WaebricScanner(reader);
		TokenIterator iterator = scanner.tokenizeStream(); // Scan module
		WaebricParser parser = new WaebricParser(iterator);
		parser.parseTokens(); // Parse module
		
		// Retrieve modules
		AbstractSyntaxTree tree = parser.getAbstractSyntaxTree();
		cacheModule(identifier, tree); // Cache dependent modules
		return tree;
	}
	
	/**
	 * Store a module and its AST in cache. By caching parsed modules they only
	 * have to be parsed once, which increases efficiency. It does however
	 * require a bit more memory to store all loaded modules, when the memory
	 * consumption gets too high use the clearCache function.
	 * @param identifier Module identifier
	 * @param ast Abstract syntax tree
	 */
	public void cacheModule(ModuleId identifier, AbstractSyntaxTree ast) {
		cache.put(identifier, ast);
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
	 * Retrieve module from cache, this function only works when the module
	 * has been stored in cache. Non-cached modules should be retrieved using
	 * the loadModule procedure, this will automatically cache the AST.
	 * @param identifier
	 * @return Module contents
	 */
	public AbstractSyntaxTree requestModule(ModuleId identifier) {
		return cache.get(identifier);
	}
	
	/**
	 * Retrieve all transitive dependent modules.
	 * @param modules Modules from which dependent modules are retrieved.
	 * @return 
	 * @throws  
	 */
	public AbstractSyntaxTree loadDependancies(Modules modules) {
		AbstractSyntaxTree result = new AbstractSyntaxTree();
		result.getRoot().addAll(modules); // Clone modules content
		
		for(Module module: modules) {
			for(Import imprt: module.getImports()) {
				if(! result.getRoot().contains(imprt.getIdentifier())) {
					try {
						// Retrieve and store AST of imported modules
						AbstractSyntaxTree sub = loadModule(imprt.getIdentifier()); 
						loadDependancies(sub.getRoot());
						result.getRoot().addAll(sub.getRoot());
					} catch (IOException e) {
						// Skip invalid import directives
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Retrieve all transitive dependent modules.
	 * @param module Module from which dependent modules are retrieved.
	 * @return
	 */
	public AbstractSyntaxTree loadDependancies(Module module) {
		Modules modules = new Modules();
		modules.add(module);
		return loadDependancies(modules);
	}
	
	/**
	 * Retrieve all transitive dependent modules.
	 * @param ast Abstract syntax tree from which dependent modules are retrieved.
	 * @return
	 */
	public AbstractSyntaxTree loadDependancies(AbstractSyntaxTree ast) {
		return loadDependancies(ast.getRoot());
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
		if(instance == null) { instance = new ModuleRegister(); }
		return instance;
	}
	
	/**
	 * Retrieve new instance of module cache.
	 * @return New instance
	 */
	public static ModuleRegister newInstance() {
		return new ModuleRegister();
	}

}