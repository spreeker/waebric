package org.cwi.waebric.checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	 * Cache of dependent module(s)
	 */
	private final Map<ModuleId, Modules> moduleCache;
	
	/**
	 * Checker instances
	 */
	private final List<IWaebricCheck> checks;
	
	/**
	 * Construct checker best on modules instance.
	 * @param modules Modules being checked
	 */
	public WaebricChecker() {
		this.moduleCache = new HashMap<ModuleId, Modules>();
		
		// Initiate checks
		this.checks = new ArrayList<IWaebricCheck>();
		checks.add(new ModuleCheck(moduleCache)); // Module check should be executed first, as it fills cache
	}
	
	public List<SemanticException> checkAST(Modules modules) {
		List<SemanticException> exceptions = new ArrayList<SemanticException>();
		
		// Perform all checks
		for(IWaebricCheck check : checks) {
			check.checkAST(modules, exceptions);
		}

		return exceptions;
	}
	
	
	
}