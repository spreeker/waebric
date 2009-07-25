/**************************************************************************** 
 * Specifies a Site used in ModuleElement
 * 
 * @author Nickolas Heirbaut 
 ****************************************************************************/

/**
 * Site Class
 * 
 * Path ":" Markup -> Mapping
 * 
 * @param {Array} Array of mapping elements
 */
function Site (mappings){
	this.mappings = mappings;
}
Site.prototype = new Node(); //Inheritance base class