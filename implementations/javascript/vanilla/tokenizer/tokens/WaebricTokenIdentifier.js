/**
 * Waebric Identifier Token
 * 
 * An identifier is recognized by the characters allowed in an identifier.
 * 
 * @param {String} The value of the token
 */
WaebricToken.IDENTIFIER = function(value){
	this.value = value;
	this.type = 'IDENTIFIER'
}

WaebricToken.IDENTIFIER.prototype = new WaebricToken();

WaebricToken.IDENTIFIER.ALLOWEDCHARS = "[^\"\f\n\r\t\v\u00A0\u2028\u2029 :/.(){};=<>&|?#$@%+,\\]\[]"

