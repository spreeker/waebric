/**
 * Waebric Tokenizer
 *
 * Reads the String representation of a Waebric program and classifies 
 * the characters based on regular expressions or starting and ending characters.
 *
 * The tokenizer categorize the following tokens:
 * - SYMBOLS
 * - TEXT
 * - NATURALS
 * - IDENTIFIERS
 * - KEYWORDS
 *
 * Reserved keywords that appear in the PATH of a site mapping are
 * categorized as IDENTIFIERS. Whitespaces and comments are ignored 
 * and not tokenized.
 *
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 */
function WaebricTokenizer(){

    var tokenList = new WaebricTokenizerResult(new Array());
	var position = new WaebricCharacter.Position(1,1) ;
	
    /**
     * Tokenizes the input String
     * 
     * @param {String} input String representation of a Waebric program
     * @return {WaebricTokenizerResult}
     */
    this.tokenize = function(input){
        var currentChar = new WaebricCharacter(input, 0);
        do {			
            currentChar = tokenizeCharacter(currentChar);
        } while (currentChar != null && currentChar.hasNextChar())
        return tokenList;
    }
    
    /**
     * Tokenizes a sequence of characters to a token
     *
     * @param {WaebricCharacter} character The character that needs to be tokenized
     * @return {WaebricCharacter} The next character to be tokenized
     */
    function tokenizeCharacter(character){
        if (isWhitespace(character)) {
            return tokenizeWhitespaces(character);
        } else if (isStartSinglelineComment(character)) {
            return tokenizeSinglelineComment(character);
        } else if (isStartMultilineComment(character)) {
            return tokenizeMultilineComment(character);
        } else if (isStartNatural(character)) {
            return tokenizeNatural(character);
        } else if (isSymbol(character)) {
            return tokenizeSymbol(character);
        } else if (isStartSingleQuotedText(character)) {
            return tokenizeSingleQuotedText(character);
        } else if (isStartDoubleQuotedText(character)) {
            return tokenizeDoubleQuotedText(character);
        } else if (isStartIdentifier(character)) {
            return tokenizeIdentifier(character);
        } else {
            throw new WaebricLexicalException("Unrecognized character in input stream.", position);
        }
    }
    
    /**
     * Checks whether the input character is whitespace
     *
     * @param {WaebricCharacter} character The character that needs to be tokenized
     * @return {Boolean}
     */
    function isWhitespace(character){
        var regularExpression = new RegExp(WaebricToken.WHITESPACE.ALLOWEDCHARS);
        return character.match(regularExpression);
    }
    
    /**
     * Tokenizes whitespace (ignored)
     *
     * @param {WaebricCharacter} character The character that needs to be tokenized
     * @return {WaebricCharacter} The next character to be tokenized
     */
    function tokenizeWhitespaces(character){
		position.update(character);
        return character.nextChar();
    }
    
    /**
     * Checks whether the input character is the start of singleline comment
     *
     * @param {WaebricCharacter} character The input character
     * @return {Boolean}
     */
    function isStartSinglelineComment(character){
        return character.equals(WaebricToken.COMMENT.SINGLELINE_STARTCHAR_1) &&
        character.nextChar().equals(WaebricToken.COMMENT.SINGLELINE_STARTCHAR_2);
    }
    
    /**
     * Tokenizes Singleline comment (ignored)
     *
     * @param {WaebricCharacter} character The character that needs to be tokenized
     * @return {WaebricCharacter} The next character to be tokenized
     */
    function tokenizeSinglelineComment(character){
        var currentChar = character.nextChar().nextChar(); //Ignore comment start
        position.increaseColumn(2);
        while (currentChar != WaebricToken.COMMENT.SINGLELINE_ENDCHAR_1) {
            position.update(currentChar)
            currentChar = currentChar.nextChar();
        }
        return currentChar;
    }
    
    /**
     * Checks whether the input character is the start of multiline comment
     *
     * @param {WaebricCharacter} character The character that needs to be tokenized
     * @return {Boolean}
     */
    function isStartMultilineComment(character){
        return character.equals(WaebricToken.COMMENT.MULTILINE_STARTCHAR_1) &&
        character.nextChar().equals(WaebricToken.COMMENT.MULTILINE_STARTCHAR_2);
    }
    
    /**
     * Tokenizes Multiline comment (ignored)
     *
     * @param {WaebricCharacter} character The character that needs to be tokenized
     * @return {WaebricCharacter} The next character to be tokenized
     */
    function tokenizeMultilineComment(character){
        var currentChar = character.nextChar().nextChar(); //Ignore comment start
        position.increaseColumn(2);
        while ((currentChar.value + currentChar.nextChar().value) !=
        WaebricToken.COMMENT.MULTILINE_ENDCHARS) {
            position.update(currentChar)
            currentChar = currentChar.nextChar();
        }
        return currentChar.nextChar().nextChar(); //Ignore comment ending
    }
    
    /**
     * Checks whether the input character is the start of a number
     *
     * @param {WaebricCharacter} character The input character
     * @return {Boolean}
     */
    function isStartNatural (character){
        var regularExpression = new RegExp(WaebricToken.NATURAL.ALLOWEDCHARS);
        return character.match(regularExpression);
    }
    
    /**
     * Tokenizes a Number
     *
     * @param {WaebricCharacter} character The character that needs to be tokenized
     * @return {WaebricCharacter} The next character to be tokenized
     */
    function tokenizeNatural(character){
        var token = new WaebricToken.NATURAL(WaebricToken.EMPTY_TOKEN_VALUE, position.clone());
        var currentChar = character;
        
        while (isStartNatural(currentChar)) {
			position.increaseColumn(1);		
            token.addChar(currentChar);
            currentChar = currentChar.nextChar();
        }
        
        if (!isWhitespace(currentChar) && !isSymbol(currentChar)) {
            throw new WaebricLexicalException("Illegal character found after natural.", position);
        }
        
        tokenList.addToken(token);
        return currentChar;
    }
    
    /**
     * Checks whether the input character is a sybmol
     *
     * @param {WaebricCharacter} character The input character
     * @return {Boolean} True if the input character is a symbol
     */
    function isSymbol(character){
        return WaebricToken.SYMBOL.contains(character);
    }
    
    /**
     * Tokenizes a Symbol
     *
     * @param {WaebricCharacter} character The character that needs to be tokenized
     * @return {WaebricCharacter} The next character to be tokenized
     */
	function tokenizeSymbol(character){		
        var token = new WaebricToken.SYMBOL(character.value, position.clone());
        var currentChar = character;''
        
        //&& and || operator should be processed as one symbol
        if (currentChar.equals('&')) {			
            if (currentChar.nextChar().equals('&')) {
				position.increaseColumn(1);
                currentChar = currentChar.nextChar();
                token.addChar(currentChar);				
            }
        } else if (currentChar.equals('|')) {
            if (currentChar.nextChar().equals('|')) {
				position.increaseColumn(1);
				currentChar = currentChar.nextChar();
                token.addChar(currentChar);                
            }
        }		
        tokenList.addToken(token);
        
        //Detect the end of an embedding
        if (currentChar == WaebricToken.SYMBOL.GREATERTHAN) {
            if (currentChar.nextChar() == '"') {
				position.increaseColumn(2);
                return currentChar.nextChar().nextChar(); //Skip current character + ending quote
            }
            return tokenizeDoubleQuotedText(currentChar);
        } else {
			position.increaseColumn(1);
            return currentChar.nextChar();
        }		
    }
    
    /**
     * Checks whether the input character is the start of single quoted text
     *
     * @param {WaebricCharacter} character The character that needs to be tokenized
     * @return {Boolean}
     */
    function isStartSingleQuotedText(character){
        return WaebricToken.TEXT.SINGLEQUOTEDTEXT_STARTCHAR.equals(character.value);
    }
    
    /**
     * Tokenizes the Quoted Text
     *
     * Quoted text can contain one other kind of token: Embeddings (see Waebric)
     *
     * @param {WaebricCharacter} character The character that needs to be tokenized
     * @return {WaebricCharacter} The next character to be tokenized
     */
    function tokenizeSingleQuotedText(character){
        var token = new WaebricToken.TEXT(WaebricToken.EMPTY_TOKEN_VALUE, position.clone());
        var currentChar = character.nextChar(); //Skip the opening quote
        
        //Process single quoted text until an illegal character is found
        while (WaebricToken.TEXT.isSingleQuotedText(currentChar.value)) {
			position.update(currentChar);
            token.addChar(currentChar);
            currentChar = currentChar.nextChar();
        }
        tokenList.addToken(token);
        
        //Detect start of embedding after the text
        if (WaebricToken.TEXT.EMBED_STARTCHAR.equals(currentChar.previousChar().value)) {
			position.decreaseColumn(1);
            tokenizeSymbol(currentChar.previousChar());
        }
        return currentChar;
    }
    
    /**
     * Checks whether the input character is the start of quotes text
     *
     * @param {WaebricCharacter} character The character that needs to be tokenized
     * @return {Boolean}
     */
    function isStartDoubleQuotedText(character){
        return WaebricToken.TEXT.QUOTEDTEXT_STARTCHAR.equals(character.value);
    }
    
    /**
     * Tokenizes the Quoted Text
     *
     * Quoted text can contain one other kind of token: Embeddings (see Waebric)
     *
     * @param {WaebricCharacter} character The character that needs to be tokenized
     * @return {WaebricCharacter} The next character to be tokenized
     */
    function tokenizeDoubleQuotedText(character){
        var token = new WaebricToken.TEXT(WaebricToken.EMPTY_TOKEN_VALUE, position.clone());
        var currentChar = character.nextChar(); //Skip the opening quote
        position.increaseColumn(1)
		
        //Process double quoted text until ending quote is found (or an embedding).
        while (!isEndQuoteText(currentChar) && !isStartEmbedding(currentChar) &&
        !isEndEmbedding(currentChar)) {
			position.update(currentChar)
            token.addChar(currentChar);
            currentChar = currentChar.nextChar();
			if(currentChar.value == null){
				throw new WaebricLexicalException('No ending quote of TEXT found.', position)
			}
        }
		position.increaseColumn(1);
        currentChar = currentChar.nextChar(); //Skip the ending quote      
        tokenList.addToken(token);
        
        //Detect start of embedding after the text
        if (WaebricToken.TEXT.EMBED_STARTCHAR.equals(currentChar.previousChar().value)) {
			position.decreaseColumn(1);
            tokenizeSymbol(currentChar.previousChar());
        }
        return currentChar;
    }
    
    /**
     * Checks whether the input character is the end of a quoted text.
     *
     * @param {WaebricCharacter} character The character that needs to be tokenized
     * @return {Boolean}
     */
    function isEndQuoteText(character){
        var currentChar = character;
        var previousChar = character.previousChar();
        return WaebricToken.TEXT.QUOTEDTEXT_ENDCHAR.equals(currentChar.value) && !"\\".equals(previousChar.value);
    }
    
    /**
     * Checks whether the input character is the start of an embedding tag.
     *
     * @param {WaebricCharacter} character The character that needs to be tokenized
     * @return {Boolean}
     */
    function isStartEmbedding (character){
        var currentChar = character;
        var previousChar = character.previousChar();
        var lastToken = tokenList.tokens[tokenList.tokens.length - 1]
        if (lastToken != null) {
			var isPartOfComment = WaebricToken.KEYWORD.COMMENT.equals(lastToken.value);
			return WaebricToken.TEXT.EMBED_STARTCHAR.equals(currentChar.value) && !"\\".equals(previousChar.value) && !isPartOfComment;
		}else{
			return WaebricToken.TEXT.EMBED_STARTCHAR.equals(currentChar.value) && !"\\".equals(previousChar.value)
		}
    }
    
    /**
     * Checks whether the input character is the end of an embedding tag.
     *
     * @param {WaebricCharacter} character The character that needs to be tokenized
     * @return {Boolean}
     */
    function isEndEmbedding(character){
        var currentChar = character;
        var previousChar = character.previousChar();
        return WaebricToken.TEXT.EMBED_ENDCHAR.equals(currentChar.value) &&
        WaebricToken.TEXT.QUOTEDTEXT_ENDCHAR.equals(currentChar.value) &&
        !"\\".equals(previousChar.value);
    }
    
    /**
     * Checks whether the input character is allowed in an identifier
     *
     * @param {WaebricCharacter} character The character that needs to be tokenized
     * @return {Boolean}
     */
    function isStartIdentifier(character){
        var regExp = new RegExp("[^\ \t\n\r./\\\\:.(){};=<>&|?#$@%+,\\]\[\!]");
        return character.match(regExp);
    }
    
    /**
     * Tokenizes the Identifier
     *
     * @param {WaebricCharacter} character The character that needs to be tokenized
     * @return {WaebricCharacter} The next character to be tokenized
     */
    function tokenizeIdentifier(character){
        var value = "";
		var startPosition = position.clone()
        var currentChar = character;
        while (isStartIdentifier(currentChar)) {
			position.update(currentChar);
            value += currentChar.value;
            currentChar = currentChar.nextChar();
        }
        //Check if the value is a waebric reserved keyword
        //Path elements are never tokenized as keywords
        //Path elements require a regular expression to be valid
        var isPathElement = isValidPathElement(value);
        if (WaebricToken.KEYWORD.contains(value) && !isPathElement) {			
            tokenList.addToken(new WaebricToken.KEYWORD(value, startPosition));
            return currentChar;
        } else if (!isPathElement) {
            if (isValidIdentifier(value)) {
                tokenList.addToken(new WaebricToken.IDENTIFIER(value, startPosition));
                return currentChar;
            } else {
                throw new WaebricLexicalException("Illegal character in identifier.", startPosition)
            }
        } else if (isPathElement) {
            if (isValidPath(value)) {
                tokenList.addToken(new WaebricToken.IDENTIFIER(value, startPosition));
                return currentChar;
            } else {
                throw new WaebricLexicalException("Illegal character in path element.", startPosition)
            }
        }
    }
    
    /**
     * Checks whether the input value is a valid path
     *
     * @param {String} value
     * @return {Boolean}
     */
    function isValidPath(value){
        var regExp = new RegExp('^[^\ \t\n\r.\/\\\\]*$');
        return value.match(regExp);
    }
    
    /**
     * Checks whether the input value is a valid identifier
     *
     * @param {String} value
     * @return {Boolean}
     */
    function isValidIdentifier(value){
        var regExp = new RegExp('^([A-Za-z][A-Za-z0-9\-]*)$');
        return value.match(regExp);
    }
    
    /**
     * Determines whether the input token is a path element.
     *
     * This function is required to prevent the tokenizer to categorize reserved keywords
     * as such in the case they are used in a path element.
     *
     * If the input value is a path element, then the value should be processed as an IDENTIFIER.
     *
     * @param {WaebricCharacter} value 
     * @return {Boolean}
     */
    function isValidPathElement(value){
        if (WaebricToken.KEYWORD.END.equals(value)) {
            return false;
        } else if (WaebricToken.KEYWORD.SITE.equals(tokenList.getLastKeyword().value)) {
            var dotFound = hasDotInSiteMapping();
            var colonFound = hasColonInSiteMapping();
            //If a DOT and a COLON is found inside a single SITE MAPPING, 
            // then the input value is markup
            if (dotFound && colonFound) {
                return false; //Markup
            }
            return true; //Path element
        }
        return false; //Not in SITE definition
    }
    
    /**
     * Returns whether a DOT appears in the current processed site mapping tokens
     * Note: this function operates at token level, not character level
	 *
     * @return {Boolean}
     */
    function hasDotInSiteMapping(){
        var currentTokenIndex = tokenList.tokens.length - 1.
        var currentToken = tokenList.tokens[currentTokenIndex];
        while (!WaebricToken.KEYWORD.SITE.equals(currentToken.value) &&
        !WaebricToken.KEYWORD.END.equals(currentToken.value) &&
        currentToken.value != WaebricToken.SYMBOL.SEMICOLON) {
            if (currentToken.value == WaebricToken.SYMBOL.DOT) {
                return true;
            }
            currentToken = tokenList.tokens[--currentTokenIndex];
        }
        return false;
    }
    
    /**
     * Returns whether a COLON appears in the current processed site mapping tokens
     * Note: this function operates at token level, not character level *
     *
     * @return {Boolean}
     */
    function hasColonInSiteMapping(){
        var currentTokenIndex = tokenList.tokens.length - 1.
        var currentToken = tokenList.tokens[currentTokenIndex];
        while (!WaebricToken.KEYWORD.SITE.equals(currentToken.value) &&
        !WaebricToken.KEYWORD.END.equals(currentToken.value) &&
        currentToken.value != WaebricToken.SYMBOL.DOT) {
            if (currentToken.value == WaebricToken.SYMBOL.COLON) {
                return true;
            }
            currentToken = tokenList.tokens[currentTokenIndex--];
        }
        return false;
    }
	
	/**
	 * Returns the position of the current token being processed by the tokenizer
	 * 
	 * @return {WaebricCharacter.Position}
	 */
	this.getPosition = function(){
		return position;
	}
}

/**
 * Reads the input stream and classifies the tokenList based on regular
 * expressions or starting and ending characters.
 *
 * @param {String} input String representation of a Waebric program
 * @return {WaebricTokenizerResult}
 * @exception {WaebricTokenizerException}
 */
WaebricTokenizer.tokenize = function(input, path){
    try {
        var tokenizer = new WaebricTokenizer();
        return tokenizer.tokenize(input);
    } catch (exception if exception instanceof WaebricLexicalException) {
        throw new WaebricTokenizerException(exception.message, exception.position, path, exception);
    } catch (exception) {
        throw new WaebricTokenizerException(exception.message, tokenizer.getPosition(), path, exception);
    }
}