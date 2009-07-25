/**
 * An environment used during Semantic Validation which
 * hold all data (functions, variables and exceptions) of 
 * a certain environment such as a module, functionmodule,
 * a let statement or an each statement
 */

function Environment(){	
	
	this.parent = null;
	this.children = new Array();
	this.functions = new Array();
	this.variables = new Array();
	this.exceptions = new Array();
	this.dependencies = new Array();
	
	this.type = 'module';
	this.name = '';
	
	/**
	 * Adds a function to the functionlist
	 * @param {FunctionDefinition} func
	 */
	this.addFunction = function(func){
		this.functions.push(func);
	}
	
	/**
	 * Adds a variable to the variable list
	 * @param {VariableName} variable
	 */
	this.addVariable = function(variable){
		this.variables.push(variable);
	}
	
	/**
	 * Adds an exception to the exceptionlist
	 * @param {WaebricSemanticException} exception
	 */
	this.addException = function(exception){
		this.exceptions.push(exception);
	}
	
	/**
	 * Adds an environment to the current environment
	 * The new environment becomes child of the current environment
	 * @return The new environment
	 */
	this.addEnvironment = function(type){
		var env = new Environment();
		env.parent = this;
		env.type = type
		this.children.push(env);		
		return env;
	}
	
	/**
	 * Adds new dependency to the current environment
	 * The new environment becomes child of the current environment
	 * @return The new environment
	 */
	this.addDependency = function(type){
		var env = new Environment();
		env.parent = this;
		env.type = type
		this.dependencies.push(env);		
		return env;
	}
	
	/**
	 * Adds new dependency to the current environment
	 * The new environment becomes child of the current environment
	 * @return The new environment
	 */
	this.addExistingDependency = function(existingDependency){
		this.dependencies.push(existingDependency);		
	}
	
	
	/**
	 * Returns a function found in the root environment
	 * -> Root module environment + dependencies environment
	 * 
	 * @param {String} funcName
	 * @return The requested function. Null if not found.
	 */
	this.getFunction = function(funcName){
		var root = this.getRoot();		
		return root.getLocalFunction(funcName)
	}
	
	/**
	 * Returns a function found in the current module environment
	 * -> Module environment + dependencies environment
	 * 
	 * @param {String} funcName
	 * @return The requested function. Null if not found.
	 */
	this.getLocalFunction = function(funcName){		
		//Search function local environment
		for(var i = 0; i < this.functions.length; i++){
			var func = this.functions[i];
			if(func.functionName == funcName){
				return func;
			}
		}
		
		//Search in the dependencies of the root module
		for (var i = 0; i < this.dependencies.length; i++) {
			var dependency = this.dependencies[i];		
			var func = dependency.getLocalFunction(funcName);
			if(func != null){
				return func;
			}
		}
		
		//Search function in parent environment but
		//do not search function outside the module itself
		if(this.parent != null && this.type != 'module') {
			return this.parent.getLocalFunction(funcName)
		}
		
		return null;
	}
	
	/**
	 * Returns wheter a function exists in the root environment and its dependencies
	 * 
	 * @param {String} funcName
	 * @return {Boolean} The existence of the requested function. False if not found.
	 */
	this.containsFunction = function(funcName){
		if(this.getFunction(funcName) != null){
			return true;
		}
		return false;
	}
	
	/**
	 * Returns wheter a function in the current environment and its dependencies
	 * 
	 * @param {String} funcName
	 * @return {Boolean} The existence of the requested function. False if not found.
	 */
	this.containsLocalFunction = function(funcName){
		if(this.getLocalFunction(funcName) != null){
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the root environment with no parents.
	 * 
	 * @return {Environment} The root environment
	 */
	this.getParentModule = function(){
		if (this.type != 'module') {			
			return this.parent.getParentModule();
		} else {
			return this.name;
		}
	}
	
	/**
	 * Returns the root environment with no parents.
	 * 
	 * @return {Environment} The root environment
	 */
	this.getRoot = function(){
		if (this.parent != null) {			
			return this.parent.getRoot();
		} else {
			return this;
		}
	}
	
	/**
	 * Returns a dependency found in the root environment or its transitive dependencies
	 * 
	 * @param {Object} dependencyName
	 */
	this.getDependency = function(dependencyName){		
		var root = this.getRoot();
		return root.getLocalDependency(dependencyName)
	}
	
	this.getLocalDependency = function(dependencyName){
		for(var i = 0; i < this.dependencies.length; i++){		
			var dependency = this.dependencies[i];		
			if(dependency.name == dependencyName){	
				return dependency;
			}else{				
				var existingDependency = dependency.getLocalDependency(dependencyName); 				
				if(existingDependency != null){						
					return existingDependency;
				}
			}
		}
		return null;
	}
		
	
	/**
	 * Returns a variable from the variablelist in the current environment or in it's
	 * parent environment. If no variable is found in the current environment,
	 * nor in the parent environment, then null is returned.
	 * 
	 * @param {String} variable
	 * @return The requested variable. Null if not found
	 */
	this.getVariable = function(variable){
		//Search function local environment
		for(var i = 0; i < this.variables.length; i++){
			var _var = this.variables[i];
			if(variable == _var){
				return _var;
			}
		}
		
		//Search function in parent environment
		if(this.parent != null && this.type != 'func-def') {
			return this.parent.getVariable(variable)
		}
		
		return null;
	}
	
	/**
	 * Returns all exceptions found in the current environment or in it's children's
	 * environment. 
	 * 
	 * @return Array of exceptions
	 */
	this.getExceptions = function(){
		var exceptionList = new Array();
		exceptionList = exceptionList.concat(this.exceptions);
		for(var i = 0; i < this.children.length; i++){
			var child = this.children[i];
			exceptionList = exceptionList.concat(child.getExceptions());
		}
		for(var i = 0; i < this.dependencies.length; i++){
			var dependency = this.dependencies[i];
			exceptionList = exceptionList.concat(dependency.getExceptions());
		}
		return exceptionList;
	}
}
