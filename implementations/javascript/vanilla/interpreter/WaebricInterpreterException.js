/** 
 * Waebric Interpreter Exception class 
 * 
 * Represents an error found during Interpreting.
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 * 
 * @param {String} message Description of the error
 */
function WaebricInterpreterException(message){
	this.message = message;
	
	this.toString = function(){
		return 'WaebricInterpreterException:' 
				+ '\Interpreting failed'
				+ '\n' + this.message
	}
}