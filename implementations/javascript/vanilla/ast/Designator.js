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
}
DesignatorTag.prototype = new Node(); //Inheritance base class 
