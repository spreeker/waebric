/**
 * Holds the result of the semantic validation
 * 
 * @param {Object} module
 * @param {Object} exceptions
 */
function WaebricSemanticValidatorResult(exceptions){
	
	this.exceptions = exceptions;
	
	/**
	 * Adds a token to the tokenlist
	 * 
	 * @param {WaebricSemanticValidatorException} exception
	 * @return {Array} The new exceptionlist
	 */
	this.addException = function(exception){
		return this.exceptions.push(exception);
	}
}
