
function WaebricEmbeddingParser(){
	
	this.currentToken;	
	
	this.expressionParser = new WaebricExpressionParser();
	this.markupParser = new WaebricMarkupParser();
	
	this.parse = function(parentParser){
		var embedding = this.parseEmbedding(parentParser.currentToken);
		parentParser.currentToken = this.currentToken;
		return embedding;
	}
	
	/**
	 * Checks whether the input value is the start of an {Embedding}
	 * 
	 * @param {WaebricParserToken} token
	 * @return {Boolean}
	 */
	this.isStartEmbedding = function(token){	
		var isValidOpening = this.expressionParser.isText(token) && token.nextToken().value == WaebricToken.SYMBOL.LESSTHAN		
			 || token.value == WaebricToken.SYMBOL.LESSTHAN;
		return isValidOpening;
	}
	
	/**
	 * Parses the input to an {Embedding}
	 * 
	 * @param {WaebricParserToken} token
	 * @return {Embedding}
	 */
	this.parseEmbedding = function(token){
		this.currentToken = token;
		
		var head = "";
		var embed;
		var tail;	
		
		if (this.expressionParser.isText(this.currentToken)) {
			head = this.currentToken.value.toString();
			this.currentToken = this.currentToken.nextToken();		
		}
		
		if(this.currentToken.value == WaebricToken.SYMBOL.LESSTHAN){			
			embed = this.parseEmbed(this.currentToken.nextToken());			
		}else{
			print('Error parsing Embedding. Expected "<" but found ' + this.currentToken.nextToken().value)
		}	
		
		tail = this.parseTail(this.currentToken.nextToken());
		return new Embedding(head, embed, tail);
	}
	
	/**
	 * Parses the input to a {PostTextTail} or a {MidTextTail}
	 * 
	 * @param {WaebricParserToken} token
	 * @return {PostTextTail} or {MidTextTail}
	 */
	this.parseTail = function(token){
		this.currentToken = token;
		var text = "";	
		
		//Validate seperator
		var hasValidSeperator = this.currentToken.value == WaebricToken.SYMBOL.GREATERTHAN;
		if(!hasValidSeperator){
			print('Error parsing TextTail. Expected > but found ' + this.currentToken.value)
		}		
		
		//Get next symbol to determine PostTextTail or MidTextTail		
		if(this.expressionParser.isText(this.currentToken.nextToken())){
			this.currentToken = this.currentToken.nextToken();
			text = this.currentToken.value.toString();
			if(this.currentToken.nextToken().value != WaebricToken.SYMBOL.SEMICOLON){
				this.currentToken = this.currentToken.nextToken();	
			}
		}
		
		//If > follows, than the remaining tokens are processed as TEXT 
		//since they are part an embedding
		if(this.currentToken.value == WaebricToken.SYMBOL.LESSTHAN){
			var embed = this.parseEmbed(this.currentToken.nextToken());
			var tail = this.parseTail(this.currentToken.nextToken());
			return new MidTextTail(text, embed, tail)
		}else{
			return new PostTextTail(text);
		}
	}
	
	/**
	 * Parses the input to an {Embed}
	 * 
	 * @param {WaebricParserToken} token
	 * @return {Embed}
	 */
	this.parseEmbed = function(token){
		this.currentToken = token;
		
		var startTokenLastMarkup = this.markupParser.getLastMarkup(token);
		var startToken = this.currentToken;
		
        var markups = this.markupParser.parseMultiple(this);	

		//Make sure that Markup is processed correctly
		// echo "<p>"; --> Variable
		// echo "<p()>"; --> Markup
		// echo "<p p>"; --> Markup, Variable
		// echo "<p p()>"; --> Markup, Markup
		var expression;
		if(this.expressionParser.isExpression(this.currentToken.nextToken())){
			this.currentToken = this.currentToken.nextToken();
			expression = this.expressionParser.parse(this); 
			return new ExpressionEmbedding(markups, expression);
		}else if(markups.length > 1 && !this.markupParser.isMarkupCall(startTokenLastMarkup)){	
			markups = markups.slice(0, markups.length-1);	
			expression = new VarExpression(startTokenLastMarkup.value.toString());		
			return new ExpressionEmbedding(markups, expression);	
		}else if(markups.length == 1 && !this.markupParser.isMarkupCall(startToken)){			
			expression = new VarExpression(startToken.value.toString());		
			return new ExpressionEmbedding(new Array(), expression);	
		}
		return new MarkupEmbedding(markups);		
	}	

}
