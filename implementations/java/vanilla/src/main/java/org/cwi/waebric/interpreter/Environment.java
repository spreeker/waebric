package org.cwi.waebric.interpreter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.module.function.FunctionDef;

/**
 * Environment maintain function and variable definitions.
 * @author Jeroen van Schagen
 * @date 22-06-2009
 */
public class Environment {

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
	 * Retrieve function names.
	 * @return
	 */
	public Collection<String> getFunctionNames() {
		Set<String> names = functions.keySet();
		if(parent != null) { names.addAll(parent.getFunctionNames()); }
		return names;
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
	 * Check if function is defined in environment.
	 * @param name
	 * @return
	 */
	public boolean containsFunction(String name) {
		boolean contains = functions.containsKey(name);
		if(! contains && parent != null) { return parent.containsFunction(name); }
		return contains;
	}
	
	/**
	 * Retrieve variable names.
	 * @return
	 */
	public Collection<String> getVariableNames() {
		Set<String> names = variables.keySet();
		if(parent != null) { names.addAll(parent.getFunctionNames()); }
		return names;
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
	 * Check if variable is defined in environment.
	 * @param name
	 * @return
	 */
	public boolean containsVariable(String name) {
		boolean contains = variables.containsKey(name);
		if(! contains && parent != null) { return parent.containsVariable(name); }
		return contains;
	}
	
	/**
	 * Extend current function definitions with a function.
	 * @param name
	 * @param function
	 */
	public void storeFunctionDef(FunctionDef function) {
		if(function == null) { return; } // Retrieve function identifier
		String identifier = function.getIdentifier() == null ? "" : function.getIdentifier().getName();
		functions.put(identifier, function);
	}
	
	/**
	 * Extend current function definitions with a collection of functions.
	 * @param functions
	 */
	public void storeFunctionDefs(Collection<FunctionDef> functions) {
		for(FunctionDef function: functions) { storeFunctionDef(function); }
	}
	
	/**
	 * Retrieve collection of function definitions.
	 * @return
	 */
	public Collection<FunctionDef> getFunctionDefs() {
		return functions.values();
	}
	
	/**
	 * Extend current variable definitions with a variable.
	 * @param name
	 * @param value
	 */
	public void storeVariable(String name, Expression value) {
		variables.put(name, value);
	}
	
	/**
	 * Retrieve parent environment.
	 * @return
	 */
	public Environment getParent() {
		return parent;
	}
	
}