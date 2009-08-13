/** 
 * Waebric Syntax Exception class 
 * 
 * Represents a syntactic error found during Parsing
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 * 
 * @param {Object} parser The parser where the error occured
 * @param {String} expectedValue The expected value
 * @param {String} astObject The name of the AST object that was tried to be parsed
 */
function WaebricSyntaxException(parser, expectedValue, expectedValueComment){
	this.parser = parser
	this.expectedValue = expectedValue;
	this.expectedValueComment = expectedValueComment;	
	
	this.toString = function(){			
			
			return 'WaebricSyntaxException:' +
			'\nParsing failed at "' +
			parser.parserStack.getLastParser() +
			' Parser"' +
			'\n\nExpected: ' +
			this.expectedValue +
			' (' +
			this.expectedValueComment +
			')' +
			'\n\nActual: ' +
			this.parser.currentToken.value + this.isKeyword() +
			'\n\nPosition: ' +
			this.parser.currentToken.value.position +
			'\n\nStack:' +
			'\n' +
			this.parser.parserStack.toString();
	}
	
	this.isKeyword = function(){
		if(WaebricToken.KEYWORD.contains(parser.currentToken.value.value)){
			return ' [Reserved keyword not allowed]';
		}else{
			return ''
		}
	}
}