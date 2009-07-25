WaebricToken.IDENTIFIER = function(value){
	this.value = value;
	this.type = 'IDENTIFIER'
}

WaebricToken.IDENTIFIER.prototype = new WaebricToken();

WaebricToken.IDENTIFIER.ALLOWEDCHARS = "[^\f\n\r\t\v\u00A0\u2028\u2029 :/.(){};=<>&|?#$@%+,\\]\[]"

