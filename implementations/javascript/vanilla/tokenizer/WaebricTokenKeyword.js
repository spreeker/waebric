WaebricToken.KEYWORD = function(value){
	this.value = value;
	this.type = 'KEYWORD'
}

WaebricToken.KEYWORD.prototype = new WaebricToken();

WaebricToken.KEYWORD.keywords = [
	WaebricToken.KEYWORD.KEYWORD_HEADCHARS 	= '[A-Za-z]',
	WaebricToken.KEYWORD.KEYWORD_TAILCHARS 	= '[A-Za-z\-0-9]',
	
	WaebricToken.KEYWORD.SITE 				= 'SITE',
	WaebricToken.KEYWORD.IMPORT 			= 'IMPORT',
	WaebricToken.KEYWORD.MODULE 			= 'MODULE',
	WaebricToken.KEYWORD.END 				= 'END',
	WaebricToken.KEYWORD.DEF 				= 'DEF',
	WaebricToken.KEYWORD.IF 				= 'IF',
	WaebricToken.KEYWORD.ELSE 				= 'ELSE',
	WaebricToken.KEYWORD.LET 				= 'LET',
	WaebricToken.KEYWORD.EACH 				= 'EACH',
	WaebricToken.KEYWORD.ECHO 				= 'ECHO',
	WaebricToken.KEYWORD.CDATA 				= 'CDATA',
	WaebricToken.KEYWORD.COMMENT 			= 'COMMENT',
	WaebricToken.KEYWORD.YIELD 				= 'YIELD'
]

WaebricToken.KEYWORD.contains = function(input){	
	for(keyword in WaebricToken.KEYWORD.keywords){
		if(WaebricToken.KEYWORD.keywords[keyword] == input.toUpperCase() ){
			return true;
		}		
	}
	return false;
}

