/**************************************************************************** 
 * Specifies a Statement used in Predicate
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 ****************************************************************************/

/**
 * List Type Class
 * 
 * "list" -> Type ("list-type")
 */
function ListType(){	
	this.toString = function(){
		return 'list'
	}
}
ListType.prototype = new Node(); //Inheritance base class

/**
 * Record Type Class
 * 
 * "record" -> Type ("record-type")
 */
function RecordType(){
	this.toString = function(){
		return 'record'
	}
}
RecordType.prototype = new Node(); //Inheritance base class

/**
 * String Type Class
 * 
 * "string" -> Type ("string-type")
 */
function StringType(){
	this.toString = function(){
		return 'string'
	}
}
StringType.prototype = new Node(); //Inheritance base class