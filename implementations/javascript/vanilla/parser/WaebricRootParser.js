/**
 * Waebric Root Parser
 *  
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 */
function WaebricRootParser(){

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

WaebricRootParser.parse = function(tokenizerResult){
	var parser = new WaebricRootParser();
	return parser.parse(tokenizerResult);
}
	
	