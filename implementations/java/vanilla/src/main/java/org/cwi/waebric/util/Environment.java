package org.cwi.waebric.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.module.function.FunctionDef;

/**
 * Environment maintain function and variable definitions.
 * @author Jeroen van Schagen
 * @date 22-06-2009
 */
public class Environment implements Cloneable {

	private final Environment parent;
	private final Map<String, FunctionDef> functions;
	private final Map<String, Expression> variables;
	
	/**
	 * Construct environment.
	 */
	public Environment() {
		this(null);
	}
	
	/**
	 * Construct environment and store parent environment.
	 * @param e Parent environment
	 */
	public Environment(Environment parent) {
		this.parent = parent;
		functions = new HashMap<String, FunctionDef>();
		variables = new HashMap<String, Expression>();
	}
	
	/**
	 * Create a hard-copy of the current environment instance.
	 * @return Cloned environment
	 */
	public Environment clone() {
		Environment clone = new Environment(getParent());
		
		// Clone function definitions
		clone.defineFunctions(this.getFunctionDefinitions());
		
		// Clone variable definitions
		for(String variable: this.getVariableNames()) {
			clone.defineVariable(variable, this.getVariable(variable));
		}
		
		return clone;
	}
	
	/**
	 * Retrieve function definition.
	 * @param name
	 * @return
	 */
	public FunctionDef getFunction(String name) {
		if(functions.containsKey(name)) { return functions.get(name); }
		if(parent != null) { return parent.getFunction(name); }
		return null;
	}
	
	/**
	 * Retrieve function names.
	 * @return
	 */
	public Collection<String> getFunctionNames() {
		return functions.keySet();
	}
	
	/**
	 * Check if function is defined in environment.
	 * @param name
	 * @return
	 */
	public boolean isDefinedFunction(String name) {
		boolean contains = functions.containsKey(name);
		if(! contains && parent != null) { return parent.isDefinedFunction(name); }
		return contains;
	}
	
	/**
	 * Retrieve variable expression.
	 * @param name
	 * @return
	 */
	public Expression getVariable(String name) {
		if(variables.containsKey(name)) { return variables.get(name); }
		if(parent != null) { return parent.getVariable(name); }
		return null;
	}
	
	/**
	 * Retrieve variable names.
	 * @return
	 */
	public Collection<String> getVariableNames() {
		return variables.keySet();
	}
	
	/**
	 * Check if variable is defined in environment.
	 * @param name
	 * @return
	 */
	public boolean isDefinedVariable(String name) {
		boolean contains = variables.containsKey(name);
		if(! contains && parent != null) { return parent.isDefinedVariable(name); }
		return contains;
	}
	
	/**
	 * Extend current function definitions with a function.
	 * @param name
	 * @param function
	 */
	public void defineFunction(FunctionDef function) {
		if(function == null) { return; } // Retrieve function identifier
		String identifier = function.getIdentifier() == null ? "" : function.getIdentifier().getName();
		functions.put(identifier, function);
	}
	
	/**
	 * Extend current function definitions with a collection of functions.
	 * @param functions
	 */
	public void defineFunctions(Collection<FunctionDef> functions) {
		for(FunctionDef function: functions) { defineFunction(function); }
	}
	
	/**
	 * Retrieve collection of function definitions.
	 * @return
	 */
	public Collection<FunctionDef> getFunctionDefinitions() {
		return functions.values();
	}
	
	/**
	 * Extend current variable definitions with a variable.
	 * @param name
	 * @param value
	 */
	public void defineVariable(String name, Expression value) {
		variables.put(name, value);
	}
	
	/**
	 * Retrieve parent environment.
	 * @return
	 */
	public Environment getParent() {
		return parent;
	}
	
	@Override
	public String toString() {
		return 
			"Functions: " + functions.keySet().toString() + "\n" +
			"Variables: " + variables.keySet().toString();
	}
	
}