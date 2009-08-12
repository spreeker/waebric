/**************************************************************************** 
 * Base class for all Waebric Classes
 *  
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 ****************************************************************************/

/**
 * Base class for AST
 */
function Node(){
	//Visitor pattern
	this.accept = function(visitorObject){
		visitorObject.visit(this);
	}
	
	//ToString methods
	this.toString = function(){
		var output = [];
		for(item in this){
			if (this.hasOwnProperty(item)) {
				output.push(item + ": " + this[item]);
			}
		}
		return output.toString();
	}
}