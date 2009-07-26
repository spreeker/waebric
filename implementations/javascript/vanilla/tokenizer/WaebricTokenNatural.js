/**
 * Waebric Natural Token
 * 
 * A natural is recognized by the characters allowed in a natural
 * 
 * @param {String} The value of the token
 */
WaebricToken.NATURAL = function(value){
	this.value = value;
	this.type = 'NATURAL'
}

WaebricToken.NATURAL.prototype = new WaebricToken();

WaebricToken.NATURAL.ALLOWEDCHARS	= '[0-9]'