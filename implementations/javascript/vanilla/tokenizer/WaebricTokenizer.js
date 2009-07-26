/**
 * Waebric Tokenizer
 *
 * Reads the input stream and classifies the tokens based on regular
 * expressions or starting and ending characters.
 *
 * The tokenizer distinguishes the following tokens from each other:
 * - COMMENTS
 * - SYMBOLS
 * - TEXT
 * - NATURALS
 * - IDENTIFIERS
 *
 * Whitespaces are ignored and not tokenized.
 */
function WaebricTokenizer(){

    const emptyToken = '';
    
    this.tokens = new Array();
    
    /**
     * Tokenizes the input
     *
     * @param {String} The input
     */
    this.tokenizeAll = function(input){
        var currentChar = new WaebricCharacter(input, 0);
        
        do {
            currentChar = this.tokenize(currentChar);
        } while (currentChar.hasNextChar())
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
            return this.tokenizeUnrecognized(character);
        }
    }
    
    /**
     *
     * @param {Object} token
     */
    this.tokenizeUnrecognized = function(character){
        print('Unrecognized character found in the input stream!');
        return token.nextChar()
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
    this.tokenizeSinglelineComment = function(token){
        var lexeme = new WaebricToken.COMMENT(emptyToken);
        var currentToken = token.nextChar().nextChar(); //Ignore comment start
        while (currentToken != WaebricToken.COMMENT.SINGLELINE_ENDCHAR_1) {
            lexeme.addChar(currentToken);
            currentToken = currentToken.nextChar();
        }
        
        this.lexemes.push(lexeme);
        return currentToken;
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
    this.tokenizeMultilineComment = function(token){
        var lexeme = new WaebricToken.COMMENT(emptyToken);
        var currentToken = token.nextChar().nextChar(); //Ignore comment start
        while (currentToken != WaebricToken.COMMENT.MULTILINE_ENDCHAR_1 &&
        currentToken.nextChar() != WaebricToken.COMMENT.MULTILINE_ENDCHAR_2) {
            lexeme.addChar(currentToken);
            currentToken = currentToken.nextChar();
        }
        
        this.lexemes.push(lexeme);
        return currentToken.nextChar().nextChar(); //Ignore comment ending
    }
    
	/**
     * Checks whether the input character is a natural number
     *
     * @param {String} The input character
     * @return {Boolean} True if the input character is a natural number
     */
    this.isNatural = function(token){
        var regularExpression = new RegExp(WaebricToken.NATURAL.ALLOWEDCHARS);
        return token.match(regularExpression);
    }
    
	/**
	 * Tokenizes the Natural number
	 * 
	 * @param {String} The input character
	 * @return {String} The next character to be tokenized
	 */
    this.tokenizeNatural = function(token){
        var lexeme = new WaebricToken.NATURAL(emptyToken);
        var currentToken = token;
        
        while (this.isNatural(currentToken)) {
            lexeme.addChar(currentToken);
            currentToken = currentToken.nextChar();
        }
        
        this.lexemes.push(lexeme);
        return currentToken;
    }
    
	/**
     * Checks whether the input character is a sybmol
     *
     * @param {String} The input character
     * @return {Boolean} True if the input character is a symbol
     */
    this.isSymbol = function(token){
        return WaebricToken.SYMBOL.contains(token);
    }
    
	/**
	 * Tokenizes the Symbol
	 * 
	 * @param {String} The input character
	 * @return {String} The next character to be tokenized
	 */ 
    this.tokenizeSymbol = function(token){
        var lexeme = new WaebricToken.SYMBOL(token);
        var currentToken = token;
        
        //&& and || operator should be processed as one symbol
        if (token.equals('&')) {
            if (token.nextChar().equals('&')) {
                lexeme.addChar(currentToken.nextChar());
                currentToken = currentToken.nextChar();
            } else {
                return currentToken;
            }
        } else if (token.equals('|')) {
            if (token.nextChar().equals('|')) {
                lexeme.addChar(currentToken.nextChar());
                currentToken = currentToken.nextChar();
            } else {
                return currentToken;
            }
        }
        this.lexemes.push(lexeme);
        return currentToken.nextChar();
    }
    
	/**
     * Checks whether the input character is the start of quotes text
     *
     * @param {String} The input character
     * @return {Boolean} True if the input character is the start of a quoted text
     */
    this.isStartQuotedText = function(token){
        return WaebricToken.TEXT.QUOTEDTEXT_STARTCHAR.equals(token.value);
    }
    
	/**
	 * Tokenizes the Quoted Text
	 * 
	 * Quoted text can contain one other kind of token: Embeddings (see Waebric)
	 * 
	 * @param {String} The input character
	 * @return {String} The next character to be tokenized
	 */ 
    this.tokenizeQuotedText = function(token){
        var lexeme = new WaebricToken.TEXT(emptyToken);
        //Skips the opening quote
        var currentToken = token.nextChar();
        
        //Text between '<' and '>' (EMBEDDING) should not be processed as Text
        while (!WaebricToken.TEXT.QUOTEDTEXT_ENDCHAR.equals(currentToken.value) &&
        !WaebricToken.TEXT.EMBED_STARTCHAR.equals(currentToken.value)) {
            lexeme.addChar(currentToken);
            currentToken = currentToken.nextChar();
        }
        
        //Save processed lexeme
        this.lexemes.push(lexeme);
        
        
        if (WaebricToken.TEXT.EMBED_STARTCHAR.equals(currentToken.value)) {
            //If the current token (not processed at this moment) is the opening of an 
            //embedding, than this value should be tokenized as a SYMBOL.
            return currentToken;
        } else if (WaebricToken.TEXT.EMBED_ENDCHAR.equals(currentToken.nextChar().value)) {
            //If the token that follows the current token is the closing of an
            //embedding, than this value should be processed as SYMBOL.	(cannot be returned
            //since the next step should be considered as well).		
            this.tokenizeSymbol(currentToken.nextChar());
            //In addition, the token that follows the previous SYMBOL should be processed
            //as TEXT (even while it doesn't start with a quote). The, continue processing
            //remaining tokens
            return this.tokenizeQuotedText(currentToken.nextChar());
        } else {
            //Skip the ending quote and continue processing remaining tokens
            return currentToken.nextChar();
        }
    }
    
	/**
     * Checks whether the input character is allowed in an identifier
     *
     * @param {String} The input character
     * @return {Boolean} True if the input character is allowed in an identifier
     */
    this.isStartIdentifier = function(token){
        var regExp = new RegExp(WaebricToken.IDENTIFIER.ALLOWEDCHARS);
        return token.match(regExp);
    }
    
	/**
	 * Tokenizes the Identifier
	 * 
	 * @param {String} The input character
	 * @return {String} The next character to be tokenized
	 */ 
    this.tokenizeIdentifier = function(token){
        var value = "";
        var currentToken = token;
        
        while (this.isStartIdentifier(currentToken)) {
            value += currentToken.value;
            currentToken = currentToken.nextChar();
        }
        //Check if the value is a waebric reserved keyword or not
        if (WaebricToken.KEYWORD.contains(value)) {
            this.lexemes.push(new WaebricToken.KEYWORD(value));
            return currentToken;
        } else {
            this.lexemes.push(new WaebricToken.IDENTIFIER(value));
            return currentToken;
        }
    }
}
