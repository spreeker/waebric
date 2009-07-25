/**************************************************************************** 
 * Specifies an attributes used in a designator tag 
 * 
 * @author Nickolas Heirbaut 
 ****************************************************************************/

/** 
 * ID Attribute Class
 * 
 * "#" IdCon -> Attribute ("id")
 * 
 * @param {object} identifier
 */
function IdAttribute(id){
	this.id = id;
}
IdAttribute.prototype = new Node(); //Inheritance base class

/** 
 * Class Attribute Class
 * 
 * "." IdCon -> Attribute ("class")
 * 
 * @param {object} class
 */
function ClassAttribute(className){
	this.className = className;
}
ClassAttribute.prototype = new Node(); //Inheritance base class

/** 
 * Name Attribute Class
 * 
 * "$" IdCon -> Attribute ("name")
 * 
 * @param {object} name
 */
function NameAttribute(name){
	this.name = name;
}
NameAttribute.prototype = new Node(); //Inheritance base class

/** 
 * Type Attribute Class
 * 
 * ":" IdCon -> Attribute ("type")
 * 
 * @param {object} type
 */
function TypeAttribute(type){
	this.type = type;
}
TypeAttribute.prototype = new Node(); //Inheritance base class

/** 
 * Width Height Attribute Class
 * 
 * "@" NatCon "%" NatCon -> Attribute ("width-height")
 * 
 * @param {object} width
 * @param {object} height
 */
function WidthHeightAttribute(width, height){
	this.width = width;
	this.height = height;
}
WidthHeightAttribute.prototype = new Node(); //Inheritance base class

/** 
 * WidthAttribute Class
 * 
 * "@" NatCon -> Attribute  ("height")
 * 
 * @param {object} height
 */
function WidthAttribute(width){
	this.width = width;
}
WidthAttribute.prototype = new Node(); //Inheritance base class