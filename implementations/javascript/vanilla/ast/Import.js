/**************************************************************************** 
 * Specifies an Import used in ModuleElement
 * 
 * @author Nickolas Heirbaut 
 ****************************************************************************/

/**
 * Import Class
 * 
 * "import" ModuleId ("import")
 * 
 * @param {Object} moduleId
 */
function Import(moduleId){
	this.moduleId = moduleId;
}
Import.prototype = new Node(); //Inheritance base class