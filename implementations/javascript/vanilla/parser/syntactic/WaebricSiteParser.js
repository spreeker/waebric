/**
 * Waebric Site Parser
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 */
function WaebricSiteParser(){
	
	this.markupParser = new WaebricMarkupParser();
	
	/**
	 * Parses the input to {Embedding}
	 * Updates currentToken of the parent parser
	 * 
	 * @param {Object} parentParser The parent parser
	 * @return {Site}
	 */
	this.parse = function(parentParser){
		this.parserStack.setStack(parentParser.parserStack)
		var site = this.parseSite(parentParser.currentToken);
		parentParser.setCurrentToken(this.currentToken);
		parentParser.parserStack.setStack(this.parserStack)
		return site;
	}
	
	/**
	 * Parses the input to {Site}
	 * 
	 * @param {WaebricParserToken} token The token to parse
	 * @return {Site}
	 */
	this.parseSite = function(token){
		this.parserStack.addParser('Site');
		this.setCurrentToken(token);  		
        var mappings = this.parseMappings(this.currentToken);
		this.parserStack.removeParser();
        return new Site(mappings);
	}	
	
	/**
     * Checks whether the input value is the start of {Site}
     *
     * @param {WaebricParserToken} token The token to evaluate
     * @return {Boolean}
     */
    this.isStartSite = function(token){
        return WaebricToken.KEYWORD.SITE.equals(token.value);
    }
	
	/**
     * Parses the input to a collection of {Mapping}
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {Array} Collection of {Mapping}
     */
    this.parseMappings = function(token){
        this.parserStack.addParser('Mappings');
		this.setCurrentToken(token);  

        var mappings = new Array();
        while (!WaebricToken.KEYWORD.END.equals(this.currentToken.value)) {
            //Skip mapping seperator
            var hasMultipleMappings = mappings.length > 0;
            var hasMappingSeperator = this.currentToken.value == WaebricToken.SYMBOL.SEMICOLON
            if (hasMultipleMappings && hasMappingSeperator) {
                this.setCurrentToken(this.currentToken.nextToken());
            } else if (hasMultipleMappings && hasMappingSeperator) {
				throw new WaebricSyntaxException(this, WaebricToken.SYMBOL.SEMICOLON, 'End of Site Mapping');
            }
            
            //Get current mapping
            var mapping = this.parseMapping(this.currentToken);
			
            //Save current mapping
            mappings.push(mapping);
            
            //Navigate to next token (skips the last token)
			var previousToken = this.currentToken;
            this.setCurrentToken(this.currentToken.nextToken());

            //Check mapping ending
            var hasSemicolonEnding = this.currentToken != null && WaebricToken.SYMBOL.SEMICOLON.equals(this.currentToken.value);				
            if (hasSemicolonEnding) {
				print('2')
				var hasEndEnding = !this.currentToken.hasNextToken() || WaebricToken.KEYWORD.END.equals(this.currentToken.nextToken().value)
				if (hasEndEnding) {
					throw new WaebricSyntaxException(this, WaebricToken.KEYWORD.END, 'Closing of last Site Mapping should be end');
				}							
            }else if(!hasSemicolonEnding){
				var hasEndEnding = this.currentToken != null && WaebricToken.KEYWORD.END.equals(this.currentToken.value)
				if (!hasEndEnding) {
					this.setCurrentToken(previousToken);
					throw new WaebricSyntaxException(this, WaebricToken.SYMBOL.SEMICOLON, 
						'Closing of Site Mapping');	
				}			
			}
        }
		this.parserStack.removeParser();
        return mappings;
    }
	
	/**
     * Parses the input to {Mapping}
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {Mapping}
     */
    this.parseMapping = function(token){
        this.parserStack.addParser('Mapping');
		this.setCurrentToken(token);  
		
        var path = this.parsePath(this.currentToken);
        var markup;
        
        var hasPathMarkupSeperator = (this.currentToken.value == WaebricToken.SYMBOL.COLON)
        var isMarkup = this.markupParser.isMarkup(this.currentToken.nextToken());
        
        if (hasPathMarkupSeperator && isMarkup) {
			this.setCurrentToken(this.currentToken.nextToken());
            markup = this.markupParser.parseSingle(this);
        } else if (!hasPathMarkupSeperator) {
			throw new WaebricSyntaxException(this, WaebricToken.SYMBOL.COLON, 'Path/Markup seperator');
        } else if (!isMarkup) {
			this.setCurrentToken(this.currentToken.nextToken());
			throw new WaebricSyntaxException(this, 'Identifier', 'Start of Markup');
        }
		this.parserStack.removeParser();
        return new Mapping(path, markup);
    }
	
	/**
     * Parses the input to {String}
     * 
     * @parem {WaebricParserToken} token The token to parse
     * @return {String}
     */
    this.parsePath = function(token){
        this.parserStack.addParser('Site Path');
		this.setCurrentToken(token);  
		
        var path;
        if (this.currentToken.value instanceof WaebricToken.IDENTIFIER) {
            //Build directory + filename
            var directoryFileName = "";
            while (this.isDirectory(this.currentToken.value)) {
                directoryFileName += this.currentToken.value;
                
				if(this.currentToken.hasNextToken()){
					this.setCurrentToken(this.currentToken.nextToken());					
				}else{
					throw new WaebricSyntaxException(this, WaebricToken.SYMBOL.DOT, 'Filename/extension seperator');
				}
            }

           	//Skip DOT file extension
           	this.setCurrentToken(this.currentToken.nextToken());
            
            //Build file extension
            var fileExtension = "";
            while (this.isFileExtension(this.currentToken.value)) {
                fileExtension += this.currentToken.value;
                this.setCurrentToken(this.currentToken.nextToken());
            }
            
            //Path should be valid
            path = directoryFileName + "." + fileExtension;
            if (!this.isPath(path)) {
				throw new WaebricSyntaxException(this, 'Path', 'Invalid characters found in Path');
            }
			
			this.parserStack.removeParser();
			return path.toString();
        }
		
		throw new WaebricSyntaxException(this, 'Identifier', 'Start of path');
    }
	
	/**
     * Checks whether the input value is a valid directory
     * 
     * @param {WaebricParserToken} value The value to evaluate
     * @return {Boolean}
     */
    this.isDirectory = function(value){
        var regExp = new RegExp("^[^\ \t\n\r.\\\\]*$");
        return value.match(regExp);
    }
    
    /**
     * Checks whether the input value is a valid file extension
     * 
     * @param {WaebricParserToken} value The value to evaluate
     * @return {Boolean}
     */
    this.isFileExtension = function(value){
        var regExp = new RegExp("[a-zA-Z0-9]");
        return value.match(regExp);
    }
    
    /**
     * Checks whether the input value is a valid path
     * 
     * @param {WaebricParserToken} value The value to evaluate
     * @return {Boolean}
     */
    this.isPath = function(value){
        var regExp = new RegExp("^[^\ \t\n\r.\\\\]*.[A-Za-z0-9]*$");
        return value.match(regExp);
    }
}
WaebricSiteParser.prototype = new WaebricBaseParser();