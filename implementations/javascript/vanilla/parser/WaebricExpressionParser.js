
function WaebricExpressionParser(){
	
	this.currentToken;
	
	this.parse = function(parentParser, ignoreFieldExpression, ignoreCatExpression){
		var expression = this.parseExpression(parentParser.currentToken);
		parentParser.currentToken = this.currentToken;
		return expression;
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
        } else if (this.isText(this.currentToken)) {			
            return this.parseTextExpression(this.currentToken);
        } else if (this.isIdentifier(this.currentToken)) {
            return this.parseVarExpression(this.currentToken);
        } else if (this.isNatural(this.currentToken)) {
            return this.parseNaturalExpression(this.currentToken)
        } else if (this.isStartList(this.currentToken)) {
            return this.parseListExpression(this.currentToken.nextToken())
        } else if (this.isStartRecord(this.currentToken)) {
            return this.parseRecordExpression(this.currentToken.nextToken())
        } else {
            return "NYI"
        }
    }
	
	/**
	 * Checks whether the input value is an expression
	 *
	 * @param {WaebricParserToken} token
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
	 * Checks whether the input value is a CatExpression
	 *
	 * @param {WaebricParserToken} token
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
     * @param {WaebricParserToken} token
     * @return {FieldExpression}
     */
    this.parseFieldExpression = function(token){
        this.currentToken = token;
        var expressionToken = this.currentToken;
        var fieldToken = this.getTokenAfterExpression(this.currentToken).nextToken();
        
        var expression = this.parseExpression(expressionToken, true);
        var field = fieldToken.value.toString();
        var fieldExpression = new FieldExpression(expression, field);
        
        while (fieldToken.nextToken().value == WaebricToken.SYMBOL.DOT) {
            if (this.isIdentifier(fieldToken.nextToken().nextToken())) {
                var field = fieldToken.nextToken().nextToken().value.toString();
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
	    
	    return (isValidExpression && hasValidSeperator && hasValidField);
	}
	
    /**
     * Parses TEXT
     *
     * @param {WaebricParserToken} token
     * @return {TextExpression}
     */
    this.parseTextExpression = function(token){
        this.currentToken = token;
        return new TextExpression(this.currentToken.value.toString());
    }  
	
	/**
	 * Checks whether the input value equals TEXT
	 *
	 * @param {WaebricParserToken} token
	 * @return {Boolean}
	 */
	this.isText = function(token){		
	    var regExp = new RegExp('^([^\x00-\x1F\&\<\"\x80-\xFF]*[\n\r\t]*(\\\\&)*(\\\\")*(&#[0-9]+;)*(&#x[0-9a-fA-F]+;)*(&[a-zA-Z_:][a-zA-Z0-9.-_:]*;)*)*$');
	    return (token.value instanceof WaebricToken.TEXT) && (token.value.match(regExp) != null);
	} 
	
	/**
     * Parses a VarExpression
     *
     * @param {WaebricParserToken} token
     * @return {VarExpression}
     */
    this.parseVarExpression = function(token){
        this.currentToken = token;
        return new VarExpression(this.currentToken.value.toString());
    } 
	
	/**
	 * Checks whether the input value equals IDENTIFIER
	 *
	 * @param {WaebricParserToken} token
	 * @return {Boolean}
	 */
	this.isIdentifier = function(token){
	    var regExp = new RegExp('^([A-Za-z][A-Za-z0-9\-]*)$');
	    return (token.value instanceof WaebricToken.IDENTIFIER && token.value.match(regExp) && !WaebricToken.KEYWORD.contains(token.value.toString()));
	}
	
	 /**
     * Parses a NATURAL
     *
     * @param {WaebricParserToken} token
     * @return {NatExpression}
     */
    this.parseNaturalExpression = function(token){
        this.currentToken = token;
        return new NatExpression(this.currentToken.value.toString());
    }
	
	/**
	 * Checks whether the input value equals a NATURAL
	 *
	 * @param {WaebricParserToken} token
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
	 * Checks whether the input value equals the start of a RECORD
	 *
	 * @param {WaebricParserToken} token
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
     * @param {WaebricParserToken} token
     * @return {KeyValuePair}
     */
    this.parseKeyValuePair = function(token){
        this.currentToken = token;
        
        var key;
        var value;
        
        key = this.currentToken.value.toString()
        this.currentToken = this.currentToken.nextToken().nextToken(); //Skip colon
        
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
	    return (token.value == WaebricToken.SYMBOL.LEFTBBRACKET);
	}
	
	
	/**
     * Parses an ExpressionList
     *
     * @param {WaebricParserToken} token
     * @return {ListExpression}
     */
    this.parseListExpression = function(token){
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