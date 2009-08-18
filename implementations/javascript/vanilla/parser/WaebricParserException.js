/** 
 * Parser Exception class 
 * 
 * Represents an exceptions occured during Parsing 
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 * 
 * @param {String} message The error message
 * @param {Position} position The position of the error in the input string
 * @param {String} filePath The path of the Waebric program on the filesystem
 * @param {Object} childException The encapsulated exception
 */
function WaebricParserException(message, position, filePath, childException){
	this.message = message;
	this.position = position;
	this.filePath = filePath;
	this.childException = childException;
	
	this.toString = function(){
		if (!childException) {
			return 'WaebricParserException:' 
				+ '\n====> ' + this.message;
		}else if(childException instanceof WaebricTokenizerException){
			return 'WaebricParserException:' 
				+ '\nParsing failed: ' + this.filePath
				+ '\n\n' + this.childException;
		}else if(childException instanceof WaebricSyntaxException){
			return 'WaebricParserException:' 
				+ '\nParsing failed: ' + this.filePath
				+ '\n\n' + this.childException;
		}else{
			return 'WaebricParserException:' 
				+ '\nParsing failed: ' + this.filePath
				+ '\n' + this.position;
				+ '\n\n' + this.childException;
		}
	}
}