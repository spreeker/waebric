/**
 * Wrapper class for DOM Document
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 */
function DOM(){
	this.document = document.implementation.createDocument().loadXML('<html/>');	
	this.lastElement = this.document.documentElement;
	this.lastValue;				
	this.yieldList = new Array();	
	this.HTMLElementDefined = false;
	
	this.setDocType = function(){
		this.document.doctype = '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">'
	}
	
	this.toString = function(){		
		//Evaluate whether the HTML element should be outputed	
		var rootHasOneChild = (this.document.documentElement.childNodes.length == 1);
		var rootChildIsTag = rootHasOneChild && this.document.documentElement.lastChild.nodeType == 1;			
		if(rootChildIsTag){
			//Remove HTML element
			this.document.documentElement = this.document.documentElement.lastChild;
			this.lastElement = this.document.documentElement;			
		}
		
		//Evaluate whether the DOCType should be outputed
		var rootHasChild = (this.document.documentElement.lastChild != null)	
		if((this.HTMLElementDefined || rootChildIsTag) && rootHasChild){
			this.setDocType();
		}
		
		return this.document.xml;
	}
	
	/**
	 * Adds a yield value to the yieldList
	 * 
	 * @param {Object} value The value for the Yield
	 * @param {WaebricEnvironment} env The environment of the yield value
	 */
	this.addYield = function(value, env){
		this.yieldList.push(new Yield(value, env));
	}
	
	/**
	 * Returns the last Yield value and removes it from the list
	 * 
	 * @return {Object} The value of the last Yield value
	 */
	this.getLastYield = function(){		
		return this.yieldList.pop();
	}
}

/**
 * Yield class
 * 
 * @param {Object} value The value for the Yield
 * @param {WaebricEnvironment} env The environment of the yield value
 */
function Yield(value, env){	
	this.value = value;
	this.env = env;
}
