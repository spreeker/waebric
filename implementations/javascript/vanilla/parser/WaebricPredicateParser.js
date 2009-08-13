/**
 * Waebric Predicate Parser
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 */
function WaebricPredicateParser(){
	
	this.currentToken;
	this.expressionParser = new WaebricExpressionParser();
	
	/**
	 * Parses the input to {Embedding}
	 * Updates currentToken of the parent parser
	 * 
	 * @param {Object} parentParser The parent parser
	 * @return {NotPredicate, AndPredicate, OrPredicate, PredicateType, Expression}
	 */
	this.parse = function(parentParser){
		this.parserStack.setStack(parentParser.parserStack)
		var predicate = this.parsePredicate(parentParser.currentToken);
		parentParser.setCurrentToken(this.currentToken);
		parentParser.parserStack.setStack(this.parserStack)
		return predicate;
	}
	
	/**
     * Checks whether the input value equals the start of a predicate
     *
     * @param {WaebricParserToken} token The token to evaluate
     * @return {Boolean}
     */
    this.isStartPredicate = function(token){
        var hasContent = token.value != WaebricToken.SYMBOL.RIGHTRBRACKET;
        var isNotPredicate = hasContent && (token.value == WaebricToken.SYMBOL.EXCLAMATION);
        var isExpression = !isNotPredicate && hasContent && this.expressionParser.isExpression(token);
        var hasValidContent = isNotPredicate || isExpression

        return (hasValidContent);
    }
    
    /**
     * Parses a predicate
     *
     * @param {WaebricParserToken} token The token to parse
     * @param {Boolean} ignoreDoubleAnd Flag to ignore a && token
     * @param {Boolean} ignoreDoubleOr Flag to ignore a || token
     * @return {NotPredicate, AndPredicate, OrPredicate, PredicateType, Expression}
     */
    this.parsePredicate = function(token, ignoreDoubleAnd, ignoreDoubleOr){
        this.parserStack.addParser('Predicate');
		this.setCurrentToken(token);  
		
        var predicate;
        
        //Parse SINGLE predicates
        if (this.isNotPredicate(this.currentToken.value)) {
            predicate = this.parseNotPredicate(this.currentToken.nextToken());
        } else if (this.expressionParser.isExpression(this.currentToken)) {
            predicate = this.expressionParser.parse(this)
            
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
        
		this.parserStack.removeParser();
        return predicate;
    }
    
    /**
     * Checks whether the input value equals the start of a not-predicate
     *
     * @param {WaebricParserToken} token The token to evaluata
     * @return {Boolean}
     */
    this.isNotPredicate = function(token){
        return token.value == WaebricToken.SYMBOL.EXCLAMATION
    }
    
    /**
     * Parses a not-predicate
     *
     * @param {WaebricParserToken} token The token to be parsed
     * @return {NotPredicate}
     */
    this.parseNotPredicate = function(token){
        this.parserStack.addParser('NotPredicate');
		this.setCurrentToken(token);  
		var predicate = this.parsePredicate(this.currentToken, true, true);
		this.parserStack.removeParser();
        return new NotPredicate(predicate);
    }
    
    /**
     * Checks whether the input value equals the start of a not-predicate
     *
     * @param {WaebricParserToken} token The token to evaluate
     * @return {Boolean}
     */
    this.isEndPredicateType = function(token){
        return token.value == WaebricToken.SYMBOL.QUESTION
    }
    
    /**
     * Parses a TYPE predicate
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {PredicateType}
     */
    this.parsePredicateType = function(token){
        this.parserStack.addParser('IsAPredicate');
		this.setCurrentToken(token);  
        
		var type;
        switch (this.currentToken.value.toString()) {
            case "list":
                this.currentToken = token.nextToken();
                type = new ListType();
            case "record":
                this.currentToken = token.nextToken();
                type = new RecordType();
            case "string":
                this.currentToken = token.nextToken();
                type = new StringType();
            default:
				throw new WaebricSyntaxException(this, '"list", "record" or "string"', 'Predicate type to evaluate an expression');
        }
		this.parserStack.removeParser();
		return type;
    }
    
    /**
     * Parses an AND predicate
     *
     * @param {WaebricParserToken} token The token to parse
     * @param {AndPredicate} 
     */
    this.parseAndPredicate = function(token, predicate){
        this.parserStack.addParser('AndPredicate');
		this.setCurrentToken(token);  
		
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
        
		this.parserStack.removeParser();
        return currentPredicate;
    }
    
    /**
     * Parses an OR predicate
     *
     * @param {WaebricParserToken} token The token to parse
     * @param {OrPredicate} predicate
     */
    this.parseOrPredicate = function(token, predicate){
        this.parserStack.addParser('OrPredicate');
		this.setCurrentToken(token);  
		
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
        
		this.parserStack.removeParser();
        return currentPredicate;
    }
}
WaebricPredicateParser.prototype = new WaebricBaseParser();
