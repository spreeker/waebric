/**************************************************************************** 
 * Base class for all Waebric Classes
 *  
 * @author Nickolas Heirbaut 
 ****************************************************************************/

function Node(){
	//Visitor pattern
	this.accept = function(visitorObject, env){
		visitorObject.visit(this, env);
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