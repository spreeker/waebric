WaebricToken.SYMBOL = function(value){
	this.value = value;
	this.type = 'SYMBOL'
}

WaebricToken.SYMBOL.prototype = new WaebricToken();

WaebricToken.SYMBOL.symbols = [
	WaebricToken.SYMBOL.COLON 			= ':',
	WaebricToken.SYMBOL.SLASH 			= '/',
	WaebricToken.SYMBOL.DOT 			= '.',
	WaebricToken.SYMBOL.LEFTRBRACKET	= '(',
	WaebricToken.SYMBOL.RIGHRRBRACKET	= ')',
	WaebricToken.SYMBOL.LEFTCBRACKET	= '{',
	WaebricToken.SYMBOL.RIGHTCBRACE		= '}',
	WaebricToken.SYMBOL.LEFTBBRACKET	= '[',
	WaebricToken.SYMBOL.RIGHTBBRACKET	= ']',
	WaebricToken.SYMBOL.SEMICOLON		= ';',
	WaebricToken.SYMBOL.EQ 				= '=',
	WaebricToken.SYMBOL.LESSTHAN 		= '<',
	WaebricToken.SYMBOL.GREATERTHAN		= '>',
	WaebricToken.SYMBOL.AND		 		= '&&',
	WaebricToken.SYMBOL.OR	 			= '||',
	WaebricToken.SYMBOL.QUESTION		= '?',
	WaebricToken.SYMBOL.CROSSHATCH		= '#',
	WaebricToken.SYMBOL.DOLLAR			= '$',
	WaebricToken.SYMBOL.AT	 			= '@',
	WaebricToken.SYMBOL.PERCENT			= '%',
	WaebricToken.SYMBOL.PLUS			= '+',
	WaebricToken.SYMBOL.COMMA			= ','
]


WaebricToken.SYMBOL.contains = function(input){	
	for(symbol in WaebricToken.SYMBOL.symbols){
		if(WaebricToken.SYMBOL.symbols[symbol] == input ){
			return true;
		}		
	}
	return false;
}