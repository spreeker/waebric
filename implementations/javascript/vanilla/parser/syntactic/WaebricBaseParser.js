/**
 * Waebric Base Parser
 * 
 * Base class for all AST parsers
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 */
function WaebricBaseParser(){
	this.parserStack = new WaebricParserStack();
	this.currentToken;
	
	this.setCurrentToken = function(token){
		this.currentToken = token;
	}
}
