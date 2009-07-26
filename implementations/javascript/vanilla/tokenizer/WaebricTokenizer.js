/**
 * Waebric Tokenizer
 *
 * Reads the input stream and classifies the tokenList based on regular
 * expressions or starting and ending characters.
 *
 * The tokenizer distinguishes the following tokenList from each other:
 * - COMMENTS
 * - SYMBOLS
 * - TEXT
 * - NATURALS
 * - IDENTIFIERS
 *
 * Whitespaces are ignored and not tokenized.
 */
function WaebricTokenizer(){

    const EMPTY_TOKEN_VALUE = '';
    
    this.tokenList = new WaebricTokenizerResult(new Array());
    
    /**
     * Tokenizes the input
     *
     * @param {String} The input
     */
    this.tokenizeAll = function(input){
		try {
			
			var currentChar = new WaebricCharacter(input, 0);
			do {
				currentChar = this.tokenize(currentChar);
			} while (currentChar.hasNextChar())		
			
			return this.tokenList;	
			
		}catch(exception if (exception instanceof WaebricTokenizerException)){
			throw exception;
		}catch(exception){
			print(exception)
			throw new WaebricTokenizerException("Tokenization failed.")
		}
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
        }else if (this.isNatural(character)) {
            return this.tokenizeNatural(character);
        } else if (this.isSymbol(character)) {
            return this.tokenizeSymbol(character);
        } else if (this.isStartQuotedText(character)) {
            return this.tokenizeQuotedText(character);
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
        
        this.tokenList.addToken(token);
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
        
        while (currentChar != WaebricToken.COMMENT.MULTILINE_ENDCHAR_1 &&
        currentChar.nextChar() != WaebricToken.COMMENT.MULTILINE_ENDCHAR_2) {
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
	 * @param {String} The input character
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
        return currentChar.nextChar();
    }
    
	/**
     * Checks whether the input character is the start of quotes text
     *
     * @param {String} The input character
     * @return {Boolean} True if the input character is the start of a quoted text
     */
    this.isStartQuotedText = function(character){
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
    this.tokenizeQuotedText = function(character){
        var token = new WaebricToken.TEXT(EMPTY_TOKEN_VALUE);
        //Skips the opening quote
        var currentChar = character.nextChar();
        
        //Text between '<' and '>' (EMBEDDING) should not be processed as Text
        while (!WaebricToken.TEXT.QUOTEDTEXT_ENDCHAR.equals(currentChar.value) &&
        !WaebricToken.TEXT.EMBED_STARTCHAR.equals(currentChar.value)) {
            token.addChar(currentChar);
            currentChar = currentChar.nextChar();
        }
        
        //Save processed token
        this.tokenList.addToken(token);
        
        
        if (WaebricToken.TEXT.EMBED_STARTCHAR.equals(currentChar.value)) {
            //If the current character (not processed at this moment) is the opening of an 
            //embedding, than this value should be tokenized as a SYMBOL.
            return currentChar;
        } else if (WaebricToken.TEXT.EMBED_ENDCHAR.equals(currentChar.nextChar().value)) {
            //If the character that follows the current character is the closing of an
            //embedding, than this value should be processed as SYMBOL.	(cannot be returned
            //since the next step should be considered as well).		
            this.tokenizeSymbol(currentChar.nextChar());
            //In addition, the token that follows the previous SYMBOL should be processed
            //as TEXT (even while it doesn't start with a quote). The, continue processing
            //remaining tokenList
            return this.tokenizeQuotedText(currentChar.nextChar());
        } else {
            //Skip the ending quote and continue processing remaining tokenList
            return currentChar.nextChar();
        }
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
        if (WaebricToken.KEYWORD.contains(value)) {
            this.tokenList.addToken(new WaebricToken.KEYWORD(value));
            return currentChar;
        } else {
            this.tokenList.addToken(new WaebricToken.IDENTIFIER(value));
            return currentChar;
        }
    }
}

WaebricTokenizer.tokenizeAll = function(input){
	var tokenizer = new WaebricTokenizer();
	return tokenizer.tokenizeAll(input);
}
