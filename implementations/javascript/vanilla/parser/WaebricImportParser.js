
function WaebricImportParser(){
	
	this.currentToken;
	
	this.parse = function(parentParser){
		var imprt = this.parseImport(parentParser.currentToken);
		parentParser.currentToken = this.currentToken;
		return imprt;
	}
	
	/**
     * Parses an Import
     * 
     * @param {WaebricParserToken} token
     * @return {Import}
     */
	this.parseImport = function(token){
		var moduleId = this.parseModuleId(token)
        return new Import(moduleId);
	}
	
	/**
     * Checks whether the input value equals the start of an IMPORT
     *
     * @param {WaebricParserToken} token
     * @return {Boolean}
     */
    this.isStartImport = function(token){
        return WaebricToken.KEYWORD.IMPORT.equals(token.value);
    }
}
