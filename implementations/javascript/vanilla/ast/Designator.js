/**************************************************************************** 
 * Specifies an designator used in markup
 * 
 * @author Nickolas Heirbaut 
 ****************************************************************************/

/** 
 * Designator Tag Class
 *
 * IdCon Attribute* -> Designator ("tag")
 * 
 * @param {object} identifier
 * @param {Array} Array of attributes
 */
function DesignatorTag (idCon, attributes){
	this.idCon = idCon;
	this.attributes = attributes;
	
	this.containsClassAttribute = function(){
		for(var i = 0; i < this.attributes.length; i++){
			var attr = this.attributes[i];
			if(attr instanceof ClassAttribute){
				return true;
			}
		}
		return false;
	}
	
	
}
DesignatorTag.prototype = new Node(); //Inheritance base class 
