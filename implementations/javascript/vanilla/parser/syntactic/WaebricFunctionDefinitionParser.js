/**
 * Waebric FunctionDefinition Parser
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 */
function WaebricFunctionDefinitionParser(){

	this.expressionParser = new WaebricExpressionParser();
	this.statementParser = new WaebricStatementParser();
	
	/**
	 * Parses the input to a {FunctionDefinition}
	 * Updates currentToken of the parent parser
	 * 
	 * @param {Object} parentParser The parent parser
	 */
	this.parse = function(parentParser){
		this.parserStack.setStack(parentParser.parserStack)
		var functionDef = this.parseFunctionDefinition(parentParser.currentToken);
		parentParser.setCurrentToken(this.currentToken);
		parentParser.parserStack.setStack(this.parserStack)
		return functionDef;
	}
	
	/**
     * Checks whether the input value equals the start of a FUNCTIONDEFINITION
     *
     * @param {WaebricParserToken} token The token to evaluate
     * @return {Boolean}
     */
    this.isStartFunctionDef = function(token){
        return WaebricToken.KEYWORD.DEF.equals(token.value);
    }
    
    /**
     * Parses a FunctionDefinition
     * "def" IdCon Formals Statement* "end"
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {FunctionDefinition}
     */
    this.parseFunctionDefinition = function(token){		
        this.parserStack.addParser('FunctionDefinition')
		this.setCurrentToken(token);  
        
        var identifier;
        var formals = new Array();
        var statements = new Array();
        
        //First token should be an identifier
        if (this.expressionParser.isIdentifier(this.currentToken)) {
            identifier = this.currentToken.value.toString();
        } else {
			throw new WaebricSyntaxException(this, 'Identifier', 'Functionname');
        }
		
        //Next token can be the start of formals
        if (this.statementParser.isFormal(this.currentToken.nextToken())) {
            formals = this.statementParser.parseFormals(this.currentToken.nextToken().nextToken());
			this.setCurrentToken(this.statementParser.currentToken);
        }
        
        //Remaining tokens are part of statements
		this.setCurrentToken(this.currentToken.nextToken());
        statements = this.statementParser.parseMultiple(this);

		if(!WaebricToken.KEYWORD.END.equals(this.currentToken.value)){		
			if (this.currentToken.hasNextToken()) {
				this.setCurrentToken(this.currentToken.nextToken());
				throw new WaebricSyntaxException(this, WaebricToken.KEYWORD.END, 'FunctionDefinition closing');
			}else{
				this.currentToken.value = 'EOF (End of Line!)'
				throw new WaebricSyntaxException(this, WaebricToken.KEYWORD.END, 'FunctionDefinition closing');
			}
		}
		this.parserStack.removeParser();
		return new FunctionDefinition(identifier, formals, statements)
    }
}
WaebricFunctionDefinitionParser.prototype = new WaebricBaseParser();
