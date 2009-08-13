/**
 * Waebric Markup Parser
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 */
function WaebricMarkupParser(){
	
	this.expressionParser = new WaebricExpressionParser();

	/**
	 * Parses the input to {MarkupCall, DesignatorTag}
	 * Updates currentToken of the parent parser
	 * 
	 * @param {Object} parentParser The parent parser
	 * @return {MarkupCall, DesignatorTag}
	 */
	this.parseSingle = function(parentParser){		
		this.parserStack.setStack(parentParser.parserStack)
		var markup = this.parseMarkup(parentParser.currentToken);
		parentParser.setCurrentToken(this.currentToken);
		parentParser.parserStack.setStack(this.parserStack)
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
		this.parserStack.setStack(parentParser.parserStack)
		var markups = this.parseMarkups(parentParser.currentToken);
		parentParser.setCurrentToken(this.currentToken);
		parentParser.parserStack.setStack(this.parserStack)
		return markups;
	}
	
	/**
	 * Parses the input to {MarkupCall, DesignatorTag}
	 * 
	 * @param {WaebricParserToken} token The token to parse
	 * @return {MarkupCall, DesignatorTag}
	 */
	this.parseMarkup = function(token){
		this.parserStack.addParser('Markup');
		this.setCurrentToken(token);  
		
		var markup;
		if(this.isMarkupCall(this.currentToken)){			
			var designator = this.parseDesignator(this.currentToken);
			var arguments = this.parseArguments(this.currentToken.nextToken());
			markup = new MarkupCall(designator, arguments);		
		}else{
			markup = this.parseDesignator(this.currentToken);
		}
		
		this.parserStack.removeParser();
		return markup;
	}
    
	/**
	 * Parses the input to a collection of {MarkupCall, DesignatorTag}
	 * 
	 * @param {WaebricParserToken} token The token to parse
	 * @return {Array} Collection of {MarkupCall, DesignatorTag}
	 */
	this.parseMarkups = function(token){
		this.parserStack.addParser('Markups');
		this.setCurrentToken(token);  
		
		var markups = new Array();
		while(this.isMarkup(this.currentToken)){
			var markup = this.parseMarkup(this.currentToken);
			markups.push(markup);
			this.currentToken = this.currentToken.nextToken();
		}
		this.currentToken = this.currentToken.previousToken();
		this.parserStack.removeParser();
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
        this.parserStack.addParser('DesignatorTag');
		this.setCurrentToken(token);  
        
        var idCon;
        var attributes;
        
        //Parse identifier
        if (this.expressionParser.isIdentifier(this.currentToken)) {
            idCon = this.currentToken.value.toString();
        } else {
			throw new WaebricSyntaxException(this, 'Identifier', 'Tag name');
        }
        
        //Parse formals
        attributes = this.parseAttributes(this.currentToken);
        
		this.parserStack.removeParser();
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
        this.parserStack.addParser('Arguments');
		this.setCurrentToken(token.nextToken());  
        
        var arguments = new Array();
        while (this.currentToken.value != WaebricToken.SYMBOL.RIGHTRBRACKET) {
            var hasMultipleArguments = arguments.length > 0;
            var hasValidSeperator = this.currentToken.value == WaebricToken.SYMBOL.COMMA;
            
            if (hasMultipleArguments && hasValidSeperator) {
                this.currentToken = this.currentToken.nextToken();
            } else if (hasMultipleArguments) {
				throw new WaebricSyntaxException(this, WaebricToken.SYMBOL.COMMA, 'Argument seperator');
            }
            var argument = this.parseArgument(this.currentToken);
            arguments.push(argument);
            this.currentToken = this.currentToken.nextToken();
        }     
		
		this.parserStack.removeParser();   
        return arguments;
    }
    
    /**
     * Parse single argument
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {Object} Argument or Expression
     */
    this.parseArgument = function(token){
        this.parserStack.addParser('Argument');
		this.setCurrentToken(token);  
		
		var argument;
        if (this.currentToken.value instanceof WaebricToken.IDENTIFIER 
			&& this.currentToken.nextToken().value == WaebricToken.SYMBOL.EQ) {
            argument = this.parseAttributeArgument(this.currentToken);
        } else {
            argument = this.parseRegularArgument(this.currentToken);
        }
		
		this.parserStack.removeParser();
		return argument;
    }
    
    /**
     * Parses regular argument
     *
     * @param {WaebricParserToken} token The token to parse
     * @param {Object} An expression
     */
    this.parseRegularArgument = function(token){
        this.parserStack.addParser('RegularArgument');
		this.setCurrentToken(token);  
        var expression = this.expressionParser.parse(this);
		this.parserStack.removeParser();
		return expression;
    }
    
    /**
     * Parses an attribute argument
     *
     * @param {WaebricParserToken} token The token to parse
     * @param {Argument}
     */
    this.parseAttributeArgument = function(token){
        this.parserStack.addParser('AttributeArgument');
		this.setCurrentToken(token);  
		
		//Parsing KEY
        var idCon = this.currentToken.value.toString();
		
		//Parsing EQUAL SIGN
		this.currentToken = this.currentToken.nextToken().nextToken();
		
        //Parsing Expression
        if (!this.expressionParser.isExpression(this.currentToken)) {
			throw new WaebricSyntaxException(this, 'Expression', 'Value of attribute');
        }
		var expression = this.expressionParser.parse(this);
		
		this.parserStack.removeParser();
        return new Argument(idCon, expression);
    }	
	
	 /**
     * Parses attributes
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {Array} An array with attributes
     */
    this.parseAttributes = function(token){
        this.parserStack.addParser('Attributes');
		this.setCurrentToken(token);  
		
        var attributes = new Array();
        while (this.isStartAttribute(this.currentToken.nextToken())) {
            var attribute = this.parseAttribute(this.currentToken.nextToken());
            attributes.push(attribute);
			this.parserStack.removeParser();
        }
		
		this.parserStack.removeParser();
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
        this.parserStack.addParser('Attribute');
		this.setCurrentToken(token);  
		
		var attribute;
        if (this.currentToken.value == WaebricToken.SYMBOL.CROSSHATCH) {
            attribute = this.parseIDAttribute(this.currentToken.nextToken());
        } else if (this.currentToken.value == WaebricToken.SYMBOL.DOT) {
            attribute = this.parseClassAttribute(this.currentToken.nextToken());
        } else if (this.currentToken.value == WaebricToken.SYMBOL.DOLLAR) {
            attribute = this.parseNameAttribute(this.currentToken.nextToken());
        } else if (this.currentToken.value == WaebricToken.SYMBOL.COLON) {
            attribute = this.parseTypeAttribute(this.currentToken.nextToken());
        } else if (this.currentToken.value == WaebricToken.SYMBOL.AT) {
            attribute = this.parseWidthHeightAttribute(this.currentToken.nextToken());
        }else{
			throw new WaebricSyntaxException(this, '"#" or "." or "$" or ":" or "@"', 'Shorthand attribute');
		}
		
		this.parserStack.removeParser();
		return attribute;
    }
    
    /**
     * Parses an ID Attribute
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {IdAttribute}
     */
    this.parseIDAttribute = function(token){
        this.parserStack.addParser('IDAttribute');
		this.setCurrentToken(token);  
		
		if (this.expressionParser.isIdentifier(this.currentToken)) {
			var idValue = this.currentToken.value.toString();
		}else{
			throw new WaebricSyntaxException(this, 'Identifier', 'Value of the shorthand attribute id');
		}
		this.parserStack.removeParser();
        return new IdAttribute(idValue);
    }
    
    /**
     * Parses a Class Attribute
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {ClassAttribute}
     */
    this.parseClassAttribute = function(token){
        this.parserStack.addParser('ClassAttribute');
		this.setCurrentToken(token);  
		
        if (this.expressionParser.isIdentifier(this.currentToken)) {
			var classValue = this.currentToken.value.toString();
		}else{
			throw new WaebricSyntaxException(this, 'Identifier', 'Value of the shorthand attribute class');
		}
		this.parserStack.removeParser();
        return new ClassAttribute(classValue);
    }
    
    /**
     * Parses a Name Attribute
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {NameAttribute}
     */
    this.parseNameAttribute = function(token){
        this.parserStack.addParser('NameAttribute');
		this.setCurrentToken(token);  
		
        if (this.expressionParser.isIdentifier(this.currentToken)) {
			var nameValue = this.currentToken.value.toString();
		}else{
			throw new WaebricSyntaxException(this, 'Identifier', 'Value of the shorthand attribute name');
		}
		this.parserStack.removeParser();
        return new NameAttribute(nameValue);
    }
    
    /**
     * Parses an Type Attribute
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {TypeAttribute}
     */
    this.parseTypeAttribute = function(token){
        this.parserStack.addParser('TypeAttribute');
		this.setCurrentToken(token);  
		
        if (this.expressionParser.isIdentifier(this.currentToken)) {
			var typeValue = this.currentToken.value.toString();
		}else{
			throw new WaebricSyntaxException(this, 'Identifier', 'Value of the shorthand attribute type');
		}
		this.parserStack.removeParser();
        return new TypeAttribute(typeValue);
    }
    
    /**
     * Parses an Width/Height Attribute
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {WidthAttribute}
     */
    this.parseWidthHeightAttribute = function(token){
		this.parserStack.addParser('WidthHeightAttribute');
		this.setCurrentToken(token);  
		
		var widthValue;
		var heightValue;
		
		if (this.expressionParser.isNatural(this.currentToken)) {
			 widthValue = this.currentToken.value.toString();
		}else{
			throw new WaebricSyntaxException(this, 'Number', 'Value of the shorthand attribute width');
		}
		
		var heightAttributeFollows = this.currentToken.nextToken().value == WaebricToken.SYMBOL.PERCENT
		var heightAttributeIsNatural = this.expressionParser.isNatural(this.currentToken.nextToken().nextToken());

		if (heightAttributeFollows) {
			this.currentToken = this.currentToken.nextToken().nextToken()
			if (heightAttributeIsNatural) {
				heightValue = this.currentToken.value.toString();
				this.parserStack.removeParser();
				return new WidthHeightAttribute(widthValue, heightValue);
			}else{
				throw new WaebricSyntaxException(this, 'Number', 'Value of the shorthand attribute height');
			}
        }
		this.parserStack.removeParser();
        return new WidthAttribute(widthValue);
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
WaebricMarkupParser.prototype = new WaebricBaseParser();