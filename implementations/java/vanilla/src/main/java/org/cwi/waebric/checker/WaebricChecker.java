package org.cwi.waebric.checker;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.module.Modules;
import org.cwi.waebric.parser.ast.module.ModuleId;
import org.cwi.waebric.scanner.WaebricScanner;

/**
 * Verify the semantics of an abstract syntax tree.
 * 
 * @author Jeroen van Schagen
 * @date 09-06-2009
 */
public class WaebricChecker {

	/**
	 * Modules being checked
	 */
	private final Modules modules;
	
	/**
	 * Cache of dependent module(s)
	 */
	private final Map<ModuleId, Modules> moduleCache;
	
	/**
	 * Construct checker best on modules instance.
	 * @param modules Modules being checked
	 */
	public WaebricChecker(Modules modules) {
		this.modules = modules;
		this.moduleCache = new HashMap<ModuleId, Modules>();
	}
	
	public List<SemanticException> checkAST() {
		List<SemanticException> exceptions = new ArrayList<SemanticException>();
		// TODO
		return exceptions;
	}
	
	private boolean cacheModule(ModuleId identifier, List<SemanticException> exceptions) {
		if(identifier.size() == 0) { return false; }
		
		// Construct path, based on module identifier.
		String path = "";
		for(int i = 0; i < identifier.size(); i++) {
			if(i > 0) { path += "/"; }
			path += identifier.get(i).getLiteral().toString();
		}
		path += ".wae";
		
		// Attempt to parse file
		try {
			FileReader reader = new FileReader(path);
			WaebricScanner scanner = new WaebricScanner(reader);
		} catch(FileNotFoundException e) {
			// Invalid file found
		}
		
		// TODO: try { parse file }, store modules
		// TODO: catch file not found, return non-existing-module, store new Modules()
		return true;
	}
	
}