
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
        if (this.isStartModule(this.currentToken)) {
            return this.parseModule(this.currentToken.nextToken()); //Skip keyword "Module"
        } else {
            print('Error parsing module. Expected keyword Module as first token but token is ' +
            this.currentToken.value);
        }
    }
    
    this.isStartModule = function(token){
        return WaebricToken.KEYWORD.MODULE.equals(token.value)
    }
    
    /**
     * Parses the module root
     * "Module" ModuleId ModuleElements*
     */
    this.parseModule = function(token){
        this.currentToken = token;
        var moduleId;
        var moduleElements;
        
        //Parse ModuleId
        if (this.isModuleIdElement(this.currentToken.value)) {
            moduleId = this.parseModuleId(this.currentToken);
        } else {
            print('Error parsing module. Expected ModuleId but token is ' + this.currentToken.value);
        }
        
        //Parse ModuleElements
        if (this.isModuleElementElement(this.currentToken.nextToken().value)) {
            moduleElements = this.parseModuleElement(this.currentToken.nextToken());
        } else {
            print('Error parsing module. Expected start ModuleElement (SITE/DEF/IMPORT) but token is ' +
            this.currentToken.value);
        }
        
        return new Module(moduleId, moduleElements);
    }
    
    this.isModuleIdElement = function(value){
        return this.isIdentifier(value);
    }
    
    this.isModuleId = function(value){
        var regExp = new RegExp('^([A-Za-z][A-Za-z0-9\-]*)(.([A-Za-z][A-Za-z0-9\-]*))$');
        return value.match(regExp);
    }
    
    /**
     * Parses the ModuleID
     * ModuleId = IdCon listOf("." IdCon)
     */
    this.parseModuleId = function(token){
        this.currentToken = token;
        
        //Parse first part ModuleId -> IdCon		
        var value = this.currentToken.value;
        
        //Parse remaining parts ModuleID -> listOf("." IdCon)
        while (this.currentToken.nextToken().value == "." &&
        this.isModuleIdElement(this.currentToken.nextToken().nextToken().value)) {
            value += this.currentToken.nextToken().value;
            value += this.currentToken.nextToken().nextToken().value;
            this.currentToken = this.currentToken.nextToken().nextToken();
        }
        
        return new ModuleId(value)
    }
    
    this.isModuleElementElement = function(value){
        return WaebricToken.KEYWORD.IMPORT.equals(value) ||
        WaebricToken.KEYWORD.SITE.equals(value) ||
        WaebricToken.KEYWORD.DEF.equals(value)
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
                print('Error parsing module elements. Expected IMPORT/SITE/DEF but current token is ' + this.currentToken.value);
                this.currentToken = this.currentToken.nextToken();
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
        
        //Parse mapping
        var mappings = this.parseMappings(token);
        //Skip the END keyword of SITE
        this.currentToken = this.currentToken.nextToken();
        
        return new Site(mappings);
    }
    
    this.parseMappings = function(token){
        this.currentToken = token;
        
        var mappings = new Array();
        while (!WaebricToken.KEYWORD.END.equals(this.currentToken.value)) {
            //Skip ";" seperator
            if (mappings.length > 0 && this.currentToken.value == WaebricToken.SYMBOL.SEMICOLON) {
                this.currentToken = this.currentToken.nextToken();
            } else if (mappings.length > 0 && this.currentToken.value != WaebricToken.SYMBOL.SEMICOLON) {
                if (!WaebricToken.KEYWORD.END.equals(this.currentToken.nextToken().value)) {
                    print('Error parsing Mapping. Expected semicolon after mapping but found ' + this.currentToken.value);
                } else {
                    print('Error parsing Mapping. The last SITE MAPPING should be followed by "END" but found ' + this.currentToken.value);
                }
            }
            
            //Get current mapping
            var mapping = this.parseMapping(this.currentToken);
            
            //Save current mapping
            mappings.push(mapping);
            
            //Navigate to next token (skips the last token)
            this.currentToken = this.currentToken.nextToken();
            
            if (WaebricToken.SYMBOL.SEMICOLON.equals(this.currentToken.value) &&
            WaebricToken.KEYWORD.END.equals(this.currentToken.nextToken().value)) {
                print('Error parsing Mapping. The last SITE MAPPING should be followed by "END" but found ' + this.currentToken.value);
            }
        }
        return mappings;
    }
    
    /**
     * Parses a Site Mapping
     * Mapping = Path ":" Markup
     */
    this.parseMapping = function(token){
        this.currentToken = token;
        var path = this.parsePath(this.currentToken);
        var markup;
        
        if (this.currentToken.value == WaebricToken.SYMBOL.COLON && this.isMarkup(this.currentToken.nextToken().value)) {
            markup = this.parseMarkup(this.currentToken.nextToken());
        } else {
            print('Error parsing Mapping. Expected colon after path but found ' + this.currentToken.value);
        }
        
        return new Mapping(path, markup);
    }
	
	this.isMarkup = function(value){
		return value instanceof WaebricToken.IDENTIFIER;
	}
    
    
    this.parseFunctionDefinition = function(token){
        this.currentToken = token;
        
        var identifier;
        var formals = new Array();
        var statements = new Array();
        
        //First token should be an identifier
        if (this.isIdentifier(this.currentToken.value)) {
            identifier = this.parseIdentifier(this.currentToken);
        } else {
            print('Error parsing function definition. Expected a FUNCTION NAME (IDENTIFIER) but found ' + this.currentToken.value);
        }
        
        //Next token can be the start of formals
        if (this.isStartFormals(this.currentToken.nextToken().value)) {
            formals = this.parseFormals(this.currentToken.nextToken().nextToken());
        }
        
        //Remaining tokens are part of statements
        statements = this.parseStatements(this.currentToken.nextToken());
        
        return new FunctionDefinition(identifier, formals, statements)
    }
    
    
    
    this.isStartFormals = function(value){
        return value == WaebricToken.SYMBOL.LEFTRBRACKET;
    }
    
    this.parseFormals = function(token){
        this.currentToken = token;
        
        var formals = new Array();
        while (this.currentToken.value != WaebricToken.SYMBOL.RIGHTRBRACKET) {
            //Skip COMMA seperator
            if (formals.length > 0 && this.currentToken.value == WaebricToken.SYMBOL.COMMA) {
                this.currentToken = this.currentToken.nextToken();
            } else if (formals.length > 0) {
                print('Error parsing Formals. Expected COMMA after previous formal but found ' + this.currentToken.value);
            }
            
            if (this.isIdentifier(this.currentToken.value)) {
                formals.push(this.currentToken.value);
                this.currentToken = this.currentToken.nextToken();
            } else {
                print('Error parsing Formals. Expected formal but found ' + this.currentToken.value);
            }
        }
        return formals;
    }
	
	this.parseStatements = function(token){
		this.currentToken = token;
		
		var statements = new Array();
		while(!WaebricToken.KEYWORD.END.equals(this.currentToken.value)){
			statement = this.parseStatement(this.currentToken);
			statements.push(statement);
			this.currentToken = this.currentToken.nextToken();
			
			//Last token should be a semicolon
			if(this.currentToken.value == WaebricToken.SYMBOL.SEMICOLON){
				this.currentToken = this.currentToken.nextToken(); //Skip semicolon
			}else{
				print('Error parsing Statements. Expected SEMICOLON after statement but found ' + this.currentToken.value);
			}
		}
		return statements;
	}
    
    this.parseStatement = function(token){
        this.currentToken = token;
		
        if (WaebricToken.KEYWORD.IF.equals(this.currentToken.value)) {
            return this.parseIfElseStatement(this.currentToken);
        } else if(this.isMarkup(this.currentToken.value)){
			return this.parseMarkup(this.currentToken)
		}else {
            print('Error parsing statement. Expected start of a statement but found ' + this.currentToken.value);
            this.currentToken = this.currentToken.nextToken();
        }
        return new Array();
    }
	
	this.parseIfElseStatement = function(token){
		this.currentToken = token.nextToken(); //Skip IF keyword

		var predicate;
		var ifStatement;
		var elseStatement;
		
		//Parse predicate
		if(this.isStartPredicate(this.currentToken)){
			predicate = this.parsePredicate(this.currentToken.nextToken());
			//print('--> ' + predicate)
		}else{
			print('Error parsing IF/ELSE statement. Expected predicate but found ' + this.currentToken.nextToken().value);
		}
		
		//Predicate should be ended correctly
		if(this.currentToken.nextToken().value == WaebricToken.SYMBOL.RIGHTRBRACKET){
			this.currentToken = this.currentToken.nextToken();			
		}else{
			print('Error parsing IF/ELSE statement. Expected ending of predicate ")" but found ' + this.currentToken.nextToken().value);
		}

		//Parse IF statement
		ifStatement = this.parseStatement(this.currentToken.nextToken());

		//Parse ELSE statement
		if(WaebricToken.KEYWORD.ELSE.equals(this.currentToken.nextToken().nextToken().value)){			
			elseStatement = this.parseStatement(this.currentToken.nextToken().nextToken().nextToken());
			return new IfElseStatement(predicate, ifStatement, elseStatement);
		}else{
			return new IfStatement(predicate, ifStatement);
		}
	}
	
	this.isStartPredicate = function(token){	
		if(token.value != WaebricToken.SYMBOL.LEFTRBRACKET){
			return false;
		}
		if (token.nextToken().value == WaebricToken.SYMBOL.RIGHTRBRACKET){
			return false;
		}
		if ( token.nextToken().value != WaebricToken.SYMBOL.EXCLAMATION	 &&	!this.isExpression(token.nextToken().value)){
			return false;
		}
		return true;
	}
	
	this.parseNotPredicate = function(token){
		this.currentToken = token;
		return new NotPredicate(this.currentToken.value);
	}
	
	this.parsePredicate = function(token, ignoreDoubleAnd, ignoreDoubleOr){
		this.currentToken = token;		
		var predicate;
		
		//Parse SINGLE predicates
		if(this.currentToken.value == WaebricToken.SYMBOL.EXCLAMATION){
			predicate = new NotPredicate(this.parsePredicate(this.currentToken.nextToken(), true, true));		
		}else if(this.isExpression(this.currentToken.value)){			
			predicate = this.parseExpression(this.currentToken)
			
			//If the expression is followed by a question mark, then this is a predicate of type "is-a-predicate"
			if (this.currentToken.nextToken().value == "?"){
				var type = this.parsePredicateType(this.currentToken);
				predicate = new IsAPredicate(predicate, type)
			} 			
		}
		
		//Parse AND and OR predicates
		if(!ignoreDoubleAnd && this.currentToken.nextToken().value == WaebricToken.SYMBOL.DOUBLEAND){
			predicate = this.parseAndPredicate(this.currentToken.nextToken(), predicate)
		}else if(!ignoreDoubleOr && this.currentToken.nextToken().value == WaebricToken.SYMBOL.DOUBLEOR){
			predicate = this.parseOrPredicate(this.currentToken.nextToken(), predicate)
		}
		
		return predicate;
	}
	
	this.parsePredicateType = function(token){
		this.currentToken = token;
		
		switch(this.currentToken.value.toString()){
			case "list"   : this.currentToken = token.nextToken(); return new ListType();
			case "record" : this.currentToken = token.nextToken(); return new RecordType();
			case "string" : this.currentToken = token.nextToken(); return new StringType();
			default: print('Error parsing predicate type. Expected LIST/RECORD/STRING but found ' + this.currentToken.value);
		}
	}
	
	this.parseAndPredicate = function(token, predicate){
		this.currentToken = token;
		var currentPredicate = predicate;
		
		//Parse all && predicates
		do{
			var predicateRight = this.parsePredicate(this.currentToken.nextToken(), true, true)
			currentPredicate = new AndPredicate(currentPredicate, predicateRight);			
			this.currentToken = this.currentToken.nextToken();
		}while(this.currentToken.value == WaebricToken.SYMBOL.DOUBLEAND)		
		
		//Parse remaining || predicates (if exists)
		if(this.currentToken.value == WaebricToken.SYMBOL.DOUBLEOR){
			currentPredicate = this.parseOrPredicate(this.currentToken, currentPredicate);			
		}else{
			this.currentToken = this.currentToken.previousToken();
		}
		
		return currentPredicate;
	}
	
	this.parseOrPredicate = function(token, predicate){
		this.currentToken = token;
		var currentPredicate = predicate;
		
		//Parse all || predicats
		do{
			var predicateRight = this.parsePredicate(this.currentToken.nextToken(), true, true)
			currentPredicate = new OrPredicate(currentPredicate, predicateRight);
			this.currentToken = this.currentToken.nextToken();
		}while(this.currentToken.value == WaebricToken.SYMBOL.DOUBLEOR)
		
		//Parse remaining && predicates (if exists)
		if(this.currentToken.value == WaebricToken.SYMBOL.DOUBLEAND){
			currentPredicate = this.parseAndPredicate(this.currentToken, currentPredicate);			
		}else{
			this.currentToken = this.currentToken.previousToken();
		}
		
		return currentPredicate;
	}
    
    
    /**
     * Parses Markup
     *
     * Markup = Designator Arguments
     * 		  | Designator
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
    
    this.parseDesignator = function(token){
        this.currentToken = token;
        
        var idCon;
        var attributes;
        
        //Parse identifier
        if (this.isIdentifier(this.currentToken.value)) {
            idCon = this.currentToken.value;
        } else {
            print('Error parsing Designator. Expected IDENTIFIER but found ' + this.currentToken.value);
        }
        
        //Parse formals
        attributes = this.parseAttributes(this.currentToken);
        
        return new DesignatorTag(idCon, attributes);
    }
    
    this.isAttribute = function(value){
        var regExp = new RegExp("[#.$:@]");
        return value.match(regExp);
    }
    
    this.parseArguments = function(token){
        this.currentToken = token.nextToken();
        
        var arguments = new Array();
        while (this.currentToken.value != WaebricToken.SYMBOL.RIGHTRBRACKET) {
            //Skip COMMA seperator
            if (arguments.length > 0 && this.currentToken.value == WaebricToken.SYMBOL.COMMA) {
                this.currentToken = this.currentToken.nextToken();
            } else if (arguments.length > 0) {
                print('Error parsing Arguments. Expected COMMA after previous argument but found ' + this.currentToken.value);
            }
            
            var argument = this.parseArgument(this.currentToken);
            arguments.push(argument);
            this.currentToken = this.currentToken.nextToken();
        }
        
        return arguments;
    }
    
    this.parseArgument = function(token){
        this.currentToken = token;
        if (this.currentToken.value instanceof WaebricToken.IDENTIFIER && this.currentToken.nextToken().value == WaebricToken.SYMBOL.EQ) {
            return this.parseAttributeArgument(this.currentToken);
        } else {
            return this.parseRegularArgument(this.currentToken);
        }
    }
    
    this.parseRegularArgument = function(token){
        this.currentToken = token;
        return this.parseExpression(this.currentToken);
    }
    
    this.parseAttributeArgument = function(token){
        this.currentToken = token;
        var idCon;
        var expression;
        
        //Parse IdCon
        if (this.isIdentifier(this.currentToken.value)) {
            idCon = this.currentToken.value;
        } else {
            print('Attribute should start with an identifier but found ' + this.currentToken.value)
        }
        
        //Skip equality sign
        if (this.currentToken.hasNextToken() && this.currentToken.nextToken().hasNextToken()) {
            this.currentToken = this.currentToken.nextToken().nextToken();
        } else {
            print('Expected equality sign but found ' + this.currentToken.value);
        }
        
        //Parse arguments/attributes
        if (this.isExpression(this.currentToken.value)) {
            expression = this.parseExpression(this.currentToken);
        } else {
            print('Attribute is not correctly closed. Expected expression but found ' + this.currentToken.value)
        }
        
        return new Argument(idCon, expression);
    }
    
    this.isExpression = function(value){
        return this.isText(value) || this.isIdentifier(value) || this.isNatural(value) ||
        this.isStartRecord(value) ||
        this.isStartList(value) ||
        this.isFieldExpression(value);
    }
    
    this.parseExpression = function(token, ignoreFieldExpression, ignoreCatExpression){
        this.currentToken = token;
        //print('start parsing expression')
        
        if (this.isCatExpression(this.currentToken) && !ignoreCatExpression) {
            return this.parseCatExpression(this.currentToken);
        } else if (this.isFieldExpression(this.currentToken) && !ignoreFieldExpression) {
            return this.parseFieldExpression(this.currentToken);
        } else if (this.isCatExpression(this.currentToken) && !ignoreCatExpression) {
            return this.parseCatExpression(this.currentToken);
        } else if (this.isText(this.currentToken.value)) {
            return this.parseText(this.currentToken);
        } else if (this.isIdentifier(this.currentToken.value)) {
            return this.parseIdentifier(this.currentToken);
        } else if (this.isNatural(this.currentToken.value)) {
            return this.parseNatural(this.currentToken)
        } else if (this.isStartList(this.currentToken.value)) {
            return this.parseList(this.currentToken.nextToken())
        } else if (this.isStartRecord(this.currentToken.value)) {
            return this.parseRecord(this.currentToken.nextToken())
        } else {
            return "NYI"
        }
    }
    
    this.isText = function(value){
        var regExp = new RegExp('^([^\x00-\x1F\&\<\"\x80-\xFF]*[\n\r\t]*(\\\\&)*(\\\\")*(&#[0-9]+;)*(&#x[0-9a-fA-F]+;)*(&[a-zA-Z_:][a-zA-Z0-9.-_:]*;)*)*$');
        return value instanceof WaebricToken.TEXT && value.match(regExp);
    }
    
    this.parseText = function(token){
        this.currentToken = token;
        return new TextExpression(this.currentToken.value);
    }
    
    this.isIdentifier = function(value){
        var regExp = new RegExp('^([A-Za-z][A-Za-z0-9\-]*)$');
        return value instanceof WaebricToken.IDENTIFIER && value.match(regExp) && !WaebricToken.KEYWORD.contains(value.toString());
    }
    
    this.parseIdentifier = function(token){
        this.currentToken = token;
        return new VarExpression(this.currentToken.value);
    }
    
    this.isNatural = function(value){
        var regExp = new RegExp('[0-9]$');
        return value instanceof WaebricToken.NATURAL && value.match(regExp);
    }
    
    this.parseNatural = function(token){
        this.currentToken = token;
        return new NatExpression(this.currentToken.value);
    }
    
    this.isStartRecord = function(value){
        return value == WaebricToken.SYMBOL.LEFTCBRACKET;
    }
    
    this.parseRecord = function(token){
        this.currentToken = token;
        
        var list = new Array();
        while (this.currentToken.value != WaebricToken.SYMBOL.RIGHTCBRACKET) {
            //Detect the list-item seperator
            if (list.length > 0 && this.currentToken.value == WaebricToken.SYMBOL.COMMA) {
                this.currentToken = this.currentToken.nextToken(); //Skip comma	
            } else if (list.length > 0) {
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
    
    this.parseKeyValuePair = function(token){
        this.currentToken = token;
        
        var key;
        var value;
        
        //Parse Identifier
        if (this.isIdentifier(this.currentToken.value)) {
            key = this.parseIdentifier(this.currentToken)
        } else {
            print('Error parsing KeyValuePair. Expected IDENTIFIER as KEY but found ' + this.currentToken.value);
        }
        
        //Parse Colon
        if (this.currentToken.nextToken().value == WaebricToken.SYMBOL.COLON) {
            this.currentToken = this.currentToken.nextToken().nextToken(); //Skip colon
        } else {
            print('Error parsing KeyValuePair. Expected COLON after IDENTIFIER but found ' + this.currentToken.value);
        }
        
        //Parse Expression
        if (this.isExpression(this.currentToken.value)) {
            value = this.parseExpression(this.currentToken)
        } else {
            print('Error parsing KeyValuePair. Expected EXPRESSION as VALUE but found ' + this.currentToken.value);
        }
        
        return new KeyValuePair(key, value);
    }
    
    this.isStartList = function(value){
        return value == WaebricToken.SYMBOL.LEFTBBRACKET;
    }
    
    this.parseList = function(token){
        this.currentToken = token;
        
        var list = new Array();
        while (this.currentToken.value != WaebricToken.SYMBOL.RIGHTBBRACKET) {
            //Detect the list-item seperator
            if (list.length > 0 && this.currentToken.value == WaebricToken.SYMBOL.COMMA) {
                this.currentToken = this.currentToken.nextToken(); //Skip comma	
            } else if (list.length > 0) {
                print('Error parsing List. Expected COMMA after previous list item but found ' + this.currentToken.value);
                return;
            }
            var expression = this.parseExpression(this.currentToken);
            list.push(expression);
            this.currentToken = this.currentToken.nextToken()
        }
        return new ListExpression(list);
    }
    
    this.isFieldExpression = function(token){
        //First token should be an expression
        if (!this.isExpression(token.value)) {
            return false;
        }
        //Next token should be a DOT
        var tokenAfterNextExpression = this.getTokenAfterExpression(token)
        if (!(tokenAfterNextExpression.value == WaebricToken.SYMBOL.DOT)) {
            return false;
        }
        //2nd next token should be an Identifier
        if (!this.isIdentifier(tokenAfterNextExpression.nextToken().value)) {
            return false;
        }

        return true;
    }
    
    this.parseFieldExpression = function(token){
        this.currentToken = token;
        var expressionToken = this.currentToken;
        var fieldToken = this.getTokenAfterExpression(this.currentToken).nextToken();
        
        var expression = this.parseExpression(expressionToken, true);
        var field = this.parseIdentifier(fieldToken);
        var fieldExpression = new FieldExpression(expression, field);
        
        while (fieldToken.nextToken().value == WaebricToken.SYMBOL.DOT) {
            if (this.isIdentifier(fieldToken.nextToken().nextToken().value)) {
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
        if (this.currentToken.nextToken().value == '+') {
            var expressionLeft = fieldExpression;
            var expressionRight = this.parseExpression(this.currentToken.nextToken().nextToken());
            var catExpression = new CatExpression(expressionLeft, expressionRight);
            return catExpression;
        } else {
            return fieldExpression;
        }
    }
    
    this.isCatExpression = function(token){
        //First token should be an expression
        if (!this.isExpression(token.value)) {
            return false;
        }
        //Next token should be a colon
        var tokenAfterNextExpression = this.getTokenAfterExpression(token)
        if (!(tokenAfterNextExpression.value == WaebricToken.SYMBOL.PLUS)) {
            return false;
        }
        //2nd next token should be an Identifier
        if (!this.isExpression(tokenAfterNextExpression.nextToken().value)) {
            return false;
        }
        
        return true;
    }
    
    this.parseCatExpression = function(token){
        this.currentToken = token;
        
        var expressionLeft = this.parseExpression(this.currentToken, false, true);
        var expressionRight = this.parseExpression(this.currentToken.nextToken().nextToken());
        var catExpression = new CatExpression(expressionLeft, expressionRight);
        
        return catExpression;
    }
    
    this.getTokenAfterExpression = function(token){
        if (token.value == WaebricToken.SYMBOL.LEFTCBRACKET) {
            return this.getTokenAfterBracketEnding(token, WaebricToken.SYMBOL.LEFTCBRACKET, WaebricToken.SYMBOL.RIGHTCBRACKET)
        } else if (token.value == WaebricToken.SYMBOL.LEFTBBRACKET) {
            return this.getTokenAfterBracketEnding(token, WaebricToken.SYMBOL.LEFTBBRACKET, WaebricToken.SYMBOL.RIGHTBBRACKET)
        } else {
            return token.nextToken();
        }
    }
    
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
    
    this.parseAttributes = function(token){
        this.currentToken = token;
        var attributes = new Array();
        while (this.isAttribute(this.currentToken.nextToken().value)) {
            var attribute = this.parseAttribute(this.currentToken.nextToken());
            attributes.push(attribute);
        }
        return attributes;
    }
    
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
        if (this.currentToken.nextToken().value == WaebricToken.SYMBOL.PERCENT) {
            this.currentToken = this.currentToken.nextToken().nextToken();
            var heightValue = this.parseNaturalValue(this.currentToken);
            return new WidthHeightAttribute(widthValue, heightValue);
        } else {
            return new WidthAttribute(widthValue);
        }
    }
    
    this.parseIdentifierValue = function(token){
        this.currentToken = token;
        if (this.currentToken.value instanceof WaebricToken.IDENTIFIER) {
            return this.currentToken.value;
        }
        print('Error parsing Attribute value. Expected IDENTIFIER but found ' + this.currentToken.value);
    }
    
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
            if (this.currentToken.value == WaebricToken.SYMBOL.DOT) {
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
