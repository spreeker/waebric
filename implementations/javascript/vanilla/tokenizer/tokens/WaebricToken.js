/**
 * Waebric Token
 * 
 * The token's value is a sequence of characters
 * 
 * @param {Object} value
 */
WaebricToken = function(value){	
	this.value = value;
	this.type = "unrecognized"
	
	/**
	 * Adds a character/string to the token
	 * 
	 * @param {String} Character or String
	 * @return {String} The new value of the token
	 */
	this.addChar = function(character){
		return this.value += character;
	}
		
	/**
	 * @return {String} The value of the token
	 */
	this.toString = function(){
		return this.value;
	}
}