function WaebricTokenizer(){
	
	const emptyToken = '';
	const space = ' ';
	
	this.lexemes = new Array();
	
	this.tokenizeAll = function(input){
		var currentToken = new WaebricCharacter(input, 0);
		
		do{
			currentToken = this.tokenize(currentToken);
		}while(currentToken.hasNextChar())	
	}
	
	this.tokenize = function(token){
		if(this.isWhitespace(token)){
			return this.tokenizeWhitespaces(token);
		}else if(this.isStartComment(token)){
			return this.tokenizeComments(token);
		}else if (this.isStartNatural(token)) {
			return this.tokenizeNatural(token);
		}else if(this.isStartSymbol(token)){
			return this.tokenizeSymbol(token);
		}else if(this.isStartQuotedText(token)){
			return this.tokenizeQuotedText(token);
		}else if(this.isStartIdentifier(token)){
			return this.tokenizeIdentifier(token);
		}else {
			print(token + ' not found. Previous token is : ' + token.previousChar())
			return this.tokenizeUnrecognized(token);
		}
	}
	
	this.tokenizeUnrecognized = function(token){
		var lexeme = new WaebricToken(token);
		this.lexemes.push(lexeme);
		return token.nextChar()
	}
	
	this.isWhitespace = function(token){
		var regularExpression = new RegExp( WaebricToken.WHITESPACE.ALLOWEDCHARS );
		return token.match(regularExpression);
	}
	 
	this.tokenizeWhitespaces = function(token){
		return token.nextChar();
	}
	
	this.isStartComment = function(token){	
		return this.isStartSinglelineComment( token ) || this.isStartMultilineComment( token );
	}
	
	this.isStartSinglelineComment = function(token){
		return token.equals(WaebricToken.COMMENT.SINGLELINE_STARTCHAR_1) 
				&& token.nextChar().equals(WaebricToken.COMMENT.SINGLELINE_STARTCHAR_2);
	}
	
	this.isStartMultilineComment = function(token){
		return token.equals(WaebricToken.COMMENT.MULTILINE_STARTCHAR_1) 
				&& token.nextChar().equals(WaebricToken.COMMENT.MULTILINE_STARTCHAR_2);
	}
	
	this.tokenizeComments = function(token){
		if(this.isStartSinglelineComment(token)){
			return this.tokenizeSinglelineComment(token);
		}else if(this.isStartMultilineComment(token)){
			return this.tokenizeMultilineComment(token);
		}
	}
	
	this.tokenizeSinglelineComment = function(token){
		var lexeme = new WaebricToken.COMMENT(emptyToken);
		var currentToken = token.nextChar().nextChar(); //Ignore comment start
		
		while(currentToken != WaebricToken.COMMENT.SINGLELINE_ENDCHAR_1){
			lexeme.addToken(currentToken);
			currentToken = currentToken.nextChar();
		}
		
		this.lexemes.push(lexeme);	
		return currentToken;
	}
	
	this.tokenizeMultilineComment = function(token){
		var lexeme = new WaebricToken.COMMENT(emptyToken);
		var currentToken = token.nextChar().nextChar(); //Ignore comment start
		
		while(currentToken != WaebricToken.COMMENT.MULTILINE_ENDCHAR_1 
				&& currentToken.nextChar() != WaebricToken.COMMENT.MULTILINE_ENDCHAR_2){
			lexeme.addToken(currentToken);
			currentToken = currentToken.nextChar();
		}
		
		this.lexemes.push(lexeme);	
		return currentToken.nextChar().nextChar(); //Ignore comment ending
	}
	
	this.isStartNatural = function(token){
		var regularExpression = new RegExp( WaebricToken.NATURAL.ALLOWEDCHARS );
		return token.match(regularExpression);
	}
	
	this.tokenizeNatural = function(token){
		var lexeme = new WaebricToken.NATURAL(emptyToken);
		var currentToken = token;
		
		while(this.isStartNatural(currentToken)){
			lexeme.addToken(currentToken);
			currentToken = currentToken.nextChar();
		}
		
		this.lexemes.push(lexeme);
		return currentToken;
	}

	this.isStartSymbol = function(token){		
		return WaebricToken.SYMBOL.contains(token);
	}
	
	this.tokenizeSymbol = function(token){
		var lexeme = new WaebricToken.SYMBOL(token);	
		var currentToken = token;
		
		//&& and || operator should be processed as one symbol
		if(token.equals('&')){
			if (token.nextChar().equals('&')) {
				lexeme.addToken(currentToken.nextChar());	
				currentToken = currentToken.nextChar();
			}else{
				return currentToken;
			}
		}else if(token.equals('|')){
			if (token.nextChar().equals('|')) {
				lexeme.addToken(currentToken.nextChar());
				currentToken = currentToken.nextChar();
			}else{
				return currentToken;
			}
		}				
		this.lexemes.push(lexeme);
		return currentToken.nextChar();
	}
	
	this.isStartQuotedText = function(token){
		return WaebricToken.TEXT.QUOTEDTEXT_STARTCHAR.equals(token.value);
	}
	
	this.tokenizeQuotedText = function(token){
		var lexeme = new WaebricToken.TEXT(emptyToken);
		//Skips the opening quote
		var currentToken = token.nextChar();

		//Text between '<' and '>' (EMBEDDING) should not be processed as Text
		while(!WaebricToken.TEXT.QUOTEDTEXT_ENDCHAR.equals(currentToken.value) 
				&& !WaebricToken.TEXT.EMBED_STARTCHAR.equals(currentToken.value)){			
				lexeme.addToken(currentToken);
				currentToken = currentToken.nextChar();			
		}
		
		//Save processed lexeme
		this.lexemes.push(lexeme);
		
		
		if (WaebricToken.TEXT.EMBED_STARTCHAR.equals(currentToken.value)) {
			//If the current token (not processed at this moment) is the opening of an 
			//embedding, than this value should be tokenized as a SYMBOL.
			return currentToken;
		}else if(WaebricToken.TEXT.EMBED_ENDCHAR.equals(currentToken.nextChar().value)){
			//If the token that follows the current token is the closing of an
			//embedding, than this value should be processed as SYMBOL.	(cannot be returned
			//since the next step should be considered as well).		
			this.tokenizeSymbol(currentToken.nextChar());
			//In addition, the token that follows the previous SYMBOL should be processed
			//as TEXT (even while it doesn't start with a quote). The, continue processing
			//remaining tokens
			return this.tokenizeQuotedText(currentToken.nextChar());
		}else {
			//Skip the ending quote and continue processing remaining tokens
			return currentToken.nextChar();
		}
	}
	
	this.isStartIdentifier = function(token){
		var regExp = new RegExp( WaebricToken.IDENTIFIER.ALLOWEDCHARS );
		return token.match(regExp);
	}
	
	this.tokenizeIdentifier = function(token){
		var value = "";
		var currentToken = token;

		while(this.isStartIdentifier(currentToken)){
			value += currentToken.value;
			currentToken = currentToken.nextChar();
		}
		//Check if the value is a waebric reserved keyword or not
		if (WaebricToken.KEYWORD.contains(value)) {
			this.lexemes.push(new WaebricToken.KEYWORD(value));
			return currentToken;
		}else{
			this.lexemes.push(new WaebricToken.IDENTIFIER(value));
			return currentToken;
		}
	}
}
