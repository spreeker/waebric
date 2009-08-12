/**
 * Waebric Token
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 * 
 * @param {Object} value
 */
WaebricToken = function(value, position){	
	this.value = value;
	this.type = "unrecognized"
	this.position = position;
	
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

WaebricToken.EMPTY_TOKEN_VALUE = '';