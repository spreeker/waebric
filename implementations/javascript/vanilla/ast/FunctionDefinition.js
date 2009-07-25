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
function FunctionDefinition (functionName, formals, statements){
	//Fields
	this.functionName = functionName;
	this.formals = formals;
	this.statements = statements;	
	
	//Methods
	this.toString = function(){
		return this.functionName + "(" + this.formals.join('') + ")";
	}
}
FunctionDefinition.prototype = new Node(); //Inheritance base class