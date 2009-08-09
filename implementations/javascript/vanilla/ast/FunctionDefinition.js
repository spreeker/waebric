/**************************************************************************** 
 * Specifies a Function Definition used in ModuleElement
 * 
 * @author Nickolas Heirbaut 
 ****************************************************************************/

/**
 * Function Definition Class
 * 
 * "def" IdCon Formals Statement* "end" -> FunctionDef ("def")
 * 
 * @param {Object} functionName
 * @param {Array} Array of formals
 * @param {Array} Array of statements
 */
function FunctionDefinition (functionName, formals, statements, isFunctionBinding){
	//Fields
	this.functionName = functionName;
	this.formals = formals;
	this.statements = statements;	
	this.isFunctionBinding = isFunctionBinding;
	
	//Methods
	this.toString = function(){
		return '[func-def: ' + this.functionName + ", " + this.formals.toString() + ", " + this.statements + ']';
	}
}
FunctionDefinition.prototype = new Node(); //Inheritance base class