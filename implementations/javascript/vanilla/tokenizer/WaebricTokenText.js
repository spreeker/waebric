/**
 * Waebric Text Token
 * 
 * Text is recognized by it's start and ending character (quotes).
 * 
 * @param {String} The value of the token
 */
WaebricToken.TEXT = function(value){
	this.value = value;
	this.type = 'TEXT'
}

WaebricToken.TEXT.prototype = new WaebricToken();

WaebricToken.TEXT.QUOTEDTEXT_STARTCHAR	= '"'
WaebricToken.TEXT.QUOTEDTEXT_ENDCHAR 	= '"'
WaebricToken.TEXT.EMBED_STARTCHAR		= '<'
WaebricToken.TEXT.EMBED_ENDCHAR			= '>'