/**
 * Waebric Root Parser
 *  
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 */
function WaebricRootParser(){

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
			this.setCurrentToken(this.currentToken.nextToken());
            var module = this.moduleParser.parse(this); //Skip keyword "Module"
            this.parserStack = module.parserStack;
			this.setCurrentToken(module.currentToken);
			return module;
        } else {
			throw new WaebricSyntaxException(this, "Module", "Start Waebric file");
        }
    }
}

WaebricRootParser.parse = function(tokenizerResult, path){
	try {	
		var parser = new WaebricRootParser();
		return parser.parse(tokenizerResult);
	}catch (exception){
		if (this.currentToken != null) {
			throw new WaebricParserException(exception.message, this.currentToken.value.position, path, exception)
		}else{
			throw new WaebricParserException(exception.message, exception.message, path, exception)
		}
	}
}
WaebricRootParser.prototype = new WaebricBaseParser();