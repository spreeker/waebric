
function WaebricFunctionDefinitionParser(){
	
	this.currentToken;
	this.expressionParser = new WaebricExpressionParser();
	this.statementParser = new WaebricStatementParser();
	
	this.parse = function(parentParser){
		var functionDef = this.parseFunctionDefinition(parentParser.currentToken);
		parentParser.currentToken = this.currentToken;
		return functionDef;
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
        if (this.expressionParser.isIdentifier(this.currentToken)) {
            identifier = this.expressionParser.parseIdentifier(this.currentToken);
        } else {
            print('Error parsing function definition. Expected a FUNCTION NAME (IDENTIFIER) but found ' + this.currentToken.value);
        }
		
        //Next token can be the start of formals
        if (this.statementParser.isFormal(this.currentToken.nextToken())) {
            formals = this.statementParser.parseFormals(this.currentToken.nextToken().nextToken());
			this.currentToken = this.statementParser.currentToken;
        }
        
        //Remaining tokens are part of statements
		this.currentToken = this.currentToken.nextToken();
        statements = this.statementParser.parseMultiple(this);
        
		if(WaebricToken.KEYWORD.END.equals(this.currentToken.value)){
			return new FunctionDefinition(identifier, formals, statements)
		}else{
			print('Error parsing function definition. Expected END after statements but found ' + this.currentToken.nextToken().value);
		}
    }
}
