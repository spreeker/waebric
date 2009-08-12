/** 
 * Performs a semantic validation on the module object
 *
 * The semantic validation includes:
 *
 * - Undefined functions: If for a markup-call f, no function definition can be
 * 	 found in the current module or one of its transitive imports, and if f is
 * 	 not a tag defined in the XHTML 1.0 Transitional standard, then this is an error.
 *
 * - Undefined variables: If a variable reference x cannot be traced back to an
 *   enclosing let-definition or function parameter, this is an error.
 *
 * - Non-existing module: If for an import directive import m no corresponding file
 * 	 m.wae can be found, this is an error.
 *
 * - Duplicate definitions: Multiple function definitions with the same name are
 *   disallowed.
 *
 * - Arity mismathces: If a function is called with an incorrect numer of arguments
 *   (as follows from its definition), this is an error.
 *   
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 */
function WaebricValidator(){
	
	/**
	 * Validates a module and returns an exceptionlist of semantic errors.
	 * 
	 * @param {Module} module The module to be validated
	 * @return {Array} An array of semantic errors
	 * @exception {Error}
	 */
	this.validate = function(module){
		try {
			//Setup environment for logging exceptions and storing functions, 
			//variables and dependencies
			var env = new WaebricEnvironment();
			
			//Start semantic validation
			var visitor = new WaebricValidatorVisitor();
			module.accept(visitor.getModuleVisitor(env));
			
			//Return exception list
			return env.getExceptions()
		}catch(exception){
			throw exception;
		}
	}
}

/**
 * Validates a module and returns an exceptionlist of semantic errors.
 * 
 * @param {Module} module The module to be validated
 * @return {WaebricValidatorResult}
 * @exception {WaebricValidatorException}
 */
WaebricValidator.validate = function(module){
	try {
		var validator = new WaebricValidator();
		var exceptionList = validator.validate(module);
		return new WaebricValidatorResult(exceptionList);
	}catch(exception){
		throw new WaebricValidatorException(exception);
	}
}
