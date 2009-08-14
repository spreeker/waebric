/**
 * Waebric Parser
 * 
 * Parses a Waebric program to xHTML code
 * - Reads in the Waebric program from the filesystem
 * - Tokenizes the Waebric file
 * - Parses the tokens to an Abstract Syntax Tree {Module}
 * 
 * @author Nickolas Heirbaut
 */
function WaebricParser(){
	
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
			return new WaebricParserResult(module);	
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
		//Get Waebric program	
		print('\n---- Loading ' + path)
		var programSource = getSourceWaebricProgram(path);

		//Tokenize the module		
		print('---- Tokenizing ' + path)
		var tokenizerResult = WaebricTokenizer.tokenize(programSource, path);
		writeTokenizerResult(tokenizerResult.tokens)
		//Parse the tokenizerResult to a Module
		print('---- Parsing ' + path)
		var module = WaebricRootParser.parse(tokenizerResult, path)

		//Parse the dependencies
		module.dependencies = parseDependencies(path, module);
		return module;		
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
			try {
				var dependency = parentModule.imports[i];
				var dependencyPath = getDependencyPath(parentPath, dependency);
				var dependencyModule = parseModule(dependencyPath)
				dependencies.push(dependencyModule)
			}catch(exception){
				print('---- Loading failed!\n')					
				exceptions.push(exception);
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
		var directoryDependency = dependency.moduleId.identifier.replace('.', '/');
		
		//Determine relative path of dependency towards file system
		var path = directoryParent + directoryDependency + ".wae"
		
		return path;
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
 * Parses the program source
 * 
 * @param {String} The path of the Waebric program
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
	
	