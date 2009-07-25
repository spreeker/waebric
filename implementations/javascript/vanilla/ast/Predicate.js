/**************************************************************************** 
 * Specifies a Predicate used in Statement
 * 
 * @author Nickolas Heirbaut 
 ****************************************************************************/

/**
 * Not Predicate Class
 * 
 * "!" Predicate -> Predicate ("not")
 * 
 * @param {Object} predicate
 */
function NotPredicate (predicate){
	this.predicate = predicate;
}
NotPredicate.prototype = new Node(); //Inheritance base class

/**
 * And Predicate Class
 * 
 * Predicate "&&" Predicate -> Predicate ("and")
 * 
 * @param {Object} predicateLeft
 * @param {Object} predicateRight
 */
function AndPredicate (predicateLeft, predicateRight){
	this.predicateLeft = predicateLeft;
	this.predicateRight = predicateRight;
}
AndPredicate.prototype = new Node(); //Inheritance base class

/**
 * Or Predicate Class 
 * 
 * Predicate "||" Predicate -> Predicate ("or")
 * 
 * @param {Object} predicateLeft
 * @param {Object} predicateRight
 */
function OrPredicate (predicateLeft, predicateRight){
	this.predicateLeft = predicateLeft;
	this.predicateRight = predicateRight;
}
OrPredicate.prototype = new Node(); //Inheritance base class

/**
 * Is-a Predicate Class
 * 
 * Expression "." Type "?" -> Predicate
 * 
 * @param {Object} expression
 * @param {Object} type
 */
function IsAPredicate (expression, type){
	this.expression = expression;
	this.type = type;
}
IsAPredicate.prototype = new Node(); //Inheritance base class