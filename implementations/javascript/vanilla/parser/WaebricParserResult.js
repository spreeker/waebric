/**
 * Represents the result of the parser
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 *  
 * @param {Module} module The Abstract Syntax Tree
 * @param {Array} exceptions Collection of {WaebricParserException}
 */
function WaebricParserResult(module, exceptions){
	this.module = module;
	this.exceptions = exceptions;
}
