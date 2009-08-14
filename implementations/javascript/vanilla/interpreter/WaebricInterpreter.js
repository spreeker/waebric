/**
 * Interprete a {Module} and converts it to HTML code
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 */
function WaebricInterpreter(){	

	/**
	 * Returns the HTML code for the main function and the site mappings
	 * 
	 * @param {Module} module The Abstract Syntax Tree
	 * @return {Array} A collection of {WaebricEnvironment}
	 */
	this.interprete = function(module){
		var environmentMainFunction = [getEnvironmentMainFunction(module)];
		var environmentSiteMappings = getEnvironmentsSiteMappings(module);
		return environmentMainFunction.concat(environmentSiteMappings);
	}
	
	/**
	 * Returns the HTML output for the main function
	 * 
	 * @param {Module} module The Abstract Syntax Tree
	 * @return {WaebricEnvironment}
	 */
	function getEnvironmentMainFunction(module){	
		var visitor = new WaebricInterpreterVisitor()
		var environment = new WaebricEnvironment();
		var document = new DOM(); 
		
		//Preprocessing
		module.accept(visitor.getModuleVisitor(environment, document));	
		
		//Visit main function and write HTML output to document
		module.accept(visitor.getMainVisitor(environment, document));
		environment.document = document;
		
		//Returns the environment and the document
		return environment;
	}
	
	/**
	 * Returns the HTML output for the site mappings of a module,
	 * including for the transitive dependencies
	 * 
	 * @param {Module} module The Abstract Syntax Tree
	 * @return {Array} A collection of {WaebricEnvironment}
	 */
	function getEnvironmentsSiteMappings(module){
		//Visit local site mappings
		var environmentLocal = getEnvironmentLocalSiteMapping(module);
		
		//Visit dependency mappings
		var environmentDependencies = new Array();
		for(var i = 0; i < module.dependencies.length; i++){
			var dependency = module.dependencies[i];
			var environment = getEnvironmentsSiteMappings(dependency);
			environmentDependencies = environmentDependencies.concat(environment);		
		}
		
		return environmentLocal.concat(environmentDependencies);
	}
	
	/**
	 * Returns the HTML output for the site mappings function
	 * 
	 * @param {Module} module The Abstract Syntax Tree
	 * @return {Array} A collection of {WaebricEnvironment}
	 */
	function getEnvironmentLocalSiteMapping(module){
		var environments = new Array();
		for (var i = 0; i < module.site.mappings.length; i++) {
			//Visit module (preprocessing)
			var visitor = new WaebricInterpreterVisitor();
			var environment = new WaebricEnvironment();
			var document = new DOM(); 
			module.accept(visitor.getModuleVisitor(environment, document));		
			
			//Visit main function and write HTML output to document
			//Exceptions are logged in the environment
			var mapping = module.site.mappings[i]
			mapping.accept(visitor.getMappingVisitor(environment, document));
			environment.document = document;
			
			//Returns the environment and the document
			environments.push(environment);
		}
		return environments;
	}
}

/**
 * Returns the HTML code for the main function and the site mappings
 * 
 * @param {Module} module The Abstract Syntax Tree
 * @return {WaebricInterpreterResult}
 * @exception {WaebricInterpreterException}
 */
WaebricInterpreter.interprete = function(module){
	try {
		var interpreter = new WaebricInterpreter();
		var environments = interpreter.interprete(module);
		return new WaebricInterpreterResult(environments);
	}catch(exception if exception instanceof WaebricInterpreterException){
		throw exception;
	}catch(exception){
		throw new WaebricInterpreterException(exception.toString())
	}
}
