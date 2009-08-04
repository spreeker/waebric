
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
        if (this.isStartModule(this.currentToken.value)) {
            return this.parseModule(this.currentToken.nextToken()); //Skip keyword "Module"
        } else {
            print('Error parsing module. Expected keyword Module as first token but token is ' +
            this.currentToken.value);
        }
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
        return this.isIdentifier(token);
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
        var value = this.currentToken.value;
        
        //Parse remaining parts ModuleID -> listOf("." IdCon)
        while (this.currentToken.nextToken().value == WaebricToken.SYMBOL.DOT &&
        this.isModuleIdElement(this.currentToken.nextToken().nextToken())) {
            value += this.currentToken.nextToken().value;
            value += this.currentToken.nextToken().nextToken().value;
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
            } else if (this.isStartSite(this.currentToken)) {
                var site = this.parseSite(this.currentToken.nextToken());
                moduleElements.push(site);
            } else if (this.isStartFunctionDef(this.currentToken)) {
                var def = this.parseFunctionDefinition(this.currentToken.nextToken());
                moduleElements.push(def);
            } else {
                print('Error parsing module elements. Expected IMPORT/SITE/DEF but current token is ' + this.currentToken.value);
                this.currentToken = this.currentToken.nextToken();
            }
        }
        return moduleElements;
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
    
    /**
     * Parses an Import
     * --> Import = "import" ModuleId;
     *
     * @param {WaebricParserToken} token
     * @return {Import}
     */
    this.parseImport = function(token){
        var moduleId = this.parseModuleId(token)
        return new Import(moduleId);
    }
    
    /**
     * Checks whether the input value equals the start of a SITE
     *
     * @param {WaebricParserToken} token
     * @return {Boolean}
     */
    this.isStartSite = function(token){
        return WaebricToken.KEYWORD.SITE.equals(token.value);
    }
    
    /**
     * Parses a Site
     * --> Site = "site" (Mapping ";")* "end"
     *
     * @param {WaebricParserToken} token
     * @return {Site}
     */
    this.parseSite = function(token){
        this.currentToken = token;
        var mappings = this.parseMappings(this.currentToken);
        this.currentToken = this.currentToken.nextToken() //Skip END token
        return new Site(mappings);
    }
    
    /**
     * Checks whether the input value equals the start of a FUNCTIONDEFINITION
     *
     * @param {WaebricParserToken}
     * @return {Boolean}
     */
    this.isStartFunctionDef = function(token){
        return WaebricToken.KEYWORD.DEF.equals(token.value);
    }
    
    /**
     * Parses a FunctionDefinition
     * "def" IdCon Formals Statement* "end"
     *
     * @param {WaebricParserToken} token
     * @return {FunctionDefinition}
     */
    this.parseFunctionDefinition = function(token){
        this.currentToken = token;
        
        var identifier;
        var formals = new Array();
        var statements = new Array();
        
        //First token should be an identifier
        if (this.isIdentifier(this.currentToken)) {
            identifier = this.parseIdentifier(this.currentToken);
        } else {
            print('Error parsing function definition. Expected a FUNCTION NAME (IDENTIFIER) but found ' + this.currentToken.value);
        }
        
        //Next token can be the start of formals
        if (this.isStartFormals(this.currentToken.nextToken())) {
            formals = this.parseFormals(this.currentToken.nextToken().nextToken());
        }
        
        //Remaining tokens are part of statements
        //TODO: Check ifStartStatements?
        statements = this.parseStatements(this.currentToken.nextToken());
        
        return new FunctionDefinition(identifier, formals, statements)
    }
    
    /**
     * Parses all mappings whitin a site
     * --> (Mapping ";")*
     *
     * @param {WaebricParserToken} token
     * @return {Array} An array of mappings
     */
    this.parseMappings = function(token){
        this.currentToken = token;
        
        var mappings = new Array();
        while (!WaebricToken.KEYWORD.END.equals(this.currentToken.value)) {
            //Skip mapping seperator
            var hasMultipleMappings = mappings.length > 0;
            var hasMappingSeperator = this.currentToken.value == WaebricToken.SYMBOL.SEMICOLON
            if (hasMultipleMappings && hasMappingSeperator) {
                this.currentToken = this.currentToken.nextToken();
            } else if (hasMultipleMappings && hasMappingSeperator) {
                print('Error parsing Mapping. Expected semicolon after mapping but found ' + this.currentToken.value);
            }
            
            //Get current mapping
            var mapping = this.parseMapping(this.currentToken);
            
            //Save current mapping
            mappings.push(mapping);
            
            //Navigate to next token (skips the last token)
            this.currentToken = this.currentToken.nextToken();
            
            //Check mapping ending
            var hasSemicolonEnding = WaebricToken.SYMBOL.SEMICOLON.equals(this.currentToken.value);
            var hasMappingEnding = WaebricToken.KEYWORD.END.equals(this.currentToken.nextToken().value)
            if (hasSemicolonEnding && hasMappingEnding) {
                print('Error parsing Mapping. The last SITE MAPPING should be followed by "END" but found ' + this.currentToken.value);
            }
        }
        return mappings;
    }
    
    /**
     * Parses a Site Mapping
     * Mapping = Path ":" Markup
     *
     * @param {WaebricParserToken} token
     * @return {Mapping}
     */
    this.parseMapping = function(token){
        this.currentToken = token;
        var path = this.parsePath(this.currentToken);
        var markup;
        
        var hasPathMarkupSeperator = (this.currentToken.value == WaebricToken.SYMBOL.COLON)
        var isMarkup = this.isMarkup(this.currentToken.nextToken());
        
        if (hasPathMarkupSeperator && isMarkup) {
            markup = this.parseMarkup(this.currentToken.nextToken());
        } else if (!hasPathMarkupSeperator) {
            print('Error parsing Mapping. Expected colon after path but found ' + this.currentToken.value);
        } else if (!isMarkup) {
            print('Error parsing Mapping. Expected Markup after path but found ' + this.currentToken.nextToken().value);
        }
        
        return new Mapping(path, markup);
    }
    
    /**
     * Checks whether the input value equals the start of a formal
     *
     * @param {WaebricParserToken} token
     * @return {Array} An array of formals
     */
    this.isStartFormals = function(token){
        return token.value == WaebricToken.SYMBOL.LEFTRBRACKET;
    }
    
    /**
     * Parses formals
     *
     * @param {WaebricParserToken} token
     * @return {Array} An array of formals
     */
    this.parseFormals = function(token){
        this.currentToken = token;
        
        var formals = new Array();
        while (this.currentToken.value != WaebricToken.SYMBOL.RIGHTRBRACKET) {
            var isCommaSeperator = (formals.length > 0 && this.currentToken.value == WaebricToken.SYMBOL.COMMA)
            if (isCommaSeperator) {
                this.currentToken = this.currentToken.nextToken();
            } else if (formals.length > 0) {
                print('Error parsing Formals. Expected COMMA after previous formal but found ' + this.currentToken.value);
            }
            
            if (this.isIdentifier(this.currentToken)) {
                formals.push(this.currentToken.value);
                this.currentToken = this.currentToken.nextToken();
            } else {
                print('Error parsing Formals. Expected formal but found ' + this.currentToken.value);
            }
        }
        return formals;
    }
    
    /**
     * Parses statements
     *
     * @param {WaebricParserToken} token
     * @return {Array} An array of statements
     */
    this.parseStatements = function(token){
        this.currentToken = token;
        
        var statements = new Array();
        while (!WaebricToken.KEYWORD.END.equals(this.currentToken.value)) {
            statement = this.parseStatement(this.currentToken);
            statements.push(statement);
            this.currentToken = this.currentToken.nextToken();
            
            var hasSemicolonEnding = this.currentToken.value == WaebricToken.SYMBOL.SEMICOLON;
            if (hasSemicolonEnding) {
                this.currentToken = this.currentToken.nextToken(); //Skip semicolon
            } else {
                print('Error parsing Statements. Expected SEMICOLON after statement but found ' + this.currentToken.value);
            }
        }
        return statements;
    }
    
    /**
     * Parses a single statement
     *
     * @param {WaebricParserToken} token
     * @return {Statement}
     */
    this.parseStatement = function(token){
        this.currentToken = token;
        
        if (WaebricToken.KEYWORD.IF.equals(this.currentToken.value)) {
            return this.parseIfElseStatement(this.currentToken);
        } else if (this.isMarkup(this.currentToken)) {
            return this.parseMarkup(this.currentToken)
        } else {
            print('Error parsing statement. Expected start of a statement but found ' + this.currentToken.value);
            this.currentToken = this.currentToken.nextToken(); //TODO: REMOVE THIS IF ALL STATEMENTS ARE IMPLEMENTED
        }
        return new Array();
    }
    
    /**
     * Parses an If/else statement. If the else clause is not specified, it returns an If statement
     * @param {WaebricParserToken} token
     * @return {IfElseStatement}
     * @return {IfStatement}
     */
    this.parseIfElseStatement = function(token){
        this.currentToken = token.nextToken(); //Skip IF keyword
        var predicate;
        var ifStatement;
        var elseStatement;
        
        //Parse predicate
        if (this.isStartPredicate(this.currentToken)) {
            predicate = this.parsePredicate(this.currentToken.nextToken());
        } else {
            print('Error parsing IF/ELSE statement. Expected predicate but found ' + this.currentToken.nextToken().value);
        }
        
        //Predicate should be ended correctly
        var hasValidEnding = this.currentToken.nextToken().value == WaebricToken.SYMBOL.RIGHTRBRACKET;
        if (hasValidEnding) {
            this.currentToken = this.currentToken.nextToken();
        } else {
            print('Error parsing IF/ELSE statement. Expected ending of predicate ")" but found ' + this.currentToken.nextToken().value);
        }
        
        //Parse IF statement
        ifStatement = this.parseStatement(this.currentToken.nextToken());
        
        //Parse ELSE statement (if exists)
        var hasElseClause = WaebricToken.KEYWORD.ELSE.equals(this.currentToken.nextToken().nextToken().value);
        if (hasElseClause) {
            elseStatement = this.parseStatement(this.currentToken.nextToken().nextToken().nextToken());
            return new IfElseStatement(predicate, ifStatement, elseStatement);
        }
        return new IfStatement(predicate, ifStatement);
    }
    
    /**
     * Checks whether the input value equals the start of a predicate
     *
     * @param {WaebricParserToken} token
     * @return {Boolean}
     */
    this.isStartPredicate = function(token){
        var hasValidOpening = token.value == WaebricToken.SYMBOL.LEFTRBRACKET;
        var hasContent = token.nextToken().value != WaebricToken.SYMBOL.RIGHTRBRACKET;
        var isNotPredicate = hasContent && (token.nextToken().value == WaebricToken.SYMBOL.EXCLAMATION);
        var isExpression = !isNotPredicate && hasContent && this.isExpression(token.nextToken());
        var hasValidContent = isNotPredicate || isExpression
        
        return (hasValidOpening && hasValidContent);
    }
    
    /**
     * Parses a predicate
     *
     * @param {WaebricParserToken} token
     * @param {Boolean} ignoreDoubleAnd
     * @param {Boolean} ignoreDoubleOr
     * @return {Object} Predicate
     */
    this.parsePredicate = function(token, ignoreDoubleAnd, ignoreDoubleOr){
        this.currentToken = token;
        var predicate;
        
        //Parse SINGLE predicates
        if (this.isNotPredicate(this.currentToken.value)) {
            predicate = new NotPredicate(this.parsePredicate(this.currentToken.nextToken(), true, true));
        } else if (this.isExpression(this.currentToken)) {
            predicate = this.parseExpression(this.currentToken)
            
            //If the expression is followed by a question mark, then this is a predicate of type "is-a-predicate"
            if (this.isEndPredicateType(this.currentToken.nextToken())) {
                var type = this.parsePredicateType(this.currentToken);
                predicate = new IsAPredicate(predicate, type)
            }
        }
        
        //Parse AND and OR predicates
        if (!ignoreDoubleAnd && this.currentToken.nextToken().value == WaebricToken.SYMBOL.DOUBLEAND) {
            predicate = this.parseAndPredicate(this.currentToken.nextToken(), predicate)
        } else if (!ignoreDoubleOr && this.currentToken.nextToken().value == WaebricToken.SYMBOL.DOUBLEOR) {
            predicate = this.parseOrPredicate(this.currentToken.nextToken(), predicate)
        }
        
        return predicate;
    }
    
    /**
     * Checks whether the input value equals the start of a not-predicate
     *
     * @param {WaebricParserToken} token
     * @return {Boolean}
     */
    this.isNotPredicate = function(token){
        return token.value == WaebricToken.SYMBOL.EXCLAMATION
    }
    
    /**
     * Parses a not-predicate
     *
     * @param {WaebricParserToken} token
     * @return {NotPredicate}
     */
    this.parseNotPredicate = function(token){
        this.currentToken = token;
        return new NotPredicate(this.currentToken.value);
    }
    
    /**
     * Checks whether the input value equals the start of a not-predicate
     *
     * @param {WaebricParserToken} token
     * @return {Boolean}
     */
    this.isEndPredicateType = function(token){
        return token.value == WaebricToken.SYMBOL.QUESTION
    }
    
    /**
     * Parses a TYPE predicate
     * 
     * @param {WaebricParserToken} token
     * @return {PredicateType}
     */
    this.parsePredicateType = function(token){
        this.currentToken = token;
        
        switch (this.currentToken.value.toString()) {
            case "list":
                this.currentToken = token.nextToken();
                return new ListType();
            case "record":
                this.currentToken = token.nextToken();
                return new RecordType();
            case "string":
                this.currentToken = token.nextToken();
                return new StringType();
            default:
                print('Error parsing is-a-predicate type. Expected LIST/RECORD/STRING but found ' + this.currentToken.value);
        }
    }
    
    /**
     * Parses an AND predicate
     * 
     * @param {Object} token
     * @param {Object} predicate
     */
    this.parseAndPredicate = function(token, predicate){
        this.currentToken = token;
        var currentPredicate = predicate;
        
        //Parse all && predicates
        do {
            var predicateRight = this.parsePredicate(this.currentToken.nextToken(), true, true)
            currentPredicate = new AndPredicate(currentPredicate, predicateRight);
            this.currentToken = this.currentToken.nextToken();
        } while (this.currentToken.value == WaebricToken.SYMBOL.DOUBLEAND)
        
        //Parse remaining || predicates (if exists)
		var hasOrPredicate = this.currentToken.value == WaebricToken.SYMBOL.DOUBLEOR;
        if (hasOrPredicate) {
            currentPredicate = this.parseOrPredicate(this.currentToken, currentPredicate);
        } else {
            this.currentToken = this.currentToken.previousToken();
        }
        
        return currentPredicate;
    }
    
	/**
	 * Parses an OR predicate
	 * 
	 * @param {Object} token
	 * @param {Object} predicate
	 */
    this.parseOrPredicate = function(token, predicate){
        this.currentToken = token;
        var currentPredicate = predicate;
        
        //Parse all || predicats
        do {
            var predicateRight = this.parsePredicate(this.currentToken.nextToken(), true, true)
            currentPredicate = new OrPredicate(currentPredicate, predicateRight);
            this.currentToken = this.currentToken.nextToken();
        } while (this.currentToken.value == WaebricToken.SYMBOL.DOUBLEOR)
        
        //Parse remaining && predicates (if exists)
		var hasAndPredicate = this.currentToken.value == WaebricToken.SYMBOL.DOUBLEAND;
        if (hasAndPredicate) {
            currentPredicate = this.parseAndPredicate(this.currentToken, currentPredicate);
        } else {
            this.currentToken = this.currentToken.previousToken();
        }
        
        return currentPredicate;
    }    
    
    /**
     * Checks whether the input value is valid markup
     *
     * @param {WaebricParserToken}
     * @param {Boolean}
     */
    this.isMarkup = function(token){
        return token.value instanceof WaebricToken.IDENTIFIER;
    }
    
    /**
     * Parses Markup
     *
     * Markup = Designator Arguments
     * 		  | Designator
     * 
     * @param {WaebricParserToken} token
     * @return {Markup}
     */
    this.parseMarkup = function(token){
        this.currentToken = token;
        
        if (this.currentToken.value instanceof WaebricToken.IDENTIFIER) {
            var designator = this.parseDesignator(this.currentToken);
            //If arguments found, parse as markup call
            if (this.currentToken.nextToken().value == WaebricToken.SYMBOL.LEFTRBRACKET) {
                var arguments = this.parseArguments(this.currentToken.nextToken());
                return new MarkupCall(designator, arguments);
            }
            //If no arguments found, parse as Markup
            return designator;
        }
        
        print('Error parsing Markup. Expected IDENTIFIER MARKUP but found ' + this.currentToken.value);
    }
    
	/**
	 * Parses a designator
	 * 
	 * @param {WaebricParserToken} token
	 * @return {Designator}
	 */
    this.parseDesignator = function(token){
        this.currentToken = token;
        
        var idCon;
        var attributes;
        
        //Parse identifier
        if (this.isIdentifier(this.currentToken)) {
            idCon = this.currentToken.value;
        } else {
            print('Error parsing Designator. Expected IDENTIFIER but found ' + this.currentToken.value);
        }
        
        //Parse formals
        attributes = this.parseAttributes(this.currentToken);
        
        return new DesignatorTag(idCon, attributes);
    }
    
	/**
	 * Checks whether the input token is an attribute
	 * 
	 * @param {WaebricParserToken} token
	 * @return {Boolean}
	 */
    this.isAttribute = function(token){
        var regExp = new RegExp("[#.$:@]");
        return token.value.match(regExp);
    }
    
	/**
	 * Parse arguments
	 * 
	 * @param {WaebricParserToken} token
	 * @return {Array} An array of arguments
	 */
    this.parseArguments = function(token){
        this.currentToken = token.nextToken();
        
        var arguments = new Array();
        while (this.currentToken.value != WaebricToken.SYMBOL.RIGHTRBRACKET) {			
            var hasMultipleArguments = arguments.length > 0;
			var hasValidSeperator = this.currentToken.value == WaebricToken.SYMBOL.COMMA;
			
            if (hasMultipleArguments && hasValidSeperator) {
                this.currentToken = this.currentToken.nextToken();
            } else if (hasMultipleArguments) {
                print('Error parsing Arguments. Expected COMMA after previous argument but found ' + this.currentToken.value);
            }            
            
			var argument = this.parseArgument(this.currentToken);
            arguments.push(argument);
            this.currentToken = this.currentToken.nextToken();
        }
        
        return arguments;
    }
    
	/**
	 * Parse single argument
	 * 
	 * @param {WaebricParserToken} token
	 * @return {Object} Argument or Expression
	 */
    this.parseArgument = function(token){
        this.currentToken = token;
        if (this.currentToken.value instanceof WaebricToken.IDENTIFIER && this.currentToken.nextToken().value == WaebricToken.SYMBOL.EQ) {
            return this.parseAttributeArgument(this.currentToken);
        } else {
            return this.parseRegularArgument(this.currentToken);
        }
    }
    
	/**
	 * Parses regular argument
	 * 
	 * @param {WaebricParserToken} token
	 * @param {Object} An expression
	 */
    this.parseRegularArgument = function(token){
        this.currentToken = token;
        return this.parseExpression(this.currentToken);
    }
    
	/**
	 * Parses an attribute argument
	 * 
	 * @param {WaebricParserToken} token
	 * @param {Argument}
	 */
    this.parseAttributeArgument = function(token){
        this.currentToken = token;
        var idCon;
        var expression;
        
        //Parse IdCon
        if (this.isIdentifier(this.currentToken)) {
            idCon = this.currentToken.value;
        } else {
            print('Attribute should start with an identifier but found ' + this.currentToken.value)
        }
        
        //Skip equality sign
		var hasValidSeperator = this.currentToken.hasNextToken() && this.currentToken.nextToken().hasNextToken()
        if (hasValidSeperator) {
            this.currentToken = this.currentToken.nextToken().nextToken();
        } else {
            print('Expected equality sign but found ' + this.currentToken.value);
        }
        
        //Parse arguments/attributes
        if (this.isExpression(this.currentToken)) {
            expression = this.parseExpression(this.currentToken);
        } else {
            print('Attribute is not correctly closed. Expected expression but found ' + this.currentToken.value)
        }
        
        return new Argument(idCon, expression);
    }
    
	/**
	 * Checks whether the input value is an expression
	 * 
	 * @param {WaebricParserToken} token
	 * @return {Boolean}
	 */
    this.isExpression = function(token){
        return this.isText(token) || this.isIdentifier(token) || this.isNatural(token) ||
        this.isStartRecord(token) || this.isStartList(token) || this.isFieldExpression(token.value);
    }
    
	/**
	 * Parses an expression
	 * 
	 * @param {WaebricParserToken} token
	 * @param {Boolean} ignoreFieldExpression If true, no FieldExpressions are parsed
	 * @param {Boolean} ignoreCatExpression If true, no CatExpressions are parsed
	 * @return {Object} Expression
	 */
    this.parseExpression = function(token, ignoreFieldExpression, ignoreCatExpression){
        this.currentToken = token;
        
        if (this.isCatExpression(this.currentToken) && !ignoreCatExpression) {
            return this.parseCatExpression(this.currentToken);
        } else if (this.isFieldExpression(this.currentToken) && !ignoreFieldExpression) {
            return this.parseFieldExpression(this.currentToken);
        } else if (this.isCatExpression(this.currentToken) && !ignoreCatExpression) {
            return this.parseCatExpression(this.currentToken);
        } else if (this.isText(this.currentToken)) {
            return this.parseText(this.currentToken);
        } else if (this.isIdentifier(this.currentToken)) {
            return this.parseIdentifier(this.currentToken);
        } else if (this.isNatural(this.currentToken)) {
            return this.parseNatural(this.currentToken)
        } else if (this.isStartList(this.currentToken)) {
            return this.parseList(this.currentToken.nextToken())
        } else if (this.isStartRecord(this.currentToken)) {
            return this.parseRecord(this.currentToken.nextToken())
        } else {
            return "NYI"
        }
    }
    
	/**
	 * Checks whether the input value equals TEXT
	 * 
	 * @param {WaebricParserToken} token
	 * @return {Boolean}
	 */
    this.isText = function(token){
        var regExp = new RegExp('^([^\x00-\x1F\&\<\"\x80-\xFF]*[\n\r\t]*(\\\\&)*(\\\\")*(&#[0-9]+;)*(&#x[0-9a-fA-F]+;)*(&[a-zA-Z_:][a-zA-Z0-9.-_:]*;)*)*$');
        return token.value instanceof WaebricToken.TEXT && token.value.match(regExp);
    }
    
	/**
	 * Parses TEXT
	 * 
	 * @param {WaebricParserToken} token
	 * @return {TextExpression}
	 */
    this.parseText = function(token){
        this.currentToken = token;
        return new TextExpression(this.currentToken.value);
    }
    
	/**
	 * Checks whether the input value equals IDENTIFIER
	 * 
	 * @param {WaebricParserToken} token
	 * @return {Boolean}
	 */
    this.isIdentifier = function(token){
        var regExp = new RegExp('^([A-Za-z][A-Za-z0-9\-]*)$');
        return token.value instanceof WaebricToken.IDENTIFIER && token.value.match(regExp) && !WaebricToken.KEYWORD.contains(token.value.toString());
    }
    
	/**
	 * Parses an IDENTIFIER
	 * 
	 * @param {WaebricParserToken} token
	 * @return {VarExpression}
	 */
    this.parseIdentifier = function(token){
        this.currentToken = token;
        return new VarExpression(this.currentToken.value);
    }
    
	/**
	 * Checks whether the input value equals a NATURAL
	 * 
	 * @param {WaebricParserToken} token
	 * @return {Boolean}
	 */
    this.isNatural = function(token){
        var regExp = new RegExp('[0-9]$');
        return token.value instanceof WaebricToken.NATURAL && token.value.match(regExp);
    }
    
	/**
	 * Parses a NATURAL
	 * 
	 * @param {WaebricParserToken} token
	 * @return {NatExpression}
	 */
    this.parseNatural = function(token){
        this.currentToken = token;
        return new NatExpression(this.currentToken.value);
    }
    
	/**
	 * Checks whether the input value equals the start of a RECORD
	 * 
	 * @param {WaebricParserToken} token
	 * @return {Boolean}
	 */
    this.isStartRecord = function(token){
        return token.value == WaebricToken.SYMBOL.LEFTCBRACKET;
    }
    
	/**
	 * Parses a RECORD
	 * 
	 * @param {WaebricParserToken} token
	 * @return {RecordExpression}
	 */
    this.parseRecord = function(token){
        this.currentToken = token;
        
        var list = new Array();
        while (this.currentToken.value != WaebricToken.SYMBOL.RIGHTCBRACKET) {
			var hasMultipleRecordItems = list.length > 0;
            var hasValidSeperator = this.currentToken.value == WaebricToken.SYMBOL.COMMA
			
            if (hasMultipleRecordItems && hasValidSeperator) {
                this.currentToken = this.currentToken.nextToken(); //Skip comma	
            } else if (hasMultipleRecordItems) {
                print('Error parsing Record. Expected COMMA after previous record item but found ' + this.currentToken.value);
                return;
            }
            
            //Parse KeyValuePair
            var expression = this.parseKeyValuePair(this.currentToken);
            list.push(expression);
            this.currentToken = this.currentToken.nextToken()
        }
        return new RecordExpression(list);
    }
    
	/**
	 * Parses a KeyValuePair
	 * 
	 * @param {WaebricParserToken} token
	 * @return {KeyValuePair}
	 */
    this.parseKeyValuePair = function(token){
        this.currentToken = token;
        
        var key;
        var value;
        
        //Parse Identifier
        if (this.isIdentifier(this.currentToken)) {
            key = this.parseIdentifier(this.currentToken)
        } else {
            print('Error parsing KeyValuePair. Expected IDENTIFIER as KEY but found ' + this.currentToken.value);
        }
        
        //Parse Colon
		var hasValidSeperator = this.currentToken.nextToken().value == WaebricToken.SYMBOL.COLON;
        if (hasValidSeperator) {
            this.currentToken = this.currentToken.nextToken().nextToken(); //Skip colon
        } else {
            print('Error parsing KeyValuePair. Expected COLON after IDENTIFIER but found ' + this.currentToken.value);
        }
        
        //Parse Expression
        if (this.isExpression(this.currentToken)) {
            value = this.parseExpression(this.currentToken)
        } else {
            print('Error parsing KeyValuePair. Expected EXPRESSION as VALUE but found ' + this.currentToken.value);
        }
        
        return new KeyValuePair(key, value);
    }
    
	/**
	 * Checks whether the input equals the start of a list
	 * 
	 * @param {WaebricParserToken} token
	 * @return {Boolean}
	 */
    this.isStartList = function(token){
        return token.value == WaebricToken.SYMBOL.LEFTBBRACKET;
    }
    
	/**
	 * Parses an ExpressionList
	 * 
	 * @param {WaebricParserToken} token
	 * @return {ListExpression}
	 */
    this.parseList = function(token){
        this.currentToken = token;
        
        var list = new Array();
        while (this.currentToken.value != WaebricToken.SYMBOL.RIGHTBBRACKET) {
            //Detect the list-item seperator
			var hasMultipleListItems = list.length > 0
			var hasValidSeperator = this.currentToken.value == WaebricToken.SYMBOL.COMMA;
			
            if (hasMultipleListItems && hasValidSeperator) {
                this.currentToken = this.currentToken.nextToken(); //Skip comma	
            } else if (hasMultipleListItems) {
                print('Error parsing List. Expected COMMA after previous list item but found ' + this.currentToken.value);
                return;
            }
            var expression = this.parseExpression(this.currentToken);
            list.push(expression);
            this.currentToken = this.currentToken.nextToken()
        }
        return new ListExpression(list);
    }
    
	/**
	 * Checks whether the input value is the start of a field expression
	 * 
	 * @param {WaebricParserToken} token
	 * @return {Boolean}
	 */
    this.isFieldExpression = function(token){
		var isValidExpression = this.isExpression(token);
		var tokenAfterNextExpression = this.getTokenAfterExpression(token)
		var hasValidSeperator = tokenAfterNextExpression.value == WaebricToken.SYMBOL.DOT
		var hasValidField = this.isIdentifier(tokenAfterNextExpression.nextToken());
		
       	return isValidExpression && hasValidSeperator && hasValidField;
    }
    
	/**
	 * Parses a FieldExpression
	 * 
	 * @param {WaebricParserToken} token
	 * @return {FieldExpression}
	 */
    this.parseFieldExpression = function(token){
        this.currentToken = token;
        var expressionToken = this.currentToken;
        var fieldToken = this.getTokenAfterExpression(this.currentToken).nextToken();
        
        var expression = this.parseExpression(expressionToken, true);
        var field = this.parseIdentifier(fieldToken);
        var fieldExpression = new FieldExpression(expression, field);
        
        while (fieldToken.nextToken().value == WaebricToken.SYMBOL.DOT) {
            if (this.isIdentifier(fieldToken.nextToken().nextToken())) {
                var field = this.parseIdentifier(fieldToken.nextToken().nextToken(), true);
                fieldExpression = new FieldExpression(fieldExpression, field);
                fieldToken = this.currentToken;
            } else {
                print('Error parsing FieldExpression. Expected IDENTIFIER as FIELD but found ' + fieldToken.nextToken().nextToken().value);
                return;
            }
        }
        
        //If the fieldExpression is followed by a "PLUS" sign, than it is part of a catExpression.
        //Cannot be forseen before since the fieldexpression has a higher priority
		var catExpressionFollows = this.currentToken.nextToken().value == WaebricToken.SYMBOL.PLUS;
        if (catExpressionFollows) {
            var expressionLeft = fieldExpression;
            var expressionRight = this.parseExpression(this.currentToken.nextToken().nextToken());
            var catExpression = new CatExpression(expressionLeft, expressionRight);
            return catExpression;
        } else {
            return fieldExpression;
        }
    }
    
	/**
	 * Checks whether the input value is a CatExpression
	 * 
	 * @param {WaebricParserToken} token
	 * @return {Boolean}
	 */
    this.isCatExpression = function(token){
		var isValidExpression = this.isExpression(token);
		var tokenAfterNextExpression = this.getTokenAfterExpression(token)
		var hasValidSeperator = tokenAfterNextExpression.value == WaebricToken.SYMBOL.PLUS
		var hasValidField = this.isIdentifier(tokenAfterNextExpression.nextToken());
		
		return isValidExpression && hasValidSeperator && hasValidField;
    }
    
	/**
	 * Parses a CatExpression
	 * 
	 * @param {WaebricParserToken} token
	 * @return {CatExpression}
	 */
    this.parseCatExpression = function(token){
        this.currentToken = token;
        
        var expressionLeft = this.parseExpression(this.currentToken, false, true);
        var expressionRight = this.parseExpression(this.currentToken.nextToken().nextToken());
        var catExpression = new CatExpression(expressionLeft, expressionRight);
        
        return catExpression;
    }
    
	/**
	 * Returns the token that follows a Record or a List.
	 * Nested records or lists are ignored
	 * 
	 * @param {WaebricParserToken} token Start token of Expression
	 * @return {WaebricParserToken} The token that follows the record or list
	 */
    this.getTokenAfterExpression = function(token){
        if (token.value == WaebricToken.SYMBOL.LEFTCBRACKET) {
            return this.getTokenAfterBracketEnding(token, WaebricToken.SYMBOL.LEFTCBRACKET, WaebricToken.SYMBOL.RIGHTCBRACKET)
        } else if (token.value == WaebricToken.SYMBOL.LEFTBBRACKET) {
            return this.getTokenAfterBracketEnding(token, WaebricToken.SYMBOL.LEFTBBRACKET, WaebricToken.SYMBOL.RIGHTBBRACKET)
        } else {
            return token.nextToken();
        }
    }
    
	/**
	 * Returns the token that follows after the closing of the supplied left symbol
	 * 
	 * @param {WaebricParserToken} token Start token of expression
	 * @param {String} symbolLeft Start symbol
	 * @param {String} symbolRight End symbol
	 */
    this.getTokenAfterBracketEnding = function(token, symbolLeft, symbolRight){
        var leftBracketsFound = 0;
        var rightBracketsFound = 0;
        do {
            if (token.value == symbolLeft) {
                leftBracketsFound++;
            } else if (token.value == symbolRight) {
                rightBracketsFound++;
            }
            token = token.nextToken();
        } while (leftBracketsFound != rightBracketsFound)
        return token;
    }
    
	/**
	 * Parses attributes
	 * 
	 * @param {WaebricParserToken} token
	 * @return {Array} An array with attributes
	 */
    this.parseAttributes = function(token){
        this.currentToken = token;
        var attributes = new Array();
        while (this.isAttribute(this.currentToken.nextToken())) {
            var attribute = this.parseAttribute(this.currentToken.nextToken());
            attributes.push(attribute);
        }
        return attributes;
    }
    
	/**
	 * Parses single attribute
	 * 
	 * @param {WaebricParserToken} token
	 * @return {Object} Attribute
	 */
    this.parseAttribute = function(token){
        this.currentToken = token
        if (this.currentToken.value == WaebricToken.SYMBOL.CROSSHATCH) {
            return this.parseIDAttribute(this.currentToken.nextToken());
        } else if (this.currentToken.value == WaebricToken.SYMBOL.DOT) {
            return this.parseClassAttribute(this.currentToken.nextToken());
        } else if (this.currentToken.value == WaebricToken.SYMBOL.DOLLAR) {
            return this.parseNameAttribute(this.currentToken.nextToken());
        } else if (this.currentToken.value == WaebricToken.SYMBOL.COLON) {
            return this.parseTypeAttribute(this.currentToken.nextToken());
        } else if (this.currentToken.value == WaebricToken.SYMBOL.AT) {
            return this.parseWidthHeightAttribute(this.currentToken.nextToken());
        }
        print('Error parsing attribute.')
    }
    
	/**
	 * Parses an ID Attribute
	 * 
	 * @param {WaebricParserToken} token
	 * @return {IdAttribute}
	 */
    this.parseIDAttribute = function(token){
        this.currentToken = token;
        var idValue = this.parseIdentifierValue(this.currentToken);
        return new IdAttribute(idValue);
    }
    
	/**
	 * Parses a Class Attribute
	 * 
	 * @param {WaebricParserToken} token
	 * @return {ClassAttribute}
	 */
    this.parseClassAttribute = function(token){
        this.currentToken = token;
        var classValue = this.parseIdentifierValue(this.currentToken);
        return new ClassAttribute(classValue);
    }
    
	/**
	 * Parses a Name Attribute
	 * 
	 * @param {WaebricParserToken} token
	 * @return {NameAttribute}
	 */
    this.parseNameAttribute = function(token){
        this.currentToken = token;
        var nameValue = this.parseIdentifierValue(this.currentToken);
        return new NameAttribute(nameValue);
    }
    
	/**
	 * Parses an Type Attribute
	 * 
	 * @param {WaebricParserToken} token
	 * @return {TypeAttribute}
	 */
    this.parseTypeAttribute = function(token){
        this.currentToken = token;
        var typeValue = this.parseIdentifierValue(this.currentToken);
        return new TypeAttribute(typeValue);
    }
    
	/**
	 * Parses an Width/Height Attribute
	 * 
	 * @param {WaebricParserToken} token
	 * @return {WidthAttribute}
	 */
    this.parseWidthHeightAttribute = function(token){
        this.currentToken = token;
        var widthValue = this.parseNaturalValue(this.currentToken);
        if (this.currentToken.nextToken().value == WaebricToken.SYMBOL.PERCENT) {
            this.currentToken = this.currentToken.nextToken().nextToken();
            var heightValue = this.parseNaturalValue(this.currentToken);
            return new WidthHeightAttribute(widthValue, heightValue);
        } else {
            return new WidthAttribute(widthValue);
        }
    }
    
	/**
	 * Parses an identifier
	 * 
	 * @param {WaebricParserToken} token
	 * @return {String}
	 */	
    this.parseIdentifierValue = function(token){
        this.currentToken = token;
        if (this.currentToken.value instanceof WaebricToken.IDENTIFIER) {
            return this.currentToken.value;
        }
        print('Error parsing Attribute value. Expected IDENTIFIER but found ' + this.currentToken.value);
    }
    
	/**
	 * Parses a Natural
	 * 
	 * @param {WaebricParserToken} token
	 * @return {Number}
	 */	
    this.parseNaturalValue = function(token){
        this.currentToken = token;
        if (this.currentToken.value instanceof WaebricToken.NATURAL) {
            return this.currentToken.value;
        }
        print('Error parsing Attribute value. Expected NATURAL but found ' + this.currentToken.value);
    }
    
    /**
     * Parses a Path
     * Path = DirName "/" FileName
     * 		| FileName
     */
    this.parsePath = function(token){
        this.currentToken = token;
        var path;
        if (this.currentToken.value instanceof WaebricToken.IDENTIFIER) {
            //Build directory + filename
            var directoryFileName = "";
            while (this.isDirectory(this.currentToken.value)) {
                directoryFileName += this.currentToken.value;
                this.currentToken = this.currentToken.nextToken();
            }
            
            //Skip DOT file extension
			var hasDotSeperator = this.currentToken.value == WaebricToken.SYMBOL.DOT
            if (hasDotSeperator) {
                this.currentToken = this.currentToken.nextToken();
            } else {
                print('Error parsing path. Expected DOT between filename and extension but found ' +
                this.currentToken.nextToken().value);
            }
            //Build file extension
            var fileExtension = "";
            
            while (this.isFileExtension(this.currentToken.value)) {
                fileExtension += this.currentToken.value;
                if (this.currentToken.value instanceof WaebricToken.IDENTIFIER &&
                this.currentToken.nextToken().value instanceof WaebricToken.IDENTIFIER) {
                    print('Error parsing path. Found whitespace in file extension.');
                    return;
                }
                this.currentToken = this.currentToken.nextToken();
            }
            
            //Path should be valid
            path = directoryFileName + "." + fileExtension;
            if (this.isPath(path)) {
                return path;
            } else {
                print('Error parsing path. Path has invalid characters. ' + path);
            }
        }
        print('Error parsing path. Path should start with an identifier ' + this.currentToken.value);
    }
    
    /**
     * Parses a Directory as String
     * @param {Object} value
     */
    this.isDirectory = function(value){
        var regExp = new RegExp("^[^\ \t\n\r.\\\\]*$");
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
