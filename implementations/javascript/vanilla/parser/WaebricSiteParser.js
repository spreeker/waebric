
function WaebricSiteParser(){
	
	this.currentToken;	
	this.markupParser = new WaebricMarkupParser();
	
	this.parse = function(parentParser){
		var site = this.parseSite(parentParser.currentToken);
		parentParser.currentToken = this.currentToken;
		return site;
	}
	
	/**
	 * Parses the input to {Site}
	 * 
	 * @param {WaebricParserToken} token
	 * @return {Site}
	 */
	this.parseSite = function(token){
		this.currentToken = token;
        var mappings = this.parseMappings(this.currentToken);
        return new Site(mappings);
	}	
	
	/**
     * Checks whether the input value is the start of {Site}
     *
     * @param {WaebricParserToken} token
     * @return {Boolean}
     */
    this.isStartSite = function(token){
        return WaebricToken.KEYWORD.SITE.equals(token.value);
    }
	
	/**
     * Parses the input to a collection of {Mapping}
     *
     * @param {WaebricParserToken} token
     * @return {Array} Collection of {Mapping}
     */
    this.parseMappings = function(token){
        this.currentToken = token;

        var mappings = new Array();
        while (!WaebricToken.KEYWORD.END.equals(this.currentToken.value)) {
            //Skip mapping seperator
            var hasMultipleMappings = mappings.length > 0;
            var hasMappingSeperator = this.currentToken.value == WaebricToken.SYMBOL.SEMICOLON
            if (hasMultipleMappings && hasMappingSeperator) {
                this.currentToken = this.currentToken.nextToken();
            } else if (hasMultipleMappings && hasMappingSeperator) {
                print('Error parsing Mapping. Expected semicolon after mapping but found ' + this.currentToken.value);
            }
            
            //Get current mapping
            var mapping = this.parseMapping(this.currentToken);
			
            //Save current mapping
            mappings.push(mapping);
            
            //Navigate to next token (skips the last token)
            this.currentToken = this.currentToken.nextToken();

            //Check mapping ending
            var hasSemicolonEnding = WaebricToken.SYMBOL.SEMICOLON.equals(this.currentToken.value);			
            var hasMappingEnding = hasSemicolonEnding && WaebricToken.KEYWORD.END.equals(this.currentToken.nextToken().value)
            if (hasMappingEnding) {
                print('Error parsing Mapping. The last SITE MAPPING should be followed by "END" but found ' + this.currentToken.value);
            }
        }
        return mappings;
    }
	
	/**
     * Parses the input to {Mapping}
     *
     * @param {WaebricParserToken} token
     * @return {Mapping}
     */
    this.parseMapping = function(token){
        this.currentToken = token;
        var path = this.parsePath(this.currentToken);
        var markup;
        
        var hasPathMarkupSeperator = (this.currentToken.value == WaebricToken.SYMBOL.COLON)
        var isMarkup = this.markupParser.isMarkup(this.currentToken.nextToken());
        
        if (hasPathMarkupSeperator && isMarkup) {
			this.currentToken = this.currentToken.nextToken();
            markup = this.markupParser.parseSingle(this);
        } else if (!hasPathMarkupSeperator) {
            print('Error parsing Mapping. Expected colon after path but found ' + this.currentToken.value);
        } else if (!isMarkup) {
            print('Error parsing Mapping. Expected Markup after path but found ' + this.currentToken.nextToken().value);
        }
        return new Mapping(path, markup);
    }
	
	/**
     * Parses the input to {String}
     * 
     * @parem {WaebricParserToken} token
     * @return {String}
     */
    this.parsePath = function(token){
        this.currentToken = token;
        var path;
        if (this.currentToken.value instanceof WaebricToken.IDENTIFIER) {
            //Build directory + filename
            var directoryFileName = "";
            while (this.isDirectory(this.currentToken.value)) {
                directoryFileName += this.currentToken.value;
                this.currentToken = this.currentToken.nextToken();
            }
            
            //Skip DOT file extension
            var hasDotSeperator = this.currentToken.value == WaebricToken.SYMBOL.DOT
            if (hasDotSeperator) {
                this.currentToken = this.currentToken.nextToken();
            } else {
                print('Error parsing path. Expected DOT between filename and extension but found ' +
                this.currentToken.nextToken().value);
            }
            
            //Build file extension
            var fileExtension = "";
            while (this.isFileExtension(this.currentToken.value)) {
                fileExtension += this.currentToken.value;
                if (this.currentToken.value instanceof WaebricToken.IDENTIFIER &&
                this.currentToken.nextToken().value instanceof WaebricToken.IDENTIFIER) {
                    print('Error parsing path. Found whitespace in file extension.');
                    return;
                }
                this.currentToken = this.currentToken.nextToken();
            }
            
            //Path should be valid
            path = directoryFileName + "." + fileExtension;
            if (this.isPath(path)) {
                return path.toString();
            } else {
                print('Error parsing path. Path has invalid characters. ' + path);
            }
        }
        print('Error parsing path. Path should start with an identifier ' + this.currentToken.value);
    }
	
	/**
     * Checks whether the input value is a valid directory
     * 
     * @param {WaebricParserToken} value
     * @return {Boolean}
     */
    this.isDirectory = function(value){
        var regExp = new RegExp("^[^\ \t\n\r.\\\\]*$");
        return value.match(regExp);
    }
    
    /**
     * Checks whether the input value is a valid file extension
     * 
     * @param {WaebricParserToken} value
     * @return {Boolean}
     */
    this.isFileExtension = function(value){
        var regExp = new RegExp("[a-zA-Z0-9]");
        return value.match(regExp);
    }
    
    /**
     * Checks whether the input value is a valid path
     * 
     * @param {WaebricParserToken} value
     * @return {Boolean}
     */
    this.isPath = function(value){
        var regExp = new RegExp("^[^\ \t\n\r.\\\\]*.[A-Za-z0-9]*$");
        return value.match(regExp);
    }
}
