/**
 * Waebric Comment Token
 * 
 * A comment is recognized by it's start and ending character.
 * 
 * @param {String} The value of the token
 */
WaebricToken.COMMENT = function(value){
	this.value = value;
	this.type = 'COMMENT'
}

WaebricToken.COMMENT.prototype = new WaebricToken();

WaebricToken.COMMENT.SINGLELINE_STARTCHAR_1	= '/',
WaebricToken.COMMENT.SINGLELINE_STARTCHAR_2 = '/',
WaebricToken.COMMENT.SINGLELINE_ENDCHAR_1 	= '\n'

WaebricToken.COMMENT.MULTILINE_STARTCHAR_1 	= '/',
WaebricToken.COMMENT.MULTILINE_STARTCHAR_2 	= '*',
WaebricToken.COMMENT.MULTILINE_ENDCHARS 	= '*/',
WaebricToken.COMMENT.MULTILINE_ENDCHAR_1 	= '*',
WaebricToken.COMMENT.MULTILINE_ENDCHAR_2 	= '/'