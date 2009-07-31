/**
 * Waebric Tokenacter
 * 
 * @param {String} input
 * @param {Number} position
 */
WaebricParserToken = function(input, position){	
	this.value = input[position];

	/**
	 * @return {Boolean} True if a token follows the current token.
	 */
	this.hasNextToken = function(){
		return position < input.length - 1;
	}
	
	/**
	 * @return {String} The next token in the input stream
	 */
	this.nextToken = function(){
		if (position < input.length){
			return new WaebricParserToken(input, position + 1);
		}else{
			return null
		}
	}
	
	/**
	 * @return {String} The previous token in the input stream
	 */
	this.previousToken = function(){
		if (position > 0){
			return new WaebricParserToken(input, position - 1);
		}else{
			return null;
		}
	}
	
	/**
	 * Determines if the input equals the character's value
	 * 
	 * @return {Boolean} True if token's value equals the input
	 */
	this.equals = function(input){
		return this.value == input;
	}
	
	/**
	 * Matches the token's value against a regular expression
	 * 
	 * @param {Object} regExpr
	 * @return {String} The string that matches the regular expression
	 */
	this.match = function(regExpr){
		if (this.value != null) {
			return this.value.match(regExpr);
		}else{
			return false;
		}
	}
}
