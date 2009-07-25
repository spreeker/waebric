/**
 * Helper class for DOM actions
 */

function DOM(){
	this.document;
	this.lastElement;
	this.lastValue;
	
	this.yieldValue;
	this.yieldEnv;
	
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
}
