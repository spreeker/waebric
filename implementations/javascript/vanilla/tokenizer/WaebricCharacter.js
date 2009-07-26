/**
 * Waebric Character
 * 
 * @param {String} input
 * @param {Number} position
 */
WaebricCharacter = function(input, position){	
	this.value = input[position];

	/**
	 * @return {Boolean} True if a character follows the current character.
	 */
	this.hasNextChar = function(){
		return position < input.length;
	}
	
	/**
	 * @return {String} The next character in the input stream
	 */
	this.nextChar = function(){
		if (position < input.length){
			return new WaebricCharacter(input, position + 1);
		}else{
			return null
		}
	}
	
	/**
	 * @return {String} The previous character in the input stream
	 */
	this.previousChar = function(){
		if (position > 0){
			return new WaebricCharacter(input, position - 1);
		}else{
			return null;
		}
	}
	
	/**
	 * @return {String} The value of the character
	 */
	this.toString = function(){
		return this.value;
	}
	
	/**
	 * Determines if the input equals the character's value
	 * 
	 * @return {Boolean} True if character's value equals the input
	 */
	this.equals = function(input){
		return this.value == input;
	}
	
	/**
	 * Matches the character's value against a regular expression
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

String.prototype.equals = function(input){
	return this.toUpperCase() == input.toUpperCase();
}
