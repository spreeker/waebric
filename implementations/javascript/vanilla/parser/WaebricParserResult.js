/**
 * Represents the result of the parser
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 *  
 * @param {Module} module The Abstract Syntax Tree
 * @param {Array} exceptionList
 */
function WaebricParserResult(module, exceptionList){
	this.module = module;
	this.exceptionList = exceptionList;
}