/**
 * Interprete the {Module} AST by converting it to HTML code
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 */
function WaebricInterpreter(){	

	/**
	 * Returns the HTML code for the main function and the site mappings
	 * 
	 * @param {Module} module The Abstract Syntax Tree
	 * @param {Boolean} outputFiles
	 * @return {Array} A collection of {WaebricEnvironment}
	 */
	this.interprete = function(module, outputPath){
		//Interprete main function and sites
		var resultMain = [interpreteMain(module)];
		var resultSites = interpreteSites(module);
		var results = resultMain.concat(resultSites);
		
		//Output results
		outputResults(results, outputPath)
		
		return results
	}
	
	/**
	 * Returns the HTML output for the main function
	 * 
	 * @param {Module} module The Abstract Syntax Tree
	 * @return {WaebricEnvironment}
	 */
	function interpreteMain(module){	
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
	function interpreteSites(module){
		//Visit local site mappings
		var environmentLocal = interpreteSite(module);
		
		//Visit dependency mappings
		var environmentDependencies = new Array();
		for(var i = 0; i < module.dependencies.length; i++){
			var dependency = module.dependencies[i];
			var environment = interpreteSites(dependency);
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
	function interpreteSite(module){
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
	
	/**
	 * Outputs all HTML files to the filesystem
	 * 
	 * @param {Array} An array of XML documents
	 */
	function outputResults(waebricEnvironments, outputPath){
		//Check output destination
		if (outputPath == null) {
			print('Unable to write XHTML document. No output path specified.')
			return;
		}
		
		var projectName = waebricEnvironments[0].name		
		for(var i = 0; i < waebricEnvironments.length; i++){			
			var waebricEnvironment = waebricEnvironments[i];
			outputHTMLFile(waebricEnvironment, projectName, outputPath);
		}	
	}
	
	/**
	 * Outputs a single HTML file to the filesystem
	 * 
	 * @param {Object} waebricEnvironment
	 * @param {Object} projectName
	 */
	function outputHTMLFile(waebricEnvironment, projectName, outputPath){
		//Check DOM document
		if (waebricEnvironment.path == '') {
			print('Unable to write XHTML document for file ' + waebricEnvironment.name + '.wae. DOM document is empty.')
			return;
		}
		
		//Determine output path file
		var projectPath = outputPath + projectName + '/'
		var filePath = waebricEnvironment.path.toString();
		var startIndexFileName = filePath.lastIndexOf('/');
		var fileDir = '';
		if (startIndexFileName > -1) {
			fileDir += filePath.substring(0, startIndexFileName)
		}
		
		//Create directories	
		var fDir = new File(projectPath + fileDir);
		if (!fDir.exists()) {
			fDir.mkdirs();
		}
			
		//Write file
		var fw = new FileWriter(projectPath + filePath);
		var bf = new BufferedWriter(fw);
		bf.write(waebricEnvironment.document);
		bf.close();
	}

}

/**
 * Returns the HTML code for the main function and the site mappings
 * 
 * @param {Module} module The Abstract Syntax Tree
 * @return {WaebricInterpreterResult}
 * @exception {WaebricInterpreterException}
 */
WaebricInterpreter.interprete = function(module, outputPath){
	try {		
		var interpreter = new WaebricInterpreter();
		var environments = interpreter.interprete(module, outputPath);
		return new WaebricInterpreterResult(environments);
	}catch(exception if exception instanceof WaebricInterpreterException){
		throw exception;
	}catch(exception){
		throw new WaebricInterpreterException(exception.toString())
	}
}
