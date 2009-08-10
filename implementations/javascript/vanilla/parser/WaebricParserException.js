/** 
 * Semantic Exception class 
 * 
 * Represents exceptions found during semantic validation
 * 
 */
function WaebricParserException(message){
	this.message = message;
	
	this.toString = function(){
		return this.message;
	}
}