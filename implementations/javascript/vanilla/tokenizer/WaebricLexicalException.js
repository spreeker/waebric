/** 
 * Waebric Lexical Exception class 
 * 
 * Represents a lexical error found during Tokenization
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 * 
 * @param {String} message The error message
 * @param {Position} position The position of the error in the input string
 */
function WaebricLexicalException(message, position){
	this.message = message;
	this.position = position;
	
	this.toString = function(indentCount){
		return 'WaebricLexicalException:' 
				+ '\n====> ' + this.message 
				+ '\n====> ' + this.position;
	}
}