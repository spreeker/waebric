package org.cwi.waebric.checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cwi.waebric.parser.ast.AbstractSyntaxTree;
import org.cwi.waebric.parser.ast.module.ModuleId;
import org.cwi.waebric.parser.ast.module.Modules;

/**
 * Verify the semantics of an abstract syntax tree.
 * 
 * @author Jeroen van Schagen
 * @date 09-06-2009
 */
public class WaebricChecker {
	
	/**
	 * Collection of cached modules.
	 */
	private final Map<ModuleId, Modules> moduleCache;
	
	/**
	 * Checker components.
	 */
	private final List<IWaebricCheck> checks;
	
	/**
	 * Construct checker.
	 * @param ast Abstract syntax tree
	 */
	public WaebricChecker() {
		this.moduleCache = new HashMap<ModuleId, Modules>();
		this.checks = new ArrayList<IWaebricCheck>();
		
		// Module check should be executed first, as it fills cache
		checks.add(new ModuleCheck(this)); 
		checks.add(new FunctionCheck(this));
		checks.add(new VarCheck());
	}
	
	public List<SemanticException> checkAST(AbstractSyntaxTree ast) {
		List<SemanticException> exceptions = new ArrayList<SemanticException>();

		// Perform all checks
		for(IWaebricCheck check : checks) {
			check.checkAST(ast, exceptions);
		}

		return exceptions;
	}
	
	/**
	 * Check if cache already contains module with specified identifier.
	 * @param identifier Module identifier
	 * @return
	 */
	public boolean hasCached(ModuleId identifier) {
		return moduleCache.containsKey(identifier);
	}
	
	/**
	 * Store module in cache, so it doesn't have to be parsed again.
	 * @param identifier Module identifier
	 * @param modules Module contents
	 */
	public void cacheModule(ModuleId identifier, Modules modules) {
		moduleCache.put(identifier, modules);
	}
	
	/**
	 * Retrieve module from cache.
	 * @param identifier
	 * @return Module contents
	 */
	public Modules requestModule(ModuleId identifier) {
		return moduleCache.get(identifier);
	}
	
	/**
	 * Clear module cache.
	 */
	public void clearCache() {
		moduleCache.clear();
	}

}