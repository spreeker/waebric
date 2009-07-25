/**************************************************************************** 
 * Specifies a Statement used in Predicate
 * 
 * @author Nickolas Heirbaut 
 ****************************************************************************/

/**
 * List Type Class
 * 
 * "list" -> Type ("list-type")
 */
function ListType(){
	
}
ListType.prototype = new Node(); //Inheritance base class

/**
 * Record Type Class
 * 
 * "record" -> Type ("record-type")
 */
function RecordType(){
	
}
RecordType.prototype = new Node(); //Inheritance base class

/**
 * String Type Class
 * 
 * "string" -> Type ("string-type")
 */
function StringType(){
	
}
StringType.prototype = new Node(); //Inheritance base class