
function WaebricPredicateParser(){
	
	this.currentToken;
	this.expressionParser = new WaebricExpressionParser();
	
	this.parse = function(parentParser){
		var predicate = this.parsePredicate(parentParser.currentToken);
		parentParser.currentToken = this.currentToken;
		return predicate;
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
        var isExpression = !isNotPredicate && hasContent && this.expressionParser.isExpression(token.nextToken());
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
		var predicate = this.parsePredicate(this.currentToken, true, true);
        return new NotPredicate(predicate);
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

}
