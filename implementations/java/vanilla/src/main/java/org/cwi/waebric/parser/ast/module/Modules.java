package org.cwi.waebric.parser.ast.module;

import java.util.Collection;

import org.cwi.waebric.parser.ast.AbstractSyntaxNodeList;

/**
 * Module* -> Modules
 * 
 * @author Jeroen van Schagen
 * @date 19-05-2009
 */
public class Modules extends AbstractSyntaxNodeList<Module> {

	/**
	 * Construct empty modules.
	 */
	public Modules() { }
	
	/**
	 * Construct modules with a collection of base modules.
	 * @param modules
	 */
	public Modules(Collection<Module> modules) {
		this.addAll(modules);
	}
	
	/**
	 * Check if a module with that specific identity is stored.
	 * @param identifier
	 * @return
	 */
	public boolean contains(ModuleId identifier) {
		for(Module module: this) {
			if(module.getIdentifier().equals(identifier)) { return true; }
		} 
		
		return false;
	}
	
	/**
	 * Check if a module is stored.
	 * @param module
	 * @return
	 */
	public boolean contains(Module module) {
		return super.contains(module);
	}
	
}