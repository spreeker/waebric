/**
 * Waebric Markup Parser
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 */
function WaebricMarkupParser(){
	
	this.currentToken;
	
	this.expressionParser = new WaebricExpressionParser();

	/**
	 * Parses the input to {MarkupCall, DesignatorTag}
	 * Updates currentToken of the parent parser
	 * 
	 * @param {Object} parentParser The parent parser
	 * @return {MarkupCall, DesignatorTag}
	 */
	this.parseSingle = function(parentParser){
		var markup = this.parseMarkup(parentParser.currentToken);
		parentParser.currentToken = this.currentToken;
		return markup;
	}
	
	/**
	 * Parses the input to a collection of {MarkupCall, DesignatorTag}
	 * Updates currentToken of the parent parser
	 * 
	 * @param {Object} parentParser The parent parser
	 * @return {Array} Collection of {MarkupCall, DesignatorTag}
	 */
	this.parseMultiple = function(parentParser){
		var markups = this.parseMarkups(parentParser.currentToken);
		parentParser.currentToken = this.currentToken;
		return markups;
	}
	
	/**
	 * Parses the input to {MarkupCall, DesignatorTag}
	 * 
	 * @param {WaebricParserToken} token The token to parse
	 * @return {MarkupCall, DesignatorTag}
	 */
	this.parseMarkup = function(token){
		this.currentToken = token;
		if(this.isMarkupCall(this.currentToken)){			
			var designator = this.parseDesignator(this.currentToken);
			var arguments = this.parseArguments(this.currentToken.nextToken());
			return new MarkupCall(designator, arguments);		
		}else{
			var designator = this.parseDesignator(this.currentToken);
			return designator
		}
	}
    
	/**
	 * Parses the input to a collection of {MarkupCall, DesignatorTag}
	 * 
	 * @param {WaebricParserToken} token The token to parse
	 * @return {Array} Collection of {MarkupCall, DesignatorTag}
	 */
	this.parseMarkups = function(token){
		this.currentToken = token;
		
		var markups = new Array();
		while(this.isMarkup(this.currentToken)){
			var markup = this.parseMarkup(this.currentToken);
			markups.push(markup);
			this.currentToken = this.currentToken.nextToken();
		}
		this.currentToken = this.currentToken.previousToken();
		return markups;
	}
		
	/**
	 * Checks whether the input value is valid markup
	 *
	 * @param {WaebricParserToken} token The token to evaluate
	 * @return {Boolean}
	 */
	this.isMarkup = function(token){
	    return token != null && token.value instanceof WaebricToken.IDENTIFIER;
	}

	/**
	 * Checks whether the input value is a valid MarkupCall
	 * 
	 * @param {WaebricParserToken} token The token to evaluate
	 * @return {Boolean}
	 */
	this.isMarkupCall = function(token){
		if (token.value instanceof WaebricToken.IDENTIFIER) {
			//Skip attributes
			while(this.isAttribute(token.nextToken())){
				token = token.nextToken().nextToken();
			}
			
            if (token.nextToken().value == WaebricToken.SYMBOL.LEFTRBRACKET) {
                return true;			
            }
        }
		return false;
	}
	
    /**
     * Parses a designator
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {Designator}
     */
    this.parseDesignator = function(token){
        this.currentToken = token;
        
        var idCon;
        var attributes;
        
        //Parse identifier
        if (this.expressionParser.isIdentifier(this.currentToken)) {
            idCon = this.currentToken.value.toString();
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
     * @param {WaebricParserToken} token The token to evaluate
     * @return {Boolean}
     */
    this.isAttribute = function(token){
        var regExp = new RegExp("^[#.$:@%]$");
        return token.value.match(regExp);
    }
	
	/**
     * Checks whether the input token is the start of an attribute
     *
     * @param {WaebricParserToken} token The token to evaluate
     * @return {Boolean}
     */
    this.isStartAttribute = function(token){
        var regExp = new RegExp("^[#.$:@]$");
        return token.value.match(regExp);
    }
    
    /**
     * Parse arguments
     *
     * @param {WaebricParserToken} token The token to parse
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
     * @param {WaebricParserToken} token The token to parse
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
     * @param {WaebricParserToken} token The token to parse
     * @param {Object} An expression
     */
    this.parseRegularArgument = function(token){
        this.currentToken = token;
        return this.expressionParser.parse(this);
    }
    
    /**
     * Parses an attribute argument
     *
     * @param {WaebricParserToken} token The token to parse
     * @param {Argument}
     */
    this.parseAttributeArgument = function(token){
        this.currentToken = token;
        var idCon;
        var expression;
        
        //Parse IdCon
        if (this.expressionParser.isIdentifier(this.currentToken)) {
            idCon = this.currentToken.value.toString();
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
        if (this.expressionParser.isExpression(this.currentToken)) {
            expression = this.expressionParser.parse(this);
        } else {
            print('Attribute is not correctly closed. Expected expression but found ' + this.currentToken.value)
        }
        return new Argument(idCon, expression);
    }	
	
	 /**
     * Parses attributes
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {Array} An array with attributes
     */
    this.parseAttributes = function(token){
        this.currentToken = token;
        var attributes = new Array();
        while (this.isStartAttribute(this.currentToken.nextToken())) {
            var attribute = this.parseAttribute(this.currentToken.nextToken());
            attributes.push(attribute);
        }
        return attributes;
    }
    
    /**
     * Parses single attribute
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {IDAttribute, ClassAttribute, NameAttribute, TypeAttribute, 
     * 			WidthHeightAttribute, WidthAttribute} 
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
		throw new Error()
        print('Error parsing attribute.')
    }
    
    /**
     * Parses an ID Attribute
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {IdAttribute}
     */
    this.parseIDAttribute = function(token){
        this.currentToken = token;
		if (this.expressionParser.isIdentifier(this.currentToken)) {
			var idValue = this.currentToken.value.toString();
		}else{
			print('Error parsing ID Attribute. Expected identifier value but found ' + this.currentToken.value)
		}
        return new IdAttribute(idValue);
    }
    
    /**
     * Parses a Class Attribute
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {ClassAttribute}
     */
    this.parseClassAttribute = function(token){
        this.currentToken = token;
        if (this.expressionParser.isIdentifier(this.currentToken)) {
			var classValue = this.currentToken.value.toString();
		}else{
			print('Error parsing Class Attribute. Expected identifier value but found ' + this.currentToken.value)
		}
        return new ClassAttribute(classValue);
    }
    
    /**
     * Parses a Name Attribute
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {NameAttribute}
     */
    this.parseNameAttribute = function(token){
        this.currentToken = token;
        if (this.expressionParser.isIdentifier(this.currentToken)) {
			var nameValue = this.currentToken.value.toString();
		}else{
			print('Error parsing Name Attribute. Expected identifier value but found ' + this.currentToken.value)
		}
        return new NameAttribute(nameValue);
    }
    
    /**
     * Parses an Type Attribute
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {TypeAttribute}
     */
    this.parseTypeAttribute = function(token){
        this.currentToken = token;
        if (this.expressionParser.isIdentifier(this.currentToken)) {
			var typeValue = this.currentToken.value.toString();
		}else{
			print('Error parsing Type Attribute. Expected identifier value but found ' + this.currentToken.value)
		}
        return new TypeAttribute(typeValue);
    }
    
    /**
     * Parses an Width/Height Attribute
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {WidthAttribute}
     */
    this.parseWidthHeightAttribute = function(token){
		this.currentToken = token;
		
		var widthValue;
		var heightValue;
		
		if (this.expressionParser.isNatural(this.currentToken)) {
			 widthValue = this.currentToken.value.toString();
		}else{
			print('Error parsing Width Attribute. Expected natural value but found ' + this.currentToken.value)
		}
		
		var heightAttributeFollows = this.currentToken.nextToken().value == WaebricToken.SYMBOL.PERCENT
		var heightAttributeIsNatural = this.expressionParser.isNatural(this.currentToken.nextToken().nextToken());

		if (heightAttributeFollows) {
			this.currentToken = this.currentToken.nextToken().nextToken()
			if (heightAttributeIsNatural) {
				heightValue = this.currentToken.value.toString();
				return new WidthHeightAttribute(widthValue, heightValue);
			}else{
				print('Error parsing WidthHeight Attribute. Expected natural value but found ' + this.currentToken.value)
			}
        } else {
            return new WidthAttribute(widthValue);
        }
    }

	/**
	 * Returns the token that follows a sequence of Markup objects.
	 * 
	 * @param {WaebricParserToken} token The token of the first Markup object in the sequence
	 * @return {WaebricParserToken}
	 */
	this.getTokenAfterMarkups = function(token){			
		var tempToken = token;
		while(this.isMarkup(tempToken)){
			tempToken = this.getTokenAfterMarkup(tempToken);
		}
		return tempToken;
	}
	
	/**
	 * Returns the token that follows after a single Markup object
	 * 
	 * @param {Object} token The token of the Markup object
	 * @return {WaebricParserToken} The token that follows after a single Markup object
	 */
	this.getTokenAfterMarkup = function(token){			
		var tempToken = token.nextToken(); //Skip identifier

		//Skip attributes
		while (this.isAttribute(tempToken)) {			
			tempToken = tempToken.nextToken().nextToken();		
		}
		
		//Skip formals
		var isStartFormal = tempToken.value == WaebricToken.SYMBOL.LEFTRBRACKET;
		if (isStartFormal) {
			while (tempToken.value != WaebricToken.SYMBOL.RIGHTRBRACKET) {
				tempToken = tempToken.nextToken();
			}
			tempToken = tempToken.nextToken();//Skip right bracket
		}
		return tempToken; 
	}

	/**
	 * Returns the last markup in a sequence of markup tokens
	 * 
	 * @param {WaebricParserToken} token The token of the first markup in the sequence of markup tokens
	 * @return {WaebricParserToken}
	 */
	this.getLastMarkup = function(token){
		var arrTokens = new Array();		
		while(this.isMarkup(token)){
			var token = this.getTokenAfterMarkup(token);
			arrTokens.push(token);
		}
		return arrTokens[arrTokens.length-2];
	}
}