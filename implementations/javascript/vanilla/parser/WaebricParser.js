/**
 * Waebric Parser
 *  
 * @author Nickolas Heirbaut
 */
function WaebricParser(){
	
	var exceptions = new Array();
	
	/**
	 * Parses the program source
	 * 
	 * @param {String} path The path of the Waebric program
	 * @return {WaebricParserResult}
	 * @exception {NonExistingModuleException, Error}
	 */
	this.parse = function(path){
		try {
			var module = parseModule(path);
			return new WaebricParserResult(module, exceptions);		
		}catch(exception if (exception instanceof NonExistingModuleException)){
			exceptions.push(exception);
		}catch(exception){
			throw exception;
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
		var programSource = getSourceWaebricProgram(path);	
		
		//Tokenize the module
		print('\n---- Parsing ' + path)
		var start=new Date().getTime();
		var tokenizerResult = WaebricTokenizer.tokenize(programSource);
		var end= new Date().getTime();
		var diff = end-start
		print('miliseconds:' + diff)
		//writeTokenizerResult(tokenizerResult.tokens)
		//Parse the tokenizerResult to a Module
		//var module = WaebricRootParser.parse(tokenizerResult)

		//Parse the dependencies
		//module.dependencies = parseDependencies(path, module);
		//return module;	
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
	 * Returns transitive dependencies for a given module
	 *
	 * @param {String} parentPath The parent path of the waebric program
	 * @param {Module} parentModule The module for which the transitive dependencies will be returned
	 * @return {Array} An array of {Module} elements
	 */
	function parseDependencies(parentPath, parentModule){
		try {
			var dependencies = new Array();
			for (var i = 0; i < parentModule.imports.length; i++) {
				var dependency = parentModule.imports[i];
				var dependencyPath = getDependencyPath(parentPath, dependency);
				var dependencyModule = parseModule(dependencyPath)
				dependencies.push(dependencyModule)
			}
			return dependencies;
		}catch(exception){
			//Unexpected exception thrown. Evaluation terminated
			throw exception;
		}
	}
	
	/**
	 * Returns the path of the dependency based on the parentpath
	 * 
	 * @param {String} parentPath Path of the parent module
	 * @param {Module} dependency The dependency for which the path should be generated
	 * @return {String} The path of the dependency
	 */
	function getDependencyPath(parentPath, dependency){
		try {
			//Determine relative path of parent module towards file system
			var directoriesParent = parentPath.split('/');
			var directoryParent = directoriesParent.slice(0, directoriesParent.length - 1).join('/').concat("/");
			
			//Determine relative path of dependency towards parent module
			var directoryDependency = dependency.moduleId.identifier.replace('.', '/');
			
			//Determine relative path of dependency towards file system
			var path = directoryParent + directoryDependency + ".wae"
			
			return path;
		}catch(exception){
			throw "The path of the dependency couldn't be recognized."
		}
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
		var fileExists = (new File(path)).exists();
		if (fileExists) {
			var parser = new WaebricParser();
			return parser.parse(path);
		}else{
			throw new NonExistingModuleException(path);
		}
	}catch(exception){
		throw new WaebricParserException(exception.message);
	}
}
	
	