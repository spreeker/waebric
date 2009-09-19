load('../vanilla/rhino/rhino-imports.js')

/**
 * Outputs the HTML code to a set of files
 * 
 * @param {Array} An array of XML documents
 */
function createHTML(waebricEnvironments, siteName){
	for(var i = 0; i < waebricEnvironments.length; i++){			
		var waebricEnvironment = waebricEnvironments[i];
		if (waebricEnvironment.path != '') {
			var rootPath = '../vanilla/generated_html/';
			var sitePath = siteName + '/' + waebricEnvironment.path.toString();
			createDirectories(rootPath, sitePath)
			
			//Write file
			var fw = new FileWriter(rootPath + sitePath);
			var bf = new BufferedWriter(fw);
			bf.write(waebricEnvironment.document);
			bf.close();
		}else{
			print('Unable to write XHTML document for file ' + waebricEnvironment.name + '.wae. DOM document is empty.')
		}
	}	
}

/**
 * Creates the directory structure for the HTML output
 * 
 * @param {String} rootPath
 * @param {String} sitePath
 */
function createDirectories(rootPath, sitePath){		
	var fDir = new File(rootPath);
	if (!fDir.exists()) {
		fDir.mkdir();
	}
	
	var lastDirectory = rootPath;
	var pathElements = sitePath.split('/')
	for(var itemIndex = 0; itemIndex < pathElements.length - 1; itemIndex++){
		var pathElement = pathElements[itemIndex];
		var fDir = new File(lastDirectory+pathElement);
		if (!fDir.exists()) {
			fDir.mkdir();
		}
		lastDirectory += pathElement + '/';
	}
	
}

/**
 * Converts a Waebric Program to HTML
 * 
 * @param {String} path
 * @param {String} siteName
 */
function convertToHTML(path, siteName){	
	try {
		//Parsing
		var parserResult = WaebricParser.parse(path);
		
		//Validating		
		var validatorResult = WaebricValidator.validate(parserResult.module)
		print(validatorResult)
		
		//Interpreting
		var interpreterResult = WaebricInterpreter.interprete(parserResult.module);
		
		//Output results		
		createHTML(interpreterResult.environments, siteName);
	}catch(exception){
		print(exception.toString());
	}
}

convertToHTML('../../../../demos/source/lava/lava.wae', 'lava');
