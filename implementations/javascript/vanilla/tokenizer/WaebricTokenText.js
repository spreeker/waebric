WaebricToken.TEXT = function(value){
	this.value = value;
	this.type = 'TEXT'
}

WaebricToken.TEXT.prototype = new WaebricToken();

WaebricToken.TEXT.QUOTEDTEXT_STARTCHAR	= '"'
WaebricToken.TEXT.QUOTEDTEXT_ENDCHAR 	= '"'
WaebricToken.TEXT.EMBED_STARTCHAR		= '<'
WaebricToken.TEXT.EMBED_ENDCHAR			= '>'