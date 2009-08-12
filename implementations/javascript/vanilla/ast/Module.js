/**************************************************************************** 
 * Specifies a Module, the root object of the data structure
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 ****************************************************************************/

/**
 * Module Class
 * 
 * "module" ModuleId ModuleElement* -> Module ("module")
 * 
 * @param {Object} moduleId
 * @param {Array} Array of module elements
 */
function Module(moduleId, moduleElements){
	//Fields
	this.moduleId = moduleId;
	this.imports = new Array();
	this.site = new Site(new Array());
	this.functionDefinitions = new Array();
	this.dependencies = new Array();
	
	//Store different types of moduleElements seperate
	for (i = 0; i < moduleElements.length; i++) {
		if (moduleElements[i] instanceof Site) {
			this.site.mappings = this.site.mappings.concat(moduleElements[i].mappings);
		} else if (moduleElements[i] instanceof Import) {
			this.imports.push(moduleElements[i]);
		} else if (moduleElements[i] instanceof FunctionDefinition) {
			this.functionDefinitions.push(moduleElements[i]);
		}
	}
	
	this.toString = function(){
		return moduleId.identifier;
	}
}
Module.prototype = new Node(); //Inheritance base class
