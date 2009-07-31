/**************************************************************************** 
 * Specifies a ModuleId used in Module and Import
 * 
 * @author Nickolas Heirbaut 
 ****************************************************************************/

/**
 * ModuleId Class
 * 
 * (IdCon ".")* -> ModuleId ("module-id")
 * 
 * @param {Object} identifier
 */
function ModuleId (identifier){
	//Fields
	this.identifier = identifier;	
	
	//Methods
	this.toString = function(){
		return this.identifier;
	}
}
ModuleId.prototype = new Node(); //Inheritance base class