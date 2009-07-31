
function WaebricParser(){
		
	this.currentToken;
	
	/**
     * Parses the input to a MODULE AST
     * @param {String} The tokenizer's output
     */
	this.parseAll = function(tokenizerResult){
		//Store first token globally
		this.currentToken = new WaebricParserToken(tokenizerResult.tokens, 0)

		//Start parsing the module
		return this.parseModule(this.currentToken);
	}
	
	/**
	 * Parses the module root 
	 * "Module" ModuleId ModuleElements*
	 */
	this.parseModule = function(token){
		this.currentToken = token;
		if(WaebricToken.KEYWORD.MODULE.equals(this.currentToken.value)){
			var moduleId = this.parseModuleId(this.currentToken.nextToken());
			var moduleElements = this.parseModuleElement(this.currentToken);	
			return new Module(moduleId, moduleElements);
		}

		print('Error parsing module. Expected keyword Module but current token is ' + this.currentToken.value);				
	}
	
	/**
	 * Parses the ModuleID 
	 * ModuleId = IdCon listOf("." IdCon)
	 */
	this.parseModuleId = function(token){
		this.currentToken = token;
		
		if(this.currentToken.value instanceof WaebricToken.IDENTIFIER){	
			var value = "";	
			do{				
				value += this.currentToken.value;
				this.currentToken = this.currentToken.nextToken();				
			}while (this.currentToken.value instanceof WaebricToken.IDENTIFIER 
				||  this.currentToken.value == WaebricToken.SYMBOL.DOT)	
				
			return new ModuleId(value)
		}
		
		print('Error parsing moduleid. Expected ModuleId but current token is ' + this.currentToken.value);		
	}
	
	/**
	 * Parses a ModuleElement
	 * 
	 * ModuleElement = "import" ModuleId
	 * 				 | "site" (Mapping ";")* "end"
	 * 			 	 | "def" IdCon Formals Statement* "end"
	 */
	this.parseModuleElement = function(token){
		this.currentToken = token;
		var moduleElements = new Array();
		
		while (this.currentToken.hasNextToken()) {	
			if (WaebricToken.KEYWORD.IMPORT.equals(this.currentToken.value)) {				
				var imprt = this.parseImport(this.currentToken.nextToken());
				moduleElements.push(imprt);
			} else if (WaebricToken.KEYWORD.SITE.equals(this.currentToken.value)) {
				var site = this.parseSite(this.currentToken.nextToken());
				moduleElements.push(site);
			} else if (WaebricToken.KEYWORD.DEF.equals(this.currentToken.value)) {
				var def = this.parseFunctionDefinition(this.currentToken.nextToken());
				moduleElements.push(def);
			} else {
				//print('Error parsing module elements. Expected IMPORT/SITE/DEF but current token is ' + this.currentToken);
			}
		}
		return moduleElements;			
	}
	
	/**
	 * Parses an Import 
	 * Import = "import" ModuleId;
	 */
	this.parseImport = function(token){
		var moduleId = this.parseModuleId(token)
		return new Import(moduleId);
	}
	
	/**
	 * Parses a Site  
	 * Site = "site" (Mapping ";")* "end"
	 */
	this.parseSite = function(token){
		this.currentToken = token;
		
		if(WaebricToken.KEYWORD.END.equals(this.currentToken)){
			return new Site([]);
		}else{
			var mappings = this.parseMappings(this.currentToken);
			this.currentToken = this.currentToken.nextToken(); //Goto "END"
			if(WaebricToken.KEYWORD.END.equals(this.currentToken.value)){
				return new Site(mappings);
			}
			print('Error parsing Site. Expected END keyword but found ' + this.currentToken.value);		
		}
	}
	
	/**
	 * Parses a Site Mapping 
	 * Mapping = Path ":" Markup
	 */
	this.parseMappings = function(token){		
		this.currentToken = token;
				
		var path = this.parsePath(this.currentToken);		
		if(this.currentToken.value == WaebricToken.SYMBOL.COLON){
			var markup = this.parseMarkup(this.currentToken.nextToken());
			return new Mapping(path, markup);
		}
		
		print('Error parsing Mapping. Expected colon after path but token has value ' + this.currentToken.value);
	}
	
	/**
	 * Parses Markup
	 * 
	 * Markup = Designator Arguments
	 * 		  | Designator
	 */
	this.parseMarkup = function(token){
		this.currentToken = token;
		
		if(this.currentToken.value instanceof WaebricToken.IDENTIFIER){
			var designator = this.parseDesignator(this.currentToken);
			if (this.currentToken.nextToken().value == WaebricToken.SYMBOL.LEFTRBRACKET) {
				var arguments = this.parseArguments(this.currentToken.nextToken());
				return new MarkupCall(designator, arguments);
			}
			return designator;
		}
		
		print('Error parsing Markup. Expected IDENTIFIER MARKUP but found ' + this.currentToken.value);
	}
	
	this.parseDesignator = function(token){
		this.currentToken = token;
		if (this.currentToken.value instanceof WaebricToken.IDENTIFIER) {
			var idCon = this.currentToken.value;
			var attributes = this.parseAttributes(this.currentToken);
			return new DesignatorTag(idCon, attributes);
		}
		print('Error parsing Designator. Expected IDENTIFIER but found ' + this.currentToken.value);
	}
	
	this.isAttribute = function(value){
		var regExp = new RegExp("[#.$:@]");
		return value.match(regExp);
	}
	
	this.parseArguments = function(token){
		this.currentToken = token.nextToken();
		var arguments = new Array();
		while(this.currentToken.value != WaebricToken.SYMBOL.RIGHTRBRACKET){	
			var argument = this.parseArgument(this.currentToken);
			arguments.push(argument);
		}
		return arguments;
	}

	this.parseArgument = function(token){
		this.currentToken = token;	
		if(this.currentToken.value instanceof WaebricToken.IDENTIFIER && this.currentToken.nextToken().value == WaebricToken.SYMBOL.EQ){
			return this.parseAttributeArgument(this.currentToken);
		}else{
			return this.parseRegularArgument(this.currentToken);
		}
	}
	
	this.parseRegularArgument = function(token){
		this.currentToken = token;		
		return this.parseExpression(this.currentToken);
	}
	
	this.parseAttributeArgument = function(token){
		this.currentToken = token;
		var idCon = this.currentToken.value;
		var expression = this.parseExpression(this.currentToken.nextToken().nextToken()); //Skip "="
		return new Argument(idCon, expression);
	}
	
	this.parseExpression = function(token){
		this.currentToken = token;		
		this.currentToken = token.nextToken();	
		return "NYI"	
	}
	
	this.parseAttributes = function(token){
		this.currentToken = token;
		var attributes = new Array();
		while(this.isAttribute(this.currentToken.nextToken().value)){
			var attribute = this.parseAttribute(this.currentToken.nextToken());			
			attributes.push(attribute);
		}
		return attributes;
	}
	
	this.parseAttribute = function(token){
		this.currentToken = token
		if(this.currentToken.value == WaebricToken.SYMBOL.CROSSHATCH){
			return this.parseIDAttribute(this.currentToken.nextToken());
		}else if(this.currentToken.value == WaebricToken.SYMBOL.DOT){
			return this.parseClassAttribute(this.currentToken.nextToken());
		}else if(this.currentToken.value == WaebricToken.SYMBOL.DOLLAR){
			return this.parseNameAttribute(this.currentToken.nextToken());
		}else if(this.currentToken.value == WaebricToken.SYMBOL.COLON){
			return this.parseTypeAttribute(this.currentToken.nextToken());
		}else if(this.currentToken.value == WaebricToken.SYMBOL.AT){
			return this.parseWidthHeightAttribute(this.currentToken.nextToken());
		}
		print('Error parsing attribute.')
	}
	
	this.parseIDAttribute = function(token){
		this.currentToken = token;
		var idValue = this.parseIdentifierValue(this.currentToken);
		return new IdAttribute(idValue);
	}
	
	this.parseClassAttribute = function(token){
		this.currentToken = token;
		var classValue = this.parseIdentifierValue(this.currentToken); 
		return new ClassAttribute(classValue);
	}
	
	this.parseNameAttribute = function(token){
		this.currentToken = token;
		var nameValue = this.parseIdentifierValue(this.currentToken); 
		return new NameAttribute(nameValue);
	}
	
	this.parseTypeAttribute = function(token){
		this.currentToken = token;
		var typeValue = this.parseIdentifierValue(this.currentToken); 
		return new TypeAttribute(typeValue);
	}
	
	this.parseWidthHeightAttribute = function(token){
		this.currentToken = token;
		var widthValue = this.parseNaturalValue(this.currentToken);
		if( this.currentToken.nextToken().value == WaebricToken.SYMBOL.PERCENT){
			this.currentToken = this.currentToken.nextToken().nextToken();	
			var heightValue = this.parseNaturalValue(this.currentToken);			
			return new WidthHeightAttribute(widthValue, heightValue);
		}else{
			return new WidthAttribute(widthValue);
		}		
	}
	
	this.parseIdentifierValue = function(token){
		this.currentToken = token;
		if(this.currentToken.value instanceof WaebricToken.IDENTIFIER){
			return this.currentToken.value;
		}
		print('Error parsing Attribute value. Expected IDENTIFIER value but current token is ' + this.currentToken.value);
	}
	
	this.parseNaturalValue = function(token){
		this.currentToken = token;
		if(this.currentToken.value instanceof WaebricToken.NATURAL){
			return this.currentToken.value;
		}
		print('Error parsing Attribute value. Expected NATURAL value but current token is ' + this.currentToken.value);
	}
	
	/**
	 * Parses a Path 
	 * Path = DirName "/" FileName
	 * 		| FileName
	 */
	this.parsePath = function(token){
		this.currentToken = token;
		var path;
		if(this.currentToken.value instanceof WaebricToken.IDENTIFIER){
			//Build directory + filename
			var directoryFileName = "";
			while(this.isDirectory(this.currentToken.value)){				
				directoryFileName += this.currentToken.value;
				this.currentToken = this.currentToken.nextToken();
			}
			
			//Skip DOT file extension
			this.currentToken = this.currentToken.nextToken(); 
			
			//Build file extension
			var fileExtension = "";			
			while (this.isFileExtension(this.currentToken.value)) {
				fileExtension += this.currentToken.value;
				this.currentToken = this.currentToken.nextToken();
			}
			
			path = directoryFileName + "." + fileExtension;
			if(this.isPath(path)){
				return path;
			}	
			
			print('Error parsing path. Path has invalid characters or is missing characters. ' + path);		
		}
		print('Error parsing path. Path has invalid characters or is missing characters. ' + path);
	}
	
	/**
	 * Parses a Directory as String
	 * @param {Object} value
	 */
	this.isDirectory = function(value){
		var regExp = new RegExp("[^\ \t\n\r.\\\\]");
		return value.match(regExp);
	}
	
	/**
	 * Parses a File extension as String
	 * @param {Object} value
	 */
	this.isFileExtension = function(value){
		var regExp = new RegExp("[a-zA-Z0-9]");
		return value.match(regExp);
	}
	
	/**
	 * Checks whether the entire path matches
	 * @param {Object} value
	 */
	this.isPath = function(value){
		var regExp = new RegExp("^[^\ \t\n\r.\\\\]*.[A-Za-z0-9]*$");
		return value.match(regExp);
	}
}

WaebricParser.parseAll = function(tokenizerResult){
	var parser = new WaebricParser();
	return parser.parseAll(tokenizerResult);
}
