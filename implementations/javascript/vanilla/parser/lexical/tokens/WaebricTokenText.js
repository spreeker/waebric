/**
 * Waebric Text Token
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 * 
 * @param {String} The value of the token
 */
WaebricToken.TEXT = function(value, position){
	this.value = value;
	this.type = 'TEXT';
	this.position = position;
}

WaebricToken.TEXT.prototype = new WaebricToken();

WaebricToken.TEXT.QUOTEDTEXT_STARTCHAR			= '"'
WaebricToken.TEXT.QUOTEDTEXT_ENDCHAR 			= '"'
WaebricToken.TEXT.EMBED_STARTCHAR				= '<'
WaebricToken.TEXT.EMBED_ENDCHAR					= '>'
WaebricToken.TEXT.SINGLEQUOTEDTEXT_STARTCHAR	= "'"
WaebricToken.TEXT.SINGLEQUOTEDTEXT_ALLOWEDCHARS = "^[^\x00-\x1F\ \t\n\r;,>\)]*$"	

WaebricToken.TEXT.isSingleQuotedText = function(value){
	var regExp = new RegExp(WaebricToken.TEXT.SINGLEQUOTEDTEXT_ALLOWEDCHARS);
	return value.match(regExp);
}
