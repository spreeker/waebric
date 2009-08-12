/** 
 * Validator Exception class 
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 */
function WaebricValidatorException(message){
	this.message = message;
	
	this.toString = function(){
		return this.message;
	}
}
