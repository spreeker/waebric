/**
 * The WaebricEnvironment holds all functions, variables, exceptions, 
 * dependencies and children. 
 */

function WaebricEnvironment(){	
	
	this.parent = null;
	this.children = new Array();
	this.functions = new Array();
	this.variables = new Array();
	this.exceptions = new Array();
	this.dependencies = new Array();	
	
	this.path = '';
	this.type = 'module';
	this.name = '';	
	this.document;
	
	/**
	 * Adds a function to the functionlist
	 * 
	 * @param {FunctionDefinition} func The function definition
	 */
	this.addFunction = function(func){
		this.functions.push(func);
	}
	
	/**
	 * Adds a variable to the variable list
	 * 
	 * @param {String} variable The name of the variable
	 * @param {Object} value The value of the variable
	 */
	this.addVariable = function(name, value){
		var foundVariable = this.getLocalVariable(name);
		if (foundVariable == null) {
			this.variables.push(new Variable(name, value));
		}else{
			foundVariable.value = value
		}
	}
	
	/**
	 * Adds an environment to the current environment
	 * The new environment becomes child of the current environment
	 * 
	 * @param {String} The type of the environment
	 * @return {WaebricEnvironment}The new environment
	 */
	this.addEnvironment = function(type){
		var env = new WaebricEnvironment();
		env.parent = this;
		env.type = type		
		this.children.push(env);	
		return env;
	}
	
	/**
	 * Adds new dependency to the current environment
	 * The new environment becomes child of the current environment
	 * 
	 * @return {WaebricEnvironment} The new environment
	 */
	this.addDependency = function(type){
		var env = new WaebricEnvironment();
		env.parent = this;
		env.type = type
		this.dependencies.push(env);		
		return env;
	}
	
	/**
	 * Adds new dependency to the current environment
	 * The new environment becomes child of the current environment
	 * 
	 * @return {WaebricEnvironment} The new environment
	 */
	this.addExistingDependency = function(existingDependency){
		this.dependencies.push(existingDependency);		
	}
	
	
	/**
	 * Returns a function found in the root environment
	 * -> Root module environment + dependencies environment
	 * 
	 * @param {String} funcName
	 * @return {FunctionDefinition} The requested function. Null if not found.
	 */
	this.getFunction = function(funcName){
		var root = this.getRootModule();		
		return root.getLocalFunction(funcName);
	}
	
	/**
	 * Returns a function found in the current module environment
	 * -> Module environment + dependencies environment
	 * 
	 * @param {String} funcName
	 * @return {FunctionDefinition} The requested function. Null if not found.
	 */
	this.getLocalFunction = function(funcName){	
		//Search function local environment
		for(var i = 0; i < this.functions.length; i++){
			var func = this.functions[i];
			if(func.functionName == funcName){
				return func;
			}
		}
		
		//Search in the dependencies of the module
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
	 * Returns the parent environment
	 * 
	 * @return {WaebricEnvironment} The root environment
	 */
	this.getParentModule = function(){
		if (this.type != 'module') {			
			return this.parent.getParentModule();
		} else {
			return this;
		}
	}
	
	/**
	 * Returns the root environment with no parents.
	 * 
	 * @return {WaebricEnvironment} The root environment
	 */
	this.getRootModule = function(){
		if (this.parent != null) {			
			return this.parent.getRootModule();
		} else {
			return this;
		}
	}
	
	/**
	 * Returns a dependency found in the root environment or its transitive dependencies
	 * 
	 * @param {String} dependencyName The name of the dependency
	 * @return {Module} The requested Module. Null if not found
	 */
	this.getDependency = function(dependencyName){		
		var root = this.getRootModule();
		return root.getLocalDependency(dependencyName)
	}
	
	/**
	 * Returns a dependency found in the transitive dependencies of the current environment
	 * 
	 * @param {String} dependencyName The name of the dependency
	 * @return {Module} The requested dependency. Null if not found.
	 */
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
	 * @param {String} variable The name of the variable
	 * @return {Object} The requested variable. Null if not found.
	 */
	this.getVariable = function(name){
		//Search function local environment
		for(var i = 0; i < this.variables.length; i++){
			var _var = this.variables[i];
			if(name == _var.name){			
				return _var;
			}
		}
		
		//Search function in parent environment
		if(this.parent != null && this.type != 'func-def') {
			return this.parent.getVariable(name)
		}		
		
		return null;
	}
	
	/**
	 * Returns a variable from the variablelist in the current environment.
	 * If no variable is found in the current environment, then null is returned.
	 * 
	 * @param {String} variable The name of the variable
	 * @return {Object} The requested variable. Null if not found.
	 */
	this.getLocalVariable = function(name){				
		//Search function local environment
		for(var i = 0; i < this.variables.length; i++){
			var _var = this.variables[i];			
			if(name == _var.name){
				return _var;
			}
		}
		return null;
	}
	
	/**
	 * Checks whether the variable exists in the current environment or in it's
	 * parent environment. 
	 * 
	 * @param {String} name The name of the variable
	 * @param {Boolean}
	 */
	this.containsVariable = function(name){
		if(this.getVariable(name) != null){
			return true
		}
		return false;
	}
	
	/**
	 * Checks whether the variable exists in the current environment.
	 * 
	 * @param {String} name The name of the variable
	 * @return {Boolean}
	 */
	this.containsLocalVariable = function(name){
		if(this.getLocalVariable(name) != null){
			return true
		}
		return false;
	}
	
	/**
	 * Returns all exceptions found in the current environment or in it's children's
	 * environment. 
	 * 
	 * @return {Array} Collection of exceptions
	 */
	this.getExceptions = function(){
		if(this.parent != null){
			this.parent.getExceptions();
		}else{
			return this.exceptions;
		}
	}
	
	/**
	 * Adds an exception to the exceptionlist
	 * 
	 * @param {WaebricSemanticException} exception The exception
	 */
	this.addException = function(exception){
		if (this.parent != null) {
			this.parent.addException(exception);
		} else {
			this.exceptions.push(exception);
		}
	}
}

/** 
 * Specifies a variable object used in the WaebricEnvironment
 */
function Variable (name, value){
	this.name = name;
	this.value = value;	
	this.toString = function(){
		this.name;
	}
}
