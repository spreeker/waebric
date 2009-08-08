/**
 * Waebric Parser
 *  
 * @author Nickolas Heirbaut
 */
function WaebricParser(){

    this.currentToken;
	this.moduleParser = new WaebricModuleParser();
	
	/**
	 * Parses the WaebricTokenizerResult to an Abstract Syntax Tree {Module}.
	 * 
	 * @param {WaebricTokenizerResult} tokenizerResult
	 * @return {Module}
	 */
	this.parse = function(tokenizerResult){
        //Store first token globally
        this.currentToken = new WaebricParserToken(tokenizerResult.tokens, 0)
        
        //Start parsing the module
        if (this.moduleParser.isStartModule(this.currentToken.value)) {
			this.currentToken = this.currentToken.nextToken();
            return this.moduleParser.parse(this); //Skip keyword "Module"
        } else {
            print('Error parsing module. Expected keyword Module as first token but found ' +
            this.currentToken.value);
        }
    }
}

/**
 * Parses the WaebricTokenizerResult to an Abstract Syntax Tree {Module}.
 * 
 * @param {WaebricTokenizerResult} tokenizerResult
 * @return {Module}
 */
WaebricParser.parse = function(tokenizerResult){
    var parser = new WaebricParser();
    var module = parser.parse(tokenizerResult);
	return new WaebricParserResult(module, new Array());
}
