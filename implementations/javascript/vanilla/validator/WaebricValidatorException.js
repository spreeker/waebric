/** 
 * Validator Exception class 
 * 
 * Represents an error occured during semantic validation
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 * 
 * @param {String} message A description of the exception
 */
function WaebricValidatorException(message){
	this.message = message;
	
	this.toString = function(){
		return this.message;
	}
}
