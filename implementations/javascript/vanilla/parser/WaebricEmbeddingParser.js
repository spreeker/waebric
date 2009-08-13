/**
 * Waebric Embedding Parser
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 */
function WaebricEmbeddingParser(){
	
	this.expressionParser = new WaebricExpressionParser();
	this.markupParser = new WaebricMarkupParser();
	
	/**
	 * Parses the input to {Embedding}
	 * Updates currentToken of the parent parser
	 *
	 * @param {Object} parentParser The parent parser
	 * @return {Embedding}
	 */
	this.parse = function(parentParser){
		this.parserStack.setStack(parentParser.parserStack)
		var embedding = this.parseEmbedding(parentParser.currentToken);
		parentParser.setCurrentToken(this.currentToken);
		parentParser.parserStack.setStack(this.parserStack)
		return embedding;
	}
	
	/**
	 * Checks whether the input value is the start of an {Embedding}
	 *
	 * @param {WaebricParserToken} token The token to be parsed
	 * @return {Boolean}
	 */
	this.isStartEmbedding = function(token){
		var isValidOpening = this.expressionParser.isText(token) && token.nextToken().value == WaebricToken.SYMBOL.LESSTHAN ||
		token.value == WaebricToken.SYMBOL.LESSTHAN;
		return isValidOpening;
	}
	
	/**
	 * Parses the input to {Embedding}
	 *
	 * @param {WaebricParserToken} token The token to be parsed
	 * @return {Embedding}
	 */
	this.parseEmbedding = function(token){
		this.parserStack.addParser('Embedding')
		this.setCurrentToken(token);  
		
		var head = "";
		var embed;
		var tail;
		
		if (this.expressionParser.isText(this.currentToken)) {
			head = this.currentToken.value.toString();
			this.currentToken = this.currentToken.nextToken();
		}
		
		embed = this.parseEmbed(this.currentToken.nextToken());
		tail = this.parseTail(this.currentToken.nextToken());
		
		this.parserStack.removeParser();
		return new Embedding(head, embed, tail);
	}
	
	/**
	 * Parses the input to {PostTextTail} or {MidTextTail}
	 *
	 * @param {WaebricParserToken} token The token to be parsed
	 * @return {PostTextTail} or {MidTextTail}
	 */
	this.parseTail = function(token){
		this.parserStack.addParser('PostTextTail/MidTextTail')
		this.setCurrentToken(token);  
		
		var text = "";
		
		//Validate seperator
		var hasValidSeperator = this.currentToken.value == WaebricToken.SYMBOL.GREATERTHAN;
		if (!hasValidSeperator) {
			throw new WaebricSyntaxException(this, WaebricToken.SYMBOL.GREATERTHAN, 'Closing embedding', 'PostTextTail or MidTextTail')
		}
		
		//Get next symbol to determine PostTextTail or MidTextTail		
		if (this.expressionParser.isText(this.currentToken.nextToken())) {
			this.currentToken = this.currentToken.nextToken();
			text = this.currentToken.value.toString();
			if (this.currentToken.nextToken().value != WaebricToken.SYMBOL.SEMICOLON) {
				this.currentToken = this.currentToken.nextToken();
			}
		}
		
		//If > follows, than the remaining tokens are processed as TEXT 
		//since they are part an embedding
		if (this.currentToken.value == WaebricToken.SYMBOL.LESSTHAN) {
			var embed = this.parseEmbed(this.currentToken.nextToken());
			var tail = this.parseTail(this.currentToken.nextToken());
			this.parserStack.removeParser();
			return new MidTextTail(text, embed, tail)
		} else {
			this.parserStack.removeParser();
			return new PostTextTail(text);
		}
	}
	
	/**
	 * Parses the input to {Embed}
	 *
	 * @param {WaebricParserToken} token The token to be parsed
	 * @return {Embed}
	 */
	this.parseEmbed = function(token){
		this.parserStack.addParser('Embed')
		this.setCurrentToken(token);  
		
		var startTokenLastMarkup = this.markupParser.getLastMarkup(token);
		var startToken = this.currentToken;
		
		var markups = this.markupParser.parseMultiple(this);
		
		//Make sure that Markup is processed correctly
		// echo "<p>"; --> Variable
		// echo "<p()>"; --> Markup
		// echo "<p p>"; --> Markup, Variable
		// echo "<p p()>"; --> Markup, Markup
		var expression;
		if (this.expressionParser.isExpression(this.currentToken.nextToken())) {
			this.currentToken = this.currentToken.nextToken();
			expression = this.expressionParser.parse(this);
			this.parserStack.removeParser();
			return new ExpressionEmbedding(markups, expression);
		} else if (markups.length > 1 && !this.markupParser.isMarkupCall(startTokenLastMarkup)) {
			markups = markups.slice(0, markups.length - 1);
			expression = new VarExpression(startTokenLastMarkup.value.toString());
			this.parserStack.removeParser();
			return new ExpressionEmbedding(markups, expression);
		} else if (markups.length == 1 && !this.markupParser.isMarkupCall(startToken)) {
			expression = new VarExpression(startToken.value.toString());
			this.parserStack.removeParser();
			return new ExpressionEmbedding(new Array(), expression);
		}
		this.parserStack.removeParser();
		return new MarkupEmbedding(markups);
	}
}
WaebricEmbeddingParser.prototype = new WaebricBaseParser();
