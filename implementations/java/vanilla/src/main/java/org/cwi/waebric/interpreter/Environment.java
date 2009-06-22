package org.cwi.waebric.interpreter;

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
public class Environment {

	private final Map<String, FunctionDef> functions;
	private final Map<String, Expression> variables;
	
	/**
	 * Construct environment.
	 */
	public Environment() {
		functions = new HashMap<String, FunctionDef>();
		variables = new HashMap<String, Expression>();
	}
	
	/**
	 * Construct environment and store function definitions.
	 * @param functions
	 */
	public Environment(Collection<FunctionDef> functions) {
		this(); // Construct maps
		this.setFunctionDefs(functions); // Store functions
	}
	
	/**
	 * Construct environment and similar data to parent environment.
	 * @param e Parent environment
	 */
	public Environment(Environment e) {
		this(e.getFunctionDefs()); // Construct maps and store functions
		for(String variable: e.getVariableNames()) {
			// Store variables
			this.setVariable(variable, e.getVariable(variable));
		}
	}
	
	/**
	 * Retrieve function names.
	 * @return
	 */
	public String[] getFunctionNames() {
		return functions.keySet().toArray(new String[0]);
	}
	
	/**
	 * Retrieve function definition.
	 * @param name
	 * @return
	 */
	public FunctionDef getFunction(String name) {
		return functions.get(name);
	}
	
	/**
	 * Check if function is defined in environment.
	 * @param name
	 * @return
	 */
	public boolean containsFunction(String name) {
		return functions.containsKey(name);
	}
	
	/**
	 * Remove function from environment.
	 * @param name
	 * @return
	 */
	public FunctionDef removeFunction(String name) {
		return functions.remove(name);
	}
	
	/**
	 * Retrieve variable names.
	 * @return
	 */
	public String[] getVariableNames() {
		return variables.keySet().toArray(new String[0]);
	}
	
	/**
	 * Retrieve variable expression.
	 * @param name
	 * @return
	 */
	public Expression getVariable(String name) {
		return variables.get(name);
	}
	
	/**
	 * Check if variable is defined in environment.
	 * @param name
	 * @return
	 */
	public boolean containsVariable(String name) {
		return variables.containsKey(name);
	}
	
	/**
	 * Remove variable from environment.
	 * @param name
	 * @return
	 */
	public Expression removeVariable(String name) {
		return variables.remove(name);
	}
	
	/**
	 * Extend current function definitions with a function.
	 * @param name
	 * @param function
	 */
	public void setFunctionDef(FunctionDef function) {
		String identifier = function.getIdentifier().getName();
		if(! functions.containsKey(identifier)) { functions.put(identifier, function); }
	}
	
	/**
	 * Extend current function definitions with a collection of functions.
	 * @param functions
	 */
	public void setFunctionDefs(Collection<FunctionDef> functions) {
		for(FunctionDef function: functions) { setFunctionDef(function); }
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
	public void setVariable(String name, Expression value) {
		variables.put(name, value);
	}
	
}