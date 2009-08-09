/**
 * Waebric Module Parser
 * 
 * @author Nickolas Heirbaut
 */
function WaebricModuleParser(){
	
	this.currentToken;
		
	this.siteParser = new WaebricSiteParser();
    this.expressionParser = new WaebricExpressionParser();
	this.functionParser = new WaebricFunctionDefinitionParser();
	
	/**
	 * Parses the input value to a {Module}
	 * 
	 * @param {Object} parentParser
	 * @return {Module}
	 */
	this.parse = function(parentParser){
		var module = this.parseModule(parentParser.currentToken);
		parentParser.currentToken = this.currentToken;
		return module;
	}
	
	/**
     * Checks whether the input token's value equals the start of a Module
     *
     * @param {WaebricParserToken} token
     * @return {Boolean}
     */
    this.isStartModule = function(token){
        return WaebricToken.KEYWORD.MODULE.equals(token.value)
    }
    
    /**
     * Parses the module root
     * --> "Module" ModuleId ModuleElements*
     *
     * @param {WaebricParserToken} token
     * @return {Module}
     */
    this.parseModule = function(token){
        this.currentToken = token;
        var moduleId;
        var moduleElements;
        
        //Parse ModuleId
        if (this.isModuleIdElement(this.currentToken)) {
            moduleId = this.parseModuleId(this.currentToken);
        } else {
            print('Error parsing module. Expected ModuleId but token is ' + this.currentToken.value);
        }       
		
        //Parse ModuleElements
        if (this.isStartModuleElement(this.currentToken.nextToken())) {
            moduleElements = this.parseModuleElement(this.currentToken.nextToken());
        } else {
            print('Error parsing module. Expected start ModuleElement (SITE/DEF/IMPORT) but token is ' +
            this.currentToken.value);
        }
        
        return new Module(moduleId, moduleElements);
    }
    
    /**
     * Checks whether the input value is part of a ModuleId. No DOT is allowed in the input value.
     *
     * @param {WaebricParserToken} token
     * @return {Boolean}
     */
    this.isModuleIdElement = function(token){
        return this.expressionParser.isIdentifier(token);
    }
    
    /**
     * Parses the ModuleID
     * --> ModuleId = IdCon listOf("." IdCon)
     *
     * @param {WaebricParserToken} token
     * @return {ModuleId}
     */
    this.parseModuleId = function(token){
        this.currentToken = token;
		
        //Parse first part ModuleId -> IdCon		
        var value = this.currentToken.value.toString();
        
        //Parse remaining parts ModuleID -> listOf("." IdCon)
        while (this.currentToken.nextToken().value == WaebricToken.SYMBOL.DOT &&
        this.isModuleIdElement(this.currentToken.nextToken().nextToken())) {
            value += this.currentToken.nextToken().value.toString();
            value += this.currentToken.nextToken().nextToken().value.toString();
            this.currentToken = this.currentToken.nextToken().nextToken();
        }
        
        return new ModuleId(value)
    }
    
    /**
     * Checks whether the input value equals the start of a ModuleElement
     *
     * @param {WaebricParserToken} token
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
     * ModuleElement = "import" ModuleId
     * 				 | "site" (Mapping ";")* "end"
     * 			 	 | "def" IdCon Formals Statement* "end"
     *
     * @param {WaebricParserToken} token
     * @return {Array} An array of moduleElements
     */
    this.parseModuleElement = function(token){
        this.currentToken = token;
        var moduleElements = new Array();
        while (this.currentToken.hasNextToken()) {
            if (this.isStartImport(this.currentToken)) {
                var imprt = this.parseImport(this.currentToken.nextToken());
                moduleElements.push(imprt);
            } else if (this.siteParser.isStartSite(this.currentToken)) {
				this.currentToken = this.currentToken.nextToken();
				var site = this.siteParser.parse(this);
                moduleElements.push(site);
            } else if (this.functionParser.isStartFunctionDef(this.currentToken)) {
				this.currentToken = this.currentToken.nextToken();
                var def = this.functionParser.parse(this);
                moduleElements.push(def);
            } else {
                print('Error parsing module elements. Expected IMPORT/SITE/DEF but current token is ' + this.currentToken.value);
                this.currentToken = this.currentToken.nextToken();
            }

			this.currentToken = this.currentToken.nextToken();
        }
        return moduleElements;
    }
	
		/**
     * Parses an Import
     * 
     * @param {WaebricParserToken} token
     * @return {Import}
     */
	this.parseImport = function(token){
		this.currentToken = token;		
		var moduleId = this.parseModuleId(this.currentToken)
        return new Import(moduleId);
	}
	
	/**
     * Checks whether the input value equals the start of an IMPORT
     *
     * @param {WaebricParserToken} token
     * @return {Boolean}
     */
    this.isStartImport = function(token){
        return WaebricToken.KEYWORD.IMPORT.equals(token.value);
    }


}
