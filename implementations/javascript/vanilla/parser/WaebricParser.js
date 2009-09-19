/**
 * Vanilla parser for Waebric language
 *
 * The WaebricParser converts a Waebric program into an AST (Module).
 * - Reads in the Waebric program from the filesystem using Rhino/Java
 * - Tokenizes the Waebric file
 * - Parses the tokens to an Abstract Syntax Tree {Module}
 * 
 * Imports in the Waebric program are automaticly loaded from the filesystem and parsed 
 * to an new AST (Module). The new AST is then added to the parent AST (Module) under the
 * property "dependencies".
 *
 * Translated from the SDF specification (Meta-Environment: www.meta-environment.org/):
 * http://code.google.com/p/waebric/source/browse/trunk/doc/waebric.pdf
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 */
function WaebricParser(){
	
	var exceptionList = new Array();	
	var allParsedModules = new Array();
	
	/**
	 * Parses the program source
	 * 
	 * @param {String} path The path of the Waebric program
	 * @return {WaebricParserResult}
	 */
	this.parse = function(path){
		var fileExists = (new File(path)).exists();
		if (fileExists) {
			var module = parseModule(path);			
			return new WaebricParserResult(module, exceptionList);	
		}else{
			throw new NonExistingModuleException(path);
		}
	}
	
	/**
	 * Parses the program source
	 * 
	 * @param {String} path The path of the Waebric program
	 * @return {Module}
	 */
	function parseModule(path, parentModule){
		try {
			print('---- Parsing module ' + path)
			//Get Waebric program
			//fileloaderTimer.start();
			var programSource = getSourceWaebricProgram(path);
			//fileloaderTimer.stop();
			
			//Tokenize the module					
			//lexicalTimer.start();
			var tokenizerResult = WaebricTokenizer.tokenize(programSource, path);
			//lexicalTimer.stop();
			
			//Parse the tokenizerResult to a Module
			//syntacticTimer.start();
			var module = WaebricRootParser.parse(tokenizerResult, path)			
			module.parent = parentModule;
			allParsedModules.push(module);

			//Parse the dependencies
			module.dependencies = parseDependencies(path, module);
			
			//syntacticTimer.stop();
			return module;
		}catch(exception){	
			throw exception;
		}
	}
	
	/**
	 * Returns the (transitive) dependencies for a given module
	 *
	 * @param {String} parentPath The parent path of the waebric program
	 * @param {Module} parentModule The module for which the transitive dependencies will be returned
	 * @return {Array} An array of {Module} elements
	 */
	function parseDependencies(parentPath, parentModule){		
		var dependencies = new Array();		
		for (var i = 0; i < parentModule.imports.length; i++) {
			var dependency = parentModule.imports[i];
			if (!isCyclicImport(dependency.moduleId.toString(), parentModule)) {
				var parsedDependency = parseDependency(dependency, parentPath, parentModule);
				dependencies.push(parsedDependency);
			}				
		}
		return dependencies;
	}
	
	function isCyclicImport(importName, parentModule){
		if(parentModule.parent != null){
			if(parentModule.parent.moduleId.toString() == importName){
				return true;
			}else{
				return isCyclicImport(importName, parentModule.parent);
			}
		}
		return false
	}
	
	function parseDependency(dependency, parentPath, parentModule){				
		var existingModule = getExistingModule(dependency.moduleId);
		if (existingModule) {	
			existingModule.parent = parentModule;
			return existingModule;
		} else {
			var dependencyPath = getDependencyPath(parentPath, dependency);
			return parseNewDependency(dependencyPath, parentModule);
		}			
	}
	
	function parseNewDependency(dependencyPath, parentModule){
		var fileExists = (new File(dependencyPath)).exists();
		if (fileExists) {
			return parseModule(dependencyPath, parentModule)
		} else {
			exceptionList.push(new NonExistingModuleException(dependencyPath));
			return null;
		}
	}
	
	function getExistingModule(moduleId){
		for(var i = 0; i < allParsedModules.length; i++){
			var parsedDependency = allParsedModules[i];
			if(moduleId.toString() == parsedDependency.moduleId.toString()){
				return parsedDependency;
			}
		}
		return null;
	}	
	
	/**
	 * Returns the path of the dependency based on the parentpath
	 * 
	 * @param {String} parentPath Path of the parent module
	 * @param {Module} dependency The dependency for which the path should be generated
	 * @return {String} The path of the dependency
	 */
	function getDependencyPath(parentPath, dependency){
		//Determine relative path of parent module towards file system
		var directoriesParent = parentPath.split('/');
		var directoryParent = directoriesParent.slice(0, directoriesParent.length - 1).join('/').concat("/");
		
		//Determine relative path of dependency towards parent module
		var directoryDependency = dependency.moduleId.identifier.replace(/\./g, '/');

		//Determine relative path of dependency towards file system
		var path = directoryParent + directoryDependency + ".wae"
		return path;
	}
	
	/**
	 * Returns the directory path of the dependency
	 * 
	 * @param {Object} imprt
	 * @return {String} The directory path of the dependency
	 */
	this.getCurrentDirectoryPath = function(parentPath, imprt){
		//Determine relative path of parent module towards file system
		var directoriesParent = parentPath.split('/');
		var directoryParent = directoriesParent.slice(0, directoriesParent.length - 1).join('/').concat("/");
		
		//Determine relative path of dependency towards parent module
		var directoriesImport = imprt.moduleId.identifier.split('.');
		var directoryImport = directoriesImport.slice(0, directoriesImport.length - 1).join('/').concat("/");

		//Determine relative path of dependency towards file system
		return(directoryParent + directoryImport)
	}	
	
	/**
	 * Returns the source of a Waebric program
	 *
	 * @param {String} path The path of the Waebric program
	 * @return {String} The source of the Waebric program
	 */
	function getSourceWaebricProgram(path){
	    try {
	        var fis = new FileInputStream(path);
	        var bis = new BufferedInputStream(fis);
	        var dis = new DataInputStream(bis);
	        
	        var program = '';
	        while (dis.available() != 0) {
	            program += dis.readLine() + '\n';
	        }
	        fis.close();
	        bis.close();
	        dis.close();
	        return program;
	    } catch (exception) {
	        throw new NonExistingModuleException(path);
	    }
	}
}

/**
 * Parses a Waebric program to a {Module} AST
 * 
 * @param {String} The path of the Waebric program on the filesystem
 * @return {WaebricParserResult}
 * @exception {WaebricParserException}
 */
WaebricParser.parse = function(path){
	try {
		var parser = new WaebricParser();		
		var result = parser.parse(path);		
		//lexicalTimer.write(reportPath, ";")
		//syntacticTimer.write(reportPath, ";")
		//fileloaderTimer.write(reportPath, ";")
		return result;
	}catch(exception if exception instanceof WaebricParserException){
		throw exception;
	}catch(exception){
		throw new WaebricParserException(exception.message, null, path, exception);
	}
}