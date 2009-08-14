/**
 * Waebric Symbol Token
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 * 
 * @param {String} The value of the token
 */
WaebricToken.SYMBOL = function(value, position){
	this.value = value;
	this.type = 'SYMBOL';
	this.position = position;
}

WaebricToken.SYMBOL.prototype = new WaebricToken();

WaebricToken.SYMBOL.symbols = [
	WaebricToken.SYMBOL.COLON 			= ':',
	WaebricToken.SYMBOL.SLASH 			= '/',
	WaebricToken.SYMBOL.DOT 			= '.',
	WaebricToken.SYMBOL.LEFTRBRACKET	= '(',
	WaebricToken.SYMBOL.RIGHTRBRACKET	= ')',
	WaebricToken.SYMBOL.LEFTCBRACKET	= '{',
	WaebricToken.SYMBOL.RIGHTCBRACKET	= '}',
	WaebricToken.SYMBOL.LEFTBBRACKET	= '[',
	WaebricToken.SYMBOL.RIGHTBBRACKET	= ']',
	WaebricToken.SYMBOL.SEMICOLON		= ';',
	WaebricToken.SYMBOL.EQ 				= '=',
	WaebricToken.SYMBOL.LESSTHAN 		= '<',
	WaebricToken.SYMBOL.GREATERTHAN		= '>',
	WaebricToken.SYMBOL.AND		 		= '&',
	WaebricToken.SYMBOL.DOUBLEAND		= '&&',
	WaebricToken.SYMBOL.OR		 		= '|',
	WaebricToken.SYMBOL.DOUBLEOR		= '||',
	WaebricToken.SYMBOL.QUESTION		= '?',
	WaebricToken.SYMBOL.CROSSHATCH		= '#',
	WaebricToken.SYMBOL.DOLLAR			= '$',
	WaebricToken.SYMBOL.AT	 			= '@',
	WaebricToken.SYMBOL.PERCENT			= '%',
	WaebricToken.SYMBOL.PLUS			= '+',
	WaebricToken.SYMBOL.COMMA			= ',',
	WaebricToken.SYMBOL.EXCLAMATION		= '!' 
]

/**
 * Returns whether the input matches one of the symbols
 * 
 * @param {String} input
 * @return True if the input matches on of the symbols
 */
WaebricToken.SYMBOL.contains = function(input){	
	for(symbol in WaebricToken.SYMBOL.symbols){
		if(WaebricToken.SYMBOL.symbols[symbol] == input ){
			return true;
		}		
	}
	return false;
}