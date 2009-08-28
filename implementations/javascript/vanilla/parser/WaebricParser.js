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
	function parseModule(path){
		try {
			//Get Waebric program	
			//print('\n---- Loading ' + path)
			var programSource = getSourceWaebricProgram(path);
			
			//Tokenize the module		
			//print('---- Tokenizing ' + path)			
			
			var tokenizerResult = WaebricTokenizer.tokenize(programSource, path);
			
			//Parse the tokenizerResult to a Module
			//print('---- Parsing ' + path)
			var module = WaebricRootParser.parse(tokenizerResult, path)
			
			//Parse the dependencies
			module.dependencies = parseDependencies(path, module);
			return module;
		}catch(exception){
			print('---- Parsing/tokenizing failed!\n')	
			throw exception;
		}
	}
	
	/**
	 * Outputs the result of the tokenizer
	 * 
	 * @param {Array} tokens An array of {WaebricToken}
	 */	
	function writeTokenizerResult(tokens){
		var text = ""
		for(tokenIndex in tokens){		
			token = (tokens[tokenIndex])	
			text += (token.type + ' : ' + token.value + '\n');
		}
		
		var fw = new FileWriter('output_scanner.txt');
		var bf = new BufferedWriter(fw);
		bf.write(text);
		bf.close();
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
			var dependencyPath = getDependencyPath(parentPath, dependency);
			var fileExists = (new File(dependencyPath)).exists();
			if (fileExists) {
				var dependencyModule = parseModule(dependencyPath)
				dependencies.push(dependencyModule)
			}else{
				print('---- Loading failed!')					
				exceptionList.push(new NonExistingModuleException(dependencyPath));
			}					
		}
		return dependencies;
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
		return parser.parse(path);
	}catch(exception if exception instanceof WaebricParserException){
		throw exception;
	}catch(exception){
		throw new WaebricParserException(exception.message, null, path, exception);
	}
}