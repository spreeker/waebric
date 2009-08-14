/**
 * Waebric Natural Token
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 * 
 * @param {String} The value of the token
 */
WaebricToken.NATURAL = function(value, position){
	this.value = value;
	this.type = 'NATURAL';
	this.position = position;
}

WaebricToken.NATURAL.prototype = new WaebricToken();

WaebricToken.NATURAL.ALLOWEDCHARS	= '[0-9]'