/**************************************************************************** 
 * Specifies the arguments in a designator call
 *  
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 ****************************************************************************/

/** 
 * Argument class
 * 
 * IdCon "=" Expression -> Argument ("attr")
 */
function Argument (variable, expression){
	this.variable = variable;
	this.expression = expression;	
	
	this.toString = function(){
		return this.variable + '=' + this.expression.toString();
	}
}
Argument.prototype = new Node();





