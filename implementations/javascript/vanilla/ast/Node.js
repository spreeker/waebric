/**************************************************************************** 
 * Base class for all Waebric Classes
 *  
 * @author Nickolas Heirbaut 
 ****************************************************************************/

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
	
	//Add slashes (copied from http://javascript.about.com/library/bladdslash.htm)
	this.addSlashes = function(str) {
		str=str.replace(/\\/g,'\\\\');
		str=str.replace(/\'/g,'\\\'');
		str=str.replace(/\"/g,'\\"');
		str=str.replace(/\0/g,'\\0');
		return str;
	}

	//Strip slashes (copied from http://javascript.about.com/library/bladdslash.htm)
	this.stripSlashes = function(str) {
		str=str.replace(/\\'/g,'\'');		
		str=str.replace(/\\"/g,'"');		
		str=str.replace(/\\\\/g,'\\');		
		str=str.replace(/\\0/g,'\0');		
		return str;
	}
}