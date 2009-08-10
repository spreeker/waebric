/**
 * Waebric Parser
 *  
 * @author Nickolas Heirbaut
 */
function WaebricParser(){
	
	this.exceptions = new Array();
	
	/**
	 * Parses the program source to a Module
	 * 
	 * @param {String} The source of the Waebric program
	 * @return {Module} The parsed Module
	 * @exception 
	 */
	this.parseModule = function(path){
		//try {
			print('---- Parsing ' + path)			
			//Get Waebric program
			var programSource = this.getSourceWaebricProgram(path);	
			//Tokenize the module
			var tokenizerResult = WaebricTokenizer.tokenize(programSource);
			writeTokenizerResult(tokenizerResult.tokens)
			//Parse the tokenizerResult to a Module
			var module = WaebricRootParser.parse(tokenizerResult)
			//Parse the dependencies
			module.dependencies = this.parseDependencies(path, module);
			return module;
		//}catch(exception if (exception instanceof NonExistingModuleException)){
			//Dependency couldn't be found, save as WaebricSemanticException
			//Evaluation will be continued for remaining dependencies
		//	this.exceptions.push(exception);
		//	print('! FAILED !')
		//}catch(exception){
			//Unexpected exception thrown. Evaluation terminated
		//	throw exception;
		//}		
	}
	
	/**
	 * Outputs the result of the tokenizer
	 * @param {Object} tokens
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
	 * @param {String} The path of the waebric program
	 * @param {Module} The module for which the transitive dependencies will be returned
	 * @return An array of {Module}
	 */
	this.parseDependencies = function(parentPath, parentModule){
		var dependencies = new Array();
		
		//Load all dependencies inside the module
	    for (var i = 0; i < parentModule.imports.length; i++) {
			var dependency = parentModule.imports[i];			
	        //try {				
				var dependencyPath = this.getDependencyPath(parentPath, dependency);
	            var dependencyModule = this.parseModule(dependencyPath)
	            dependencies.push(dependencyModule)
			//}catch(exception){
				//Unexpected exception thrown. Evaluation will be terminated				
			//	throw exception;
			//}
	    }	
	    return dependencies;
	}
	
	/**
	 * Returns the path of the dependency based on the parentpath
	 * 
	 * @param {String} Path of the parent module
	 * @param {Module} The dependency for which the path should be generated
	 * @return {String} The path of the dependency
	 */
	this.getDependencyPath = function(parentPath, dependency){
		//try {
			//Determine relative path of parent module towards file system
			var directoriesParent = parentPath.split('/');
			var directoryParent = directoriesParent.slice(0, directoriesParent.length - 1).join('/').concat("/");
			
			//Determine relative path of dependency towards parent module
			var directoryDependency = dependency.moduleId.identifier.replace('.', '/');
			
			//Determine relative path of dependency towards file system
			var path = directoryParent + directoryDependency + ".wae"
			
			return path;
		//}catch(exception){
		//	throw exception;
		//}
	}
	
	/**
	 * Returns the source of a Waebric program
	 *
	 * @param {String} The path of the Waebric program
	 * @return The source of the Waebric program
	 */
	this.getSourceWaebricProgram = function(path){
	    //try {
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
	    //} catch (exception) {
	    //    throw new NonExistingModuleException(path);
	    //}
	}
}

WaebricParser.parseAll = function(path){
	//try {
		var fileExists = (new File(path)).exists();
		if (fileExists) {
			var parser = new WaebricParser();
			var module = parser.parseModule(path);
			return new WaebricParserResult(module, parser.exceptions);
		}else{
			throw "Waebric program doesn't exists";
		}
	//}catch(exception){
	//	throw new WaebricParserException(exception);
	//}
}
	
	