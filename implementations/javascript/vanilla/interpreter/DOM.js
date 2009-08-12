/**
 * Wrapper class for DOM Document
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 */
function DOM(){
	this.document;
	this.lastElement;
	this.lastValue;	
		
	this.yieldList = new Array();
	
	/**
	 * Creates the body of a strict XHTML document
	 * 
	 * The DOM library does not support the creation of namespaces or doctype declaration and
	 * are therefore programmed manually. 
	 */
	this.createXHTMLRoot = function(){
		this.document = document.implementation.createDocument().loadXML(
		'<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">' 
		+ '<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">');
				
		//Create document
		this.document = document.implementation.createDocument().loadXML('<html></html>');
		
		//Add doctype
		this.document.doctype = '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">'
				
		//Add XML namespace attribute to HTML tag
		this.document.documentElement.setAttribute('xmlns', 'http://www.w3.org/1999/xhtml');
		
		//Add XML language attribute to HTML tag
		this.document.documentElement.setAttributeNS('xml', 'lang', 'en');
		
		//Add language attribute to HTML tag
		this.document.documentElement.setAttribute('lang', 'en');

		this.lastElement = this.document.documentElement;
	}
	
	this.toString = function(){
		return '\n\n\n' + this.document.xml + '\n\n\n';
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
