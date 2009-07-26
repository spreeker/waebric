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
}
