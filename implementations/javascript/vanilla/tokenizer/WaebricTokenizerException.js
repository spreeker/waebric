/** 
 * Waebric Tokenizer Exception class 
 * 
 * Represents exceptions found during tokenization
 * 
 */
function WaebricTokenizerException(message){
	this.message = message;
	
	this.toString = function(){
		return this.message;
	}
}