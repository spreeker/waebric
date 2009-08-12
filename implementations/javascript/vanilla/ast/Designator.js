/**************************************************************************** 
 * Specifies an designator used in markup
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
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
	
	this.toString = function(){
		return '[DesignatorTag: ' + this.idCon + this.attributes +']';
	}
}
DesignatorTag.prototype = new Node(); //Inheritance base class 
