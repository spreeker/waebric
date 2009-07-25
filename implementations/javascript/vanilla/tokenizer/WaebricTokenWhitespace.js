WaebricToken.WHITESPACE = function(){
	this.value = value;
	this.type = 'WHITESPACE'
}

WaebricToken.WHITESPACE.prototype = new WaebricToken();

WaebricToken.WHITESPACE.ALLOWEDCHARS	= '[\n\t\r ]'