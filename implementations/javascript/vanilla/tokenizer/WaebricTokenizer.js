/**
 * Waebric Tokenizer
 *
 * Reads the input stream and classifies the tokenList based on regular
 * expressions or starting and ending characters.
 *
 * The tokenizer categorize the following tokens:
 * - COMMENTS
 * - SYMBOLS
 * - TEXT
 * - NATURALS
 * - IDENTIFIERS
 * - KEYWORDS (1)
 * 
 * (1) Reserved keywords that appear in the PATH of a site mapping are 
 * categorized as IDENTIFIERS.
 * 
 * Whitespaces are ignored and not tokenized 
 * 
 */
function WaebricTokenizer(){
	
    this.tokenList = new WaebricTokenizerResult(new Array());
    const EMPTY_TOKEN_VALUE = '';
	
    /**
     * Tokenizes the input
     *
     * @param {String} The input
     */
    this.tokenizeAll = function(input){
		//try {			
			var currentChar = new WaebricCharacter(input, 0);
			do {
				currentChar = this.tokenize(currentChar);
			} while (currentChar.hasNextChar())		
			
			return this.tokenList;	
			
		//}catch(exception if (exception instanceof WaebricTokenizerException)){
		//	throw exception;
		//}catch(exception){
		//	print(exception)
		//	throw new WaebricTokenizerException("Tokenization failed.")
		//}
    }
    
    /**
     * Tokenizes a token
     *
     * @param {Object} token
     */
    this.tokenize = function(character){
        if (this.isWhitespace(character)) {
            return this.tokenizeWhitespaces(character);
        } else if (this.isStartSinglelineComment(character)) {
            return this.tokenizeSinglelineComment(character);
        } else if (this.isStartMultilineComment(character)) {
            return this.tokenizeMultilineComment(character);
        } else if (this.isNatural(character)) {
            return this.tokenizeNatural(character);
        } else if (this.isSymbol(character)) {
            return this.tokenizeSymbol(character);
        } else if (this.isStartSingleQuotedText(character)) {
            return this.tokenizeSingleQuotedText(character);
        } else if (this.isStartDoubleQuotedText(character)) {
            return this.tokenizeDoubleQuotedText(character);
        } else if (this.isStartIdentifier(character)) {
            return this.tokenizeIdentifier(character);
        } else {
            throw new WaebricTokenizerException("Unrecognized character in input stream.")
        }
    }
    
    /**
     * Checks whether the input character is whitespace
     *
     * @param {String} The input character
     * @return {Boolean} True is the input character is the start of a whitespace
     */
    this.isWhitespace = function(character){
        var regularExpression = new RegExp(WaebricToken.WHITESPACE.ALLOWEDCHARS);
        return character.match(regularExpression);
    }
    
    /**
     * Tokenizes the whitespace (whitespace is ignored)
     *
     * @param {String} The input character
     * @return {String} The next character to be tokenized
     */
    this.tokenizeWhitespaces = function(character){
        return character.nextChar();
    }    
  
    /**
     * Checks whether the input character is the start of singleline comment
     *
     * @param {String} The input character
     * @return {Boolean} True if the input character is the start of singleline comment
     */
    this.isStartSinglelineComment = function(character){
        return character.equals(WaebricToken.COMMENT.SINGLELINE_STARTCHAR_1) &&
        character.nextChar().equals(WaebricToken.COMMENT.SINGLELINE_STARTCHAR_2);
    }
	
	/**
	 * Tokenizes the Singleline comment
	 * 
	 * @param {String} The character
	 * @return {String} The next character to be tokenized
	 */
    this.tokenizeSinglelineComment = function(character){
        var token = new WaebricToken.COMMENT(EMPTY_TOKEN_VALUE);
        var currentChar = character.nextChar().nextChar(); //Ignore comment start
        while (currentChar != WaebricToken.COMMENT.SINGLELINE_ENDCHAR_1) {
            token.addChar(currentChar);
            currentChar = currentChar.nextChar();
        }
        
        //this.tokenList.addToken(token);
        return currentChar;
    }
    
    /**
     * Checks whether the input character is the start of multiline comment
     *
     * @param {String} The input character
     * @return {Boolean} True if the input character is the start of multiline comment
     */
    this.isStartMultilineComment = function(character){
        return character.equals(WaebricToken.COMMENT.MULTILINE_STARTCHAR_1) &&
        character.nextChar().equals(WaebricToken.COMMENT.MULTILINE_STARTCHAR_2);
    }    
	
	/**
	 * Tokenizes the Multiline comment
	 * 
	 * @param {String} The input character
	 * @return {String} The next character to be tokenized
	 */    
    this.tokenizeMultilineComment = function(character){
        var token = new WaebricToken.COMMENT(EMPTY_TOKEN_VALUE);
        var currentChar = character.nextChar().nextChar(); //Ignore comment start
	
		//Process comment until ending of multiline comment is found
        while ((currentChar.value + currentChar.nextChar().value) 
				!= WaebricToken.COMMENT.MULTILINE_ENDCHARS) {
            token.addChar(currentChar);
            currentChar = currentChar.nextChar();
        }	
        
        this.tokenList.addToken(token);
        return currentChar.nextChar().nextChar(); //Ignore comment ending
    }
    
	/**
     * Checks whether the input character is a natural number
     *
     * @param {String} The input character
     * @return {Boolean} True if the input character is a natural number
     */
    this.isNatural = function(character){
        var regularExpression = new RegExp(WaebricToken.NATURAL.ALLOWEDCHARS);
        return character.match(regularExpression);
    }
    
	/**
	 * Tokenizes the Natural number
	 * 
	 * @param {String} The input character
	 * @return {String} The next character to be tokenized
	 */
    this.tokenizeNatural = function(character){
        var token = new WaebricToken.NATURAL(EMPTY_TOKEN_VALUE);
        var currentChar = character;
        
        while (this.isNatural(currentChar)) {
            token.addChar(currentChar);
            currentChar = currentChar.nextChar();
        }
        
        this.tokenList.addToken(token);
        return currentChar;
    }
    
	/**
     * Checks whether the input character is a sybmol
     *
     * @param {String} The input character
     * @return {Boolean} True if the input character is a symbol
     */
    this.isSymbol = function(character){
        return WaebricToken.SYMBOL.contains(character);
    }
    
	/**
	 * Tokenizes the Symbol
	 * 
	 * @param {String} character The input character
	 * @return {String} The next character to be tokenized
	 */ 
    this.tokenizeSymbol = function(character){
        var token = new WaebricToken.SYMBOL(character.value);		
        var currentChar = character;
		
        //&& and || operator should be processed as one symbol
        if (currentChar.equals('&')) {
            if (currentChar.nextChar().equals('&')) {
                token.addChar(currentChar.nextChar());
                currentChar = currentChar.nextChar();
            } else {
                return currentChar;
            }
        } else if (currentChar.equals('|')) {
            if (currentChar.nextChar().equals('|')) {
                token.addChar(currentChar.nextChar());
                currentChar = currentChar.nextChar();
            } else {
                return currentChar;
            }
        }
        this.tokenList.addToken(token);
		
		if (currentChar == WaebricToken.SYMBOL.GREATERTHAN) {
			if(currentChar.nextChar() == '"'){
				return currentChar.nextChar().nextChar(); //Skip current character + ending quote
			}
			return this.tokenizeDoubleQuotedText(currentChar);
		} else {
			return currentChar.nextChar();
		}
    }	
	
	this.isStartSingleQuotedText = function(character){
        return WaebricToken.TEXT.SINGLEQUOTEDTEXT_STARTCHAR.equals(character.value);
    }

	/**
	 * Tokenizes the Quoted Text
	 * 
	 * Quoted text can contain one other kind of token: Embeddings (see Waebric)
	 * 
	 * @param {String} character The input character
	 * @return {String} The next character to be tokenized
	 */
	this.tokenizeSingleQuotedText = function(character){
        var token = new WaebricToken.TEXT(EMPTY_TOKEN_VALUE);        
        var currentChar = character.nextChar(); //Skip the opening quote
        
		//Process single quoted text until an illegal character is found
		while(WaebricToken.TEXT.isSingleQuotedText(currentChar.value)){				
			token.addChar(currentChar);
			currentChar = currentChar.nextChar();	
		}	
		return this.tokenizeText(token, currentChar);
    }
    
	/**
     * Checks whether the input character is the start of quotes text
     *
     * @param {String} The input character
     * @return {Boolean} True if the input character is the start of a quoted text
     */
    this.isStartDoubleQuotedText = function(character){
        return WaebricToken.TEXT.QUOTEDTEXT_STARTCHAR.equals(character.value);
    }
	
	/**
	 * Tokenizes the Quoted Text
	 * 
	 * Quoted text can contain one other kind of token: Embeddings (see Waebric)
	 * 
	 * @param {String} The input character
	 * @return {String} The next character to be tokenized
	 */
    this.tokenizeDoubleQuotedText = function(character){
        var token = new WaebricToken.TEXT(EMPTY_TOKEN_VALUE);        
        var currentChar = character.nextChar(); //Skip the opening quote
			
        //Process double quoted text until ending quote is found (or an embedding).
        while (!this.isEndQuoteText(currentChar) && !this.isStartEmbedding(currentChar) 
		&& !this.isEndEmbedding(currentChar)) {			
            token.addChar(currentChar);
            currentChar = currentChar.nextChar();	
        }		
        currentChar = currentChar.nextChar(); //Skip the ending quote
        return this.tokenizeText(token, currentChar);
    }
	
	this.isEndQuoteText = function(character){
		var currentChar = character;
		var previousChar = character.previousChar();

		return WaebricToken.TEXT.QUOTEDTEXT_ENDCHAR.equals(currentChar.value) && !"\\".equals(previousChar.value);
	}
	
	this.isStartEmbedding = function(character){
		var currentChar = character;
		var previousChar = character.previousChar();
		
		return WaebricToken.TEXT.EMBED_STARTCHAR.equals(currentChar.value) && !"\\".equals(previousChar.value);
	}
	
	this.isEndEmbedding = function(character){
		var currentChar = character;
		var previousChar = character.previousChar();
		
		return WaebricToken.TEXT.EMBED_ENDCHAR.equals(currentChar.value) && !"\\".equals(previousChar.value);
	}
	
	/**
	 * 
	 * @param {String} The input character
	 * @return {String} The next character to be tokenized
	 */
	this.tokenizeText = function(token, currentChar){
		//Save processed token
        this.tokenList.addToken(token);  

        if (WaebricToken.TEXT.EMBED_STARTCHAR.equals(currentChar.previousChar().value)) {
			this.tokenizeSymbol(currentChar.previousChar());
        }
		
		return currentChar;
	}
    
	/**
     * Checks whether the input character is allowed in an identifier
     *
     * @param {String} The input character
     * @return {Boolean} True if the input character is allowed in an identifier
     */
    this.isStartIdentifier = function(character){
        var regExp = new RegExp(WaebricToken.IDENTIFIER.ALLOWEDCHARS);
        return character.match(regExp);
    }
    
	/**
	 * Tokenizes the Identifier
	 * 
	 * @param {String} The input character
	 * @return {String} The next character to be tokenized
	 */ 
    this.tokenizeIdentifier = function(character){
        var value = "";
        var currentChar = character;
        
        while (this.isStartIdentifier(currentChar)) {
            value += currentChar.value;
            currentChar = currentChar.nextChar();
        }
		
        //Check if the value is a waebric reserved keyword or not
		if (WaebricToken.KEYWORD.contains(value) && !this.isPathElement(value)) {
			this.tokenList.addToken(new WaebricToken.KEYWORD(value));
			return currentChar;
		} else {
			this.tokenList.addToken(new WaebricToken.IDENTIFIER(value));
			return currentChar;
		}
    }
	
	/**
	 * Determines whether the input token is a path element.
	 * 
	 * This function is required to prevent the tokenizer to categorize reserved keywords
	 * as such in the case they are used in a path element.
	 * 
	 * If the input token is a path element, then the token should be processed as an IDENTIFIER.
	 * 
	 * @param {String} The value of the token
	 * @return {Boolean} True if value is part of a Path element in a Site Mapping
	 */
	this.isPathElement = function(value){
		if(WaebricToken.KEYWORD.END.equals(value)){			
			return false; 
		}else if(WaebricToken.KEYWORD.SITE.equals(this.tokenList.getLastKeyword().value)){
			var dotFound = this.hasDotInSiteMapping();
			var colonFound = this.hasColonInSiteMapping();
			//If a DOT and a COLON is found inside a single SITE MAPPING, 
			// then the input value is markup
			if(dotFound && colonFound){
				return false; //Markup
			}		
			return true; //Path element
		}
		return false; //Not in SITE definition
	}
	
	/**
	 * Returns whether a DOT appears in the current processed site mapping	  
	 * 
	 * @return {Boolean} True if a dot appears in the site mapping
	 */
	this.hasDotInSiteMapping = function(){
		var currentTokenIndex = this.tokenList.tokens.length - 1.
		var currentToken = this.tokenList.tokens[currentTokenIndex];
		while(!WaebricToken.KEYWORD.SITE.equals(currentToken.value) 
				&& !WaebricToken.KEYWORD.END.equals(currentToken.value)
				&& currentToken.value != WaebricToken.SYMBOL.SEMICOLON 
				){
			if(currentToken.value == WaebricToken.SYMBOL.DOT){	
				return true;
			}
			currentToken = this.tokenList.tokens[--currentTokenIndex];	
		}
		return false;
	}
	
	/**
	 * Returns whether a COLON appears in the current processed site mapping
	 * 
	 * @return {Boolean} True if a colon appears in the site mapping
	 */
	this.hasColonInSiteMapping = function(){
		var currentTokenIndex = this.tokenList.tokens.length - 1.
		var currentToken = this.tokenList.tokens[currentTokenIndex];
		while(!WaebricToken.KEYWORD.SITE.equals(currentToken.value) 
				&& !WaebricToken.KEYWORD.END.equals(currentToken.value)
				&& currentToken.value != WaebricToken.SYMBOL.DOT){
			if(currentToken.value == WaebricToken.SYMBOL.COLON){				
				return true;
			}
			currentToken = this.tokenList.tokens[currentTokenIndex--];
		}
		return false;
	}
}

WaebricTokenizer.tokenizeAll = function(input){
	var tokenizer = new WaebricTokenizer();
	return tokenizer.tokenizeAll(input);
}
