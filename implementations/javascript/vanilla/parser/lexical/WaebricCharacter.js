/**
 * Waebric Character
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 *  
 * @param {Array} input An array of characters
 * @param {Number} position The position of the character in the array 
 */
WaebricCharacter = function(input, position){	

	this.value = input[position];
	this.position = new WaebricCharacter.Position(position)
	
	/**
	 * Checks whether a character follows the current character
	 * 
	 * @return {Boolean}
	 */
	this.hasNextChar = function(){
		return position < input.length;
	}
	
	/**
	 * Returns the next character in the input stream
	 * 
	 * @return {WaebricCharacter} 
	 */
	this.nextChar = function(){
		if (position < input.length){
			return new WaebricCharacter(input, position + 1);
		}else{
			return null
		}
	}

	/**
	 * Checks whether a previous character exists
	 * 
	 * @return {Boolean} 
	 */
	this.hasPreviousChar = function(){
		return position > 0;
	}	
	
	/**
	 * Returns the previous character in the input stream
	 * 
	 * @return {WaebricCharacter} 
	 */
	this.previousChar = function(){
		if (position > 0){
			return new WaebricCharacter(input, position - 1);
		}else{
			return null;
		}
	}
	
	/**
	 * Returns the value of the character
	 * 
	 * @return {String} 
	 */
	this.toString = function(){
		return this.value;
	}
	
	/**
	 * Checks whether the input equals the character's value
	 * 
	 * @parem {String} input The input value to be compared 
	 * @return {Boolean}
	 */
	this.equals = function(input){
		return this.value == input;
	}
	
	/**
	 * Matches the character's value against a regular expression
	 * 
	 * @param {RegExp} regExpr
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

WaebricCharacter.Position = function(line, column){
	
	this.line = line;
	this.column = column;	
	
	/**
	 * Calculates the new position (column, line) based on the input character
	 * A new line results in a new line number and the column number is reset.
	 * A new tab results in a multiple of the tabsize.
	 * All other characters results in an increased column number.
	 * 
	 * @param {Object} character
	 */
	this.update = function(character){
		if(character.value == '\n'){
			this.line ++;
			this.column = 1;
		}else if(character.value == '\t'){
			var tabsize = 4;
			this.column = 1 + (tabsize * (Math.floor((this.column - 1)/tabsize) + 1))
		}else {
			this.column++;
		}
	}
	
	/**
	 * Increases the current line position
	 * 
	 * @param {Number} count
	 */
	this.increaseLine = function(count){
		this.line = (this.line + count);
	}
	
	/**
	 * Increases the current column position
	 * 
	 * @param {Number} count
	 */
	this.increaseColumn = function(count){
		this.column = (this.column + count);
	}
	
	/**
	 * Decreases the current line position
	 * 
	 * @param {Number} count
	 */
	this.decreaseLine = function(count){
		this.line = (this.line - count);
	}
	
	/**
	 * Decreases the current column position
	 * 
	 * @param {Number} count
	 */
	this.decreaseColumn = function(count){
		this.column = (this.column - count);
	}
	
	/**
	 * Returns a clone of the current object
	 */
	this.clone = function(){
		return new WaebricCharacter.Position(this.line, this.column)
	}
	
	this.toString = function(){
		return 'line: ' + this.line + ' column: ' + this.column
	}
}

String.prototype.equals = function(input){
	return this.toString().toUpperCase() == input.toString().toUpperCase();
}
