/**
 * Holds the result of the Tokenization validation
 * 
 * @param {Array} An array of tokens
 */
function WaebricTokenizerResult(tokens){
	this.tokens = tokens;
	
	/**
	 * Adds a token to the tokenlist
	 * 
	 * @param {WaebricToken} token
	 * @return {Array} The new tokenlist
	 */
	this.addToken = function(token){
		return this.tokens.push(token);
	}
	
	/**
	 * Returns the last used keyword
	 * 
	 * @return Last used keyword in the tokenlist.
	 */
	this.getLastKeyword = function(){
		for(var i = this.tokens.length - 1; i >= 0; i--){
			var token = this.tokens[i];
			if(token.type == "KEYWORD"){
				return token;
			}
			
		}
		return new WaebricToken.COMMENT('');
	}
}
