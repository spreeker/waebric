WaebricToken.NATURAL = function(value){
	this.value = value;
	this.type = 'NATURAL'
}

WaebricToken.NATURAL.prototype = new WaebricToken();

WaebricToken.NATURAL.ALLOWEDCHARS	= '[0-9]'