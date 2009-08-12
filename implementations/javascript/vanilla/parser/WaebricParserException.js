/** 
 * Parser Exception class 
 * 
 * Represents an exceptions occured during Parsing 
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 * 
 * @param {String} message The error message
 */
function WaebricParserException(message){
	this.message = message;
	
	this.toString = function(){
		return this.message;
	}
}