/**
 * Waebric Expression Parser
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 */
function WaebricExpressionParser(){
	
	/**
	 * Parses the input to an {Expression}
	 * Updates currentToken of the parent parser
	 * 
	 * @param {Object} parentParser The parent parser
	 * @param {Boolean} ignoreFieldExpression Flag to ignore {FieldExpression} during parsing
	 * @param {Boolean} ignoreCatExpression Flag to ignore {CatExpression} during parsing
	 */
	this.parse = function(parentParser, ignoreFieldExpression, ignoreCatExpression){
		this.parserStack.setStack(parentParser.parserStack)
		var expression = this.parseExpression(parentParser.currentToken);
		parentParser.setCurrentToken(this.currentToken)
		parentParser.parserStack.setStack(this.parserStack)
		return expression;
	}
	
    /**
     * Parses an expression
     *
     * @param {WaebricParserToken} token The token to parse
     * @param {Boolean} ignoreFieldExpression If true, no FieldExpressions are parsed
     * @param {Boolean} ignoreCatExpression If true, no CatExpressions are parsed
     * @return {CatExpression, FieldExpression, TextExpression, VarExpression, 
     * 			NatExpression, ListExpression, RecordExpression}
     */
    this.parseExpression = function(token, ignoreFieldExpression, ignoreCatExpression){
		this.parserStack.addParser('Expression')
		this.setCurrentToken(token);        			
		
		var expression;
        if (this.isCatExpression(this.currentToken) && !ignoreCatExpression) {
            expression = this.parseCatExpression(this.currentToken);
        } else if (this.isFieldExpression(this.currentToken) && !ignoreFieldExpression) {
            expression = this.parseFieldExpression(this.currentToken);
        } else if (this.isText(this.currentToken)) {			
            expression = this.parseTextExpression(this.currentToken);
        } else if (this.isIdentifier(this.currentToken)) {
            expression = this.parseVarExpression(this.currentToken);
        } else if (this.isNatural(this.currentToken)) {
            expression = this.parseNaturalExpression(this.currentToken)
        } else if (this.isStartList(this.currentToken)) {
            expression = this.parseListExpression(this.currentToken.nextToken())
        } else if (this.isStartRecord(this.currentToken)) {
            expression = this.parseRecordExpression(this.currentToken.nextToken())
        } else {
			throw new WaebricSyntaxException(this, 'Text, Natural, Identifier, "{" or "["', 'Start Expression');
        }
		this.parserStack.removeParser();
		return expression;
    }
	
	/**
	 * Checks whether the input value is an expression
	 *
	 * @param {WaebricParserToken} token The token to evaluate
	 * @return {Boolean}
	 */
	this.isExpression = function(token){
		if (token.value == null || token.value == WaebricToken.SYMBOL.SEMICOLON || WaebricToken.KEYWORD.END.equals(token.value)) {
			return false;
		} else {		
			return (this.isText(token) ||
			this.isIdentifier(token) ||
			this.isNatural(token) ||
			this.isStartRecord(token) ||
			this.isStartList(token));
		}
	}
	
	/**
     * Parses a CatExpression
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {CatExpression}
     */
    this.parseCatExpression = function(token){
		this.parserStack.addParser('CatExpression')
		this.setCurrentToken(token);  
        
        var expressionLeft = this.parseExpression(this.currentToken, false, true);
        var expressionRight = this.parseExpression(this.currentToken.nextToken().nextToken());
        var catExpression = new CatExpression(expressionLeft, expressionRight);
        
		this.parserStack.removeParser();
        return catExpression;
    }
	
	/**
	 * Checks whether the input value is a CatExpression
	 *
	 * @param {WaebricParserToken} token The token to evaluate
	 * @return {Boolean}
	 */
	this.isCatExpression = function(token){		
	    var isValidExpression = this.isExpression(token);
	    var tokenAfterNextExpression = this.getTokenAfterExpression(token);
	    var hasValidSeperator = (tokenAfterNextExpression.value == WaebricToken.SYMBOL.PLUS);
	    var hasValidField = hasValidSeperator && this.isExpression(tokenAfterNextExpression.nextToken());
	    return isValidExpression && hasValidSeperator && hasValidField;
	}
	
	/**
     * Parses a FieldExpression
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {FieldExpression}
     */
    this.parseFieldExpression = function(token){
		this.parserStack.addParser('FieldExpression')
		this.setCurrentToken(token); 		
        
		//Parse token (currentToken is not updated)
        var expression = this.parseExpression(this.currentToken, true);
		
		//Retreive token after expression and set it to currentToken
		var fieldToken = this.getTokenAfterExpression(this.currentToken).nextToken(); 
		this.setCurrentToken(fieldToken); 
		
		//Make new field expression		
        var fieldExpression = new FieldExpression(expression, fieldToken.value.toString());		
		
        while (this.currentToken.nextToken().value == WaebricToken.SYMBOL.DOT) {
            if (this.isIdentifier(this.currentToken.nextToken().nextToken())) {
				//Update current token position
				this.setCurrentToken(this.currentToken.nextToken().nextToken())
				
				//Make new field expression
                fieldExpression = new FieldExpression(fieldExpression, this.currentToken.value.toString());
            } else {
				throw new WaebricSyntaxException(this, 'Identifier', 'Field');
            }
        }
        //If the fieldExpression is followed by a "PLUS" sign, than it is part of a catExpression.
        //Cannot be forseen before since the fieldexpression has a higher priority
        var catExpressionFollows = this.currentToken.nextToken().value == WaebricToken.SYMBOL.PLUS;
        if (catExpressionFollows) {
            var expressionLeft = fieldExpression;
            var expressionRight = this.parseExpression(this.currentToken.nextToken().nextToken());
            var catExpression = new CatExpression(expressionLeft, expressionRight);
			this.parserStack.removeParser();
            return catExpression;
        } else {
			this.parserStack.removeParser();
            return fieldExpression;
        }
    }
	
	/**
	 * Checks whether the input value is the start of a field expression
	 *
	 * @param {WaebricParserToken} token The token to evaluate
	 * @return {Boolean}
	 */
	this.isFieldExpression = function(token){
	    var isValidExpression = this.isExpression(token);		
	    var tokenAfterNextExpression = this.getTokenAfterExpression(token)
	    var hasValidSeperator = tokenAfterNextExpression.value == WaebricToken.SYMBOL.DOT
	    
	    return (isValidExpression && hasValidSeperator);
	}
	
    /**
     * Parses TEXT
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {TextExpression}
     */
    this.parseTextExpression = function(token){
		this.setCurrentToken(token); 
        return new TextExpression(this.currentToken.value.toString());
    }  
	
	/**
	 * Checks whether the input value equals TEXT
	 *
	 * @param {WaebricParserToken} token The token to evaluate
	 * @return {Boolean}
	 */
	this.isText = function(token){		
	    var regExp = new RegExp('^([^\x00-\x1F\<\"\x80-\xFF]*[\n\r\t]*(\\\\&)*(\\\\")*(&#[0-9]+;)*(&#x[0-9a-fA-F]+;)*(&[a-zA-Z_:][a-zA-Z0-9.-_:]*;)*)*$');		
	    return (token.value instanceof WaebricToken.TEXT) && (token.value.match(regExp) != null);
	} 
	
	/**
     * Parses a VarExpression
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {VarExpression}
     */
    this.parseVarExpression = function(token){
		this.setCurrentToken(token); 
        return new VarExpression(this.currentToken.value.toString());
    } 
	
	/**
	 * Checks whether the input value equals IDENTIFIER
	 *
	 * @param {WaebricParserToken} token The token to evaluate
	 * @return {Boolean}
	 */
	this.isIdentifier = function(token){
	    var regExp = new RegExp('^([A-Za-z][A-Za-z0-9\-]*)$');
	    return (token.value instanceof WaebricToken.IDENTIFIER && token.value.match(regExp) && !WaebricToken.KEYWORD.contains(token.value.toString()));
	}
	
	 /**
     * Parses a NATURAL
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {NatExpression}
     */
    this.parseNaturalExpression = function(token){
		this.setCurrentToken(token); 
        return new NatExpression(this.currentToken.value.toString());
    }
	
	/**
	 * Checks whether the input value equals a NATURAL
	 *
	 * @param {WaebricParserToken} token The token to evaluate
	 * @return {Boolean}
	 */
	this.isNatural = function(token){
	    var regExp = new RegExp('[0-9]$');
	    return (token.value instanceof WaebricToken.NATURAL && token.value.match(regExp));
	}
	
	/**
     * Parses a RECORD
     *
     * @param {WaebricParserToken} token
     * @return {RecordExpression}
     */
    this.parseRecordExpression = function(token){
        this.parserStack.addParser('RecordExpression')
		this.setCurrentToken(token); 
        
        var list = new Array();
        while (this.currentToken.value != WaebricToken.SYMBOL.RIGHTCBRACKET) {
            var hasMultipleRecordItems = list.length > 0;
            var hasValidSeperator = this.currentToken.value == WaebricToken.SYMBOL.COMMA
            
            if (hasMultipleRecordItems && hasValidSeperator) {
                this.currentToken = this.currentToken.nextToken(); //Skip comma	
            } else if (hasMultipleRecordItems) {
				throw new WaebricSyntaxException(this, WaebricToken.SYMBOL.COMMA, 
					'Record seperator', 'RecordExpression');
            }

            //Parse KeyValuePair
            var expression = this.parseKeyValuePair(this.currentToken);
            list.push(expression);
            this.currentToken = this.currentToken.nextToken()
        }
		this.parserStack.removeParser();
        return new RecordExpression(list);
    }
	
	/**
	 * Checks whether the input value equals the start of a RECORD
	 *
	 * @param {WaebricParserToken} token The token to evaluate
	 * @return {Boolean}
	 */
	this.isStartRecord = function(token){
	    return (token.value == WaebricToken.SYMBOL.LEFTCBRACKET)
			&& this.isIdentifier(token.nextToken()) 
			&& token.nextToken().nextToken().value == WaebricToken.SYMBOL.COLON;
	}
	
	/**
     * Parses a KeyValuePair
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {KeyValuePair}
     */
    this.parseKeyValuePair = function(token){
        this.parserStack.addParser('KeyValuePair')
		this.setCurrentToken(token); 
        
        var key;
        var value;
        
		//Parse Key
        key = this.currentToken.value.toString()
		
		//Parse Colon
		var hasValidSeperator = this.currentToken.nextToken().value == WaebricToken.SYMBOL.COLON;
		if (!hasValidSeperator) {
			throw new WaebricSyntaxException(this, ':', 'Key/Value seperator');
        }
        this.currentToken = this.currentToken.nextToken().nextToken(); 
        		
		//Parse Expression
        if (this.isExpression(this.currentToken)) {
           value = this.parseExpression(this.currentToken)
        } else {
			throw new WaebricSyntaxException(this, 'Expression', 'Value');
        }
        
		this.parserStack.removeParser();
        return new KeyValuePair(key, value);
    }
	
	/**
	 * Checks whether the input equals the start of a list
	 *
	 * @param {WaebricParserToken} token The token to evaluate
	 * @return {Boolean}
	 */
	this.isStartList = function(token){
	    return (token.value == WaebricToken.SYMBOL.LEFTBBRACKET);
	}
	
	
	/**
     * Parses an ExpressionList
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {ListExpression}
     */
    this.parseListExpression = function(token){
        this.parserStack.addParser('ListExpression')
		this.setCurrentToken(token); 
        
        var list = new Array();
        while (this.currentToken.value != WaebricToken.SYMBOL.RIGHTBBRACKET) {
            //Detect the list-item seperator
            var hasMultipleListItems = list.length > 0
            var hasValidSeperator = this.currentToken.value == WaebricToken.SYMBOL.COMMA;
            
            if (hasMultipleListItems && hasValidSeperator) {
                this.currentToken = this.currentToken.nextToken(); //Skip comma	
            } else if (hasMultipleListItems) {
				throw new WaebricSyntaxException(this, WaebricToken.SYMBOL.COMMA, 'List seperator');
            }
            var expression = this.parseExpression(this.currentToken);
            list.push(expression);
            this.currentToken = this.currentToken.nextToken()
        }
		this.parserStack.removeParser();
        return new ListExpression(list);
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
}
WaebricExpressionParser.prototype = new WaebricBaseParser();
