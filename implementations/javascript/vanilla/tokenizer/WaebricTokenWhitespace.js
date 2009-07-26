/**
 * Waebric Whitespace Token
 * 
 * A whitespace is recognized by the characters allowed as whitespace
 * 
 * @param {String} The value of the token
 */
WaebricToken.WHITESPACE = function(){
	this.value = value;
	this.type = 'WHITESPACE'
}

WaebricToken.WHITESPACE.prototype = new WaebricToken();

WaebricToken.WHITESPACE.ALLOWEDCHARS	= '[\n\t\r ]'
WabericToken.WHITESPACE.SPACE 			= ' '
