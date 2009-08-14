/** 
 * Waebric Tokenizer Exception class 
 * 
 * Represents an exception found during tokenization
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 * 
 * @param {String} message The error message
 * @param {Position} position The position of the error in the input string
 * @param {String} path The path of the Waebric program on the filesystem
 * @param {Object} childException The encapsulated exception
 */
function WaebricTokenizerException(message, position, path, childException){
	this.message = message;
	this.position = position;
	this.path = path;
	this.childException = childException;
	
	this.toString = function(indentCount){
		if (!childException) {
			return 'WaebricTokenizerException:' 
				+ '\n====> ' + this.message
				+ '\n====> ' + this.position;
		}else if (childException instanceof WaebricLexicalException){
			return 'WaebricTokenizerException:'
				+ '\nError while tokenizing ' + this.path
				+ '\n\n' + this.childException;
		}else{
			return 'WaebricTokenizerException:'				
				+ '\n\n' + this.childException;
		}
	}
}