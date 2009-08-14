/**
 * Waebric Identifier Token
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 * 
 * @param {String} The value of the token
 */
WaebricToken.IDENTIFIER = function(value, position){
	this.value = value;
	this.type = 'IDENTIFIER';	
	this.position = position;
}

WaebricToken.IDENTIFIER.prototype = new WaebricToken();

WaebricToken.IDENTIFIER.ALLOWEDCHARS = "[^\"\n\r\t\u00A0\u2028\u2029 :/.(){};=<>&|?#$@%+,\\]\[\!]"

