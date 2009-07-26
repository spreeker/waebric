/**
 * Interprete the module and returns the HTML output
 */
function WaebricInterpreter(){
	
}

/**
 * Returns the HTML output for the main function and the site mappings
 * 
 * @param {Module} A module
 * @return {Array} An array of {WaebricEnvironment}
 */
WaebricInterpreter.interpreteAll = function(module){	
	var environmentMainFunction = [getEnvironmentMainFunction(module)];
	var environmentSiteMappings = getEnvironmentsSiteMappings(module);
	var environments = environmentMainFunction.concat(environmentSiteMappings);
	return new WaebricInterpreterResult(environments);
}

/**
 * Returns the HTML output for the main function
 * 
 * @param {Array} An array of {WaebricEnvironment}
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
 * @param {Array} An array of {WaebricEnvironment}
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
 * @param {Array} an array of WaebricEnvironments
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
