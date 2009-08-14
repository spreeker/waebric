/**
 * Waebric Module Parser
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 */
function WaebricModuleParser(){
	
	this.siteParser = new WaebricSiteParser();
    this.expressionParser = new WaebricExpressionParser();
	this.functionParser = new WaebricFunctionDefinitionParser();
	
	/**
	 * Parses the input value to a {Module}
	 * Updates currentToken of the parent parser
	 * 
	 * @param {Object} parentParser The parent parser
	 * @return {Module}
	 */
	this.parse = function(parentParser){
		this.parserStack.setStack(parentParser.parserStack)
		var module = this.parseModule(parentParser.currentToken);
		parentParser.setCurrentToken(this.currentToken);
		parentParser.parserStack.setStack(this.parserStack)
		return module;
	}
	
	/**
     * Checks whether the input token's value equals the start of a Module
     *
     * @param {WaebricParserToken} token The token to evaluate
     * @return {Boolean}
     */
    this.isStartModule = function(token){
        return WaebricToken.KEYWORD.MODULE.equals(token.value)
    }
    
    /**
     * Parses the module root
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {Module}
     */
    this.parseModule = function(token){
        this.parserStack.addParser('Module');
		this.setCurrentToken(token);  
		
        var moduleId;
        var moduleElements = new Array();
        
        //Parse ModuleId
        if (this.isModuleIdElement(this.currentToken)) {
            moduleId = this.parseModuleId(this.currentToken);
        } else {
			throw new WaebricSyntaxException(this, 'Identifier', 'The name of the module');
        }       
		
        //Parse ModuleElements
		if (this.currentToken.hasNextToken()) {
			if (this.isStartModuleElement(this.currentToken.nextToken())) {
				moduleElements = this.parseModuleElement(this.currentToken.nextToken());
			} else {
				this.setCurrentToken(this.currentToken.nextToken());
				throw new WaebricSyntaxException(this, '"SITE", "DEF" or "IMPORT"', 
					'Start of a new ModuleElement');
			}
		}        
		this.parserStack.removeParser();
        return new Module(moduleId, moduleElements);
    }
    
    /**
     * Checks whether the input value is part of a ModuleId. No DOT is allowed in the input value.
     *
     * @param {WaebricParserToken} token The token to evaluate
     * @return {Boolean}
     */
    this.isModuleIdElement = function(token){
        return this.expressionParser.isIdentifier(token);
    }
    
    /**
     * Parses the ModuleID
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {ModuleId}
     */
    this.parseModuleId = function(token){
		this.setCurrentToken(token);  
		
        //Parse first part ModuleId -> IdCon		
        var value = this.currentToken.value.toString();
        
        //Parse remaining parts ModuleID -> listOf("." IdCon)
        while (this.currentToken.nextToken().value == WaebricToken.SYMBOL.DOT &&
        this.isModuleIdElement(this.currentToken.nextToken().nextToken())) {
            value += this.currentToken.nextToken().value.toString();
            value += this.currentToken.nextToken().nextToken().value.toString();
            this.setCurrentToken(this.currentToken.nextToken().nextToken());
        }
		
        return new ModuleId(value)
    }
    
    /**
     * Checks whether the input value equals the start of a ModuleElement
     *
     * @param {WaebricParserToken} token The token to evaluate
     * @return {Boolean}
     */
    this.isStartModuleElement = function(token){
        return WaebricToken.KEYWORD.IMPORT.equals(token.value) ||
        WaebricToken.KEYWORD.SITE.equals(token.value) ||
        WaebricToken.KEYWORD.DEF.equals(token.value)
    }
    
    /**
     * Parses a ModuleElement
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {Array} An array of moduleElements
     */
    this.parseModuleElement = function(token){
        this.parserStack.addParser('ModuleElements');
		this.setCurrentToken(token);  
		
        var moduleElements = new Array();
        while (this.currentToken != null) {			
			this.parserStack.addParser('ModuleElement');

			if(!this.currentToken.hasNextToken()){
				throw new WaebricSyntaxException(this, '"SITE", "DEF" or "IMPORT"', 
					'Start of a new ModuleElement');
			}else if (this.isStartImport(this.currentToken)) {
                var imprt = this.parseImport(this.currentToken.nextToken());
                moduleElements.push(imprt);
            } else if (this.siteParser.isStartSite(this.currentToken)) {
				this.setCurrentToken(this.currentToken.nextToken());
				var site = this.siteParser.parse(this);
                moduleElements.push(site);
            } else if (this.functionParser.isStartFunctionDef(this.currentToken)) {
				this.setCurrentToken(this.currentToken.nextToken());
                var def = this.functionParser.parse(this);
                moduleElements.push(def);
            } else {
				this.setCurrentToken(this.currentToken.nextToken());
				throw new WaebricSyntaxException(this, '"SITE", "DEF" or "IMPORT"', 
					'Start of a new ModuleElement');
            }
			this.setCurrentToken(this.currentToken.nextToken());
			
			this.parserStack.removeParser();
        }		
		this.parserStack.removeParser();
        return moduleElements;
    }
	
	/**
     * Parses an Import
     * 
     * @param {WaebricParserToken} token The token to parse
     * @return {Import}
     */
	this.parseImport = function(token){
		this.parserStack.addParser('Import')
		this.setCurrentToken(token);
		
		if(!this.isModuleIdElement(this.currentToken)){
			throw new WaebricSyntaxException(this, 'Identifier', 'The name of the module to import');
		}
		
		var moduleId = this.parseModuleId(this.currentToken)
		this.parserStack.removeParser();
        return new Import(moduleId);
	}
	
	/**
     * Checks whether the input value equals the start of an IMPORT
     *
     * @param {WaebricParserToken} token The token to evaluate
     * @return {Boolean}
     */
    this.isStartImport = function(token){
        return WaebricToken.KEYWORD.IMPORT.equals(token.value);
    }
}
WaebricModuleParser.prototype = new WaebricBaseParser();