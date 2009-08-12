/** 
 * Semantic Exception class 
 * 
 * Represents exceptions found during semantic validation
 * 
 */
function WaebricSemanticException(message){
	this.message = message;
	
	this.toString = function(){
		return this.message;
	}
}

/** 
 * Semantic Exception: non-existing modules
 * 
 * @param {String} path The path of the non existing module
 */
function NonExistingModuleException(path){
	this.path = path;
	this.message = "The module " + path + " was not found on the filesystem.";
}
NonExistingModuleException.prototype = new WaebricSemanticException();

/** 
 * Semantic Exception: undefined functions
 * 
 * @param {Markup} functionCall The function call for which no function definition exists
 * @param {WaebricEnvironment} env The environment where the exception occured
 */
function UndefinedFunctionException(functionCall, env){
	this.message = "The function '" + functionCall + "' in module '" + env.getParentModule().name + "'was not found in any of the loaded modules, nor is the functionname part of XHTML";
}
UndefinedFunctionException.prototype = new WaebricSemanticException();

/** 
 * Semantic Exception: duplicate functions
 * 
 * @param {FunctionDefinition} func The function definition 
 * @param {WaebricEnvironment} env The environment where the exception occured
 */
function DuplicateDefinitionException(func, env){
	this.message = "Duplicate function found: " + func + "' in module '" + env.getParentModule().name + "'";
}
DuplicateDefinitionException.prototype = new WaebricSemanticException();

/** 
 * Semantic Exception: functions calls with incorrect arguments 
 * 
 * @param {Markup} functionCall The function call with the incorrect number of arguments
 * @param {WaebricEnvironment} env The environment where the exception occured
 */
function IncorrectArgumentsException(functionCall, env){
	this.message = "The functioncall '" + functionCall + "' in module '" + env.getParentModule().name + "' has an incorrect number of arguments. ";
}
IncorrectArgumentsException.prototype = new WaebricSemanticException();

/** 
 * Semantic Exception: undefined variables
 * 
 * @param {VarExpression} variable The reference to the variable
 * @param {WaebricEnvironment} env The environment where the exception occured
 */
function UndefinedVariableException(variable, env){
	this.message = "The variable '" + variable + "' in module '" + env.getParentModule().name + "' is not declared.";
}
UndefinedVariableException.prototype = new WaebricSemanticException();
