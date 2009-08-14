/**
 * Waebric Statement Parser
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 */
function WaebricStatementParser(){
	
    this.predicateParser = new WaebricPredicateParser();
	this.expressionParser = new WaebricExpressionParser();
	this.embeddingParser = new WaebricEmbeddingParser();
	this.markupParser = new WaebricMarkupParser();
	
	/**
	 * Parses the input to a Statement
	 * Updates currentToken of the parent parser
	 * 
	 * @param {Object} parentParser The parent parser
	 * @return {IfStatement, IfElseStatement, EachStatement, LetStatement, 
	 * 			BlockStatement, CommentStatement, EchoExpressionStatement, 
	 * 			EchoEmbeddingStatement, CDataStatement, YieldStatement, 
	 * 			MarkupStatement, MarkupMarkupStatement, MarkupExpressionStatement, 
	 * 			MarkupEmbeddingStatement, MarkupStatementStatement}
	 */
    this.parseSingle = function(parentParser){
		this.parserStack.setStack(parentParser.parserStack)
		var statement = this.parseStatement(parentParser.currentToken);
		parentParser.setCurrentToken(this.currentToken);
		parentParser.parserStack.setStack(this.parserStack)
		return statement;
    }
    
	/**
	 * Parses the input to a Statement
	 * Updates currentToken of the parent parser
	 * 
	 * @param {Object} parentParser The parent parser
	 * @return {Array} An collection of statements
	 */
    this.parseMultiple = function(parentParser){
		this.parserStack.setStack(parentParser.parserStack)
		var statements = this.parseStatements(parentParser.currentToken);
		parentParser.setCurrentToken(this.currentToken);
		parentParser.parserStack.setStack(this.parserStack)
		return statements;		
    }
    
    /**
     * Parses the input to a Statement
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {IfStatement, IfElseStatement, EachStatement, LetStatement, 
	 * 			BlockStatement, CommentStatement, EchoExpressionStatement, 
	 * 			EchoEmbeddingStatement, CDataStatement, YieldStatement, 
	 * 			MarkupStatement, MarkupMarkupStatement, MarkupExpressionStatement, 
	 * 			MarkupEmbeddingStatement, MarkupStatementStatement}
     */
    this.parseStatement = function(token){
		this.parserStack.addParser('Statement')
		this.setCurrentToken(token);

		var statement;
        if (this.isStartIfStatement(this.currentToken)) {
            statement = this.parseIfElseStatement(this.currentToken);
        } else if (this.isStartEachStatement(this.currentToken)) {
            statement = this.parseEachStatement(this.currentToken);
        } else if (this.isStartLetStatement(this.currentToken)) {
            statement = this.parseLetStatement(this.currentToken);
        } else if (this.isStartBlockStatement(this.currentToken)) {
            statement = this.parseBlockStatement(this.currentToken);
        } else if (this.isStartCommentStatement(this.currentToken)) {
            statement = this.parseCommentStatement(this.currentToken);
        } else if (this.isEchoEmbeddingStatement(this.currentToken)) {
            statement = this.parseEchoEmbeddingStatement(this.currentToken);
        } else if (this.isEchoStatement(this.currentToken)) {
            statement = this.parseEchoStatement(this.currentToken);
        } else if (this.isCDataStatement(this.currentToken)) {
            statement = this.parseCDataStatement(this.currentToken);
        } else if (this.isYieldStatement(this.currentToken)) {
            statement = this.parseYieldStatement(this.currentToken);
        } else if (this.isMarkupStatement(this.currentToken)) {			
            statement = this.parseMarkupStatement(this.currentToken)
        } else if (this.isMarkupEmbeddingStatement(this.currentToken)) {
            statement = this.parseMarkupEmbeddingStatement(this.currentToken)
        } else if (this.isMarkupExpressionStatement(this.currentToken)) {
            statement = this.parseMarkupExpressionStatement(this.currentToken)
        } else if (this.isMarkupMarkupStatement(this.currentToken)) {
            statement = this.parseMarkupMarkupStatement(this.currentToken)
        } else if (this.isMarkupStatementStatement(this.currentToken)) {
            statement = this.parseMarkupStatementStatement(this.currentToken)
        } else {
			throw new WaebricSyntaxException(this, '"IF", "EACH", "LET", "{", "COMMENT", "ECHO", "CDATA", "YIELD" or Identifier', 'Start of statement');
        }

		this.parserStack.removeParser();
		return statement;
    }
    
	/**
     * Parses the input a collection of Statements
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {Array} A collection of statements
     */
    this.parseStatements = function(token){
        this.parserStack.addParser('Statements');
		this.setCurrentToken(token);  
		
        var statements = new Array();
        while (!WaebricToken.KEYWORD.END.equals(this.currentToken.value) 
				&& !WaebricToken.KEYWORD.DEF.equals(this.currentToken.value)) {
            statement = this.parseStatement(this.currentToken);
            statements.push(statement);
			if (this.currentToken.hasNextToken()) {
				this.setCurrentToken(this.currentToken.nextToken());
			}else{
				print(this.currentToken.value.position)
				this.currentToken.value.value = 'EOF (End of File)'
				throw new WaebricSyntaxException(this, WaebricToken.KEYWORD.END, 'Closing function definition');
			}
        }
		this.parserStack.removeParser();
        return statements;
    }
	
	/**
	 * Checks whether the input value is the start of an {IfStatement} or {IfElseStatement}
	 * 
	 * @param {WaebricParserToken} token The token to evaluate
	 * @return {Boolean}
	 */
	this.isStartIfStatement = function(token){
        return WaebricToken.KEYWORD.IF.equals(token.value)
    }
    
    /**
     * Parses the input to an {IfStatement} or {IfElseStatement}. If the else clause is not specified, it returns an If statement
     * 
     * @param {WaebricParserToken} token The token to parse
     * @return {IfElseStatement}
     * @return {IfStatement}
     */
    this.parseIfElseStatement = function(token){
        this.parserStack.addParser('IfElseStatement');
		this.setCurrentToken(token.nextToken());   //Skip IF keyword
		
        var predicate;
        var ifStatement;
        var elseStatement;
        
		//Parse opening
		var hasValidOpening = this.currentToken.value == WaebricToken.SYMBOL.LEFTRBRACKET;
        if (hasValidOpening) {
            this.setCurrentToken(this.currentToken.nextToken());
        } else {
			this.setCurrentToken(this.currentToken.nextToken());
			throw new WaebricSyntaxException(this, WaebricToken.SYMBOL.LEFTRBRACKET,'Opening predicate');
        }
		
        //Parse predicate
        if (this.predicateParser.isStartPredicate(this.currentToken)) {
            predicate = this.predicateParser.parse(this);
        } else {
			throw new WaebricSyntaxException(this, 'Expression or "!" ', 
					'Predicate', 'IfElseStatement');
        }
        
        //Predicate should be ended correctly
        var hasValidEnding = this.currentToken.nextToken().value == WaebricToken.SYMBOL.RIGHTRBRACKET;
        if (hasValidEnding) {
            this.setCurrentToken(this.currentToken.nextToken());
        } else {
			this.setCurrentToken(this.currentToken.nextToken());
			throw new WaebricSyntaxException(this, WaebricToken.SYMBOL.RIGHTRBRACKET, 'Closing predicate');
        }
        
        //Parse IF statement
        ifStatement = this.parseStatement(this.currentToken.nextToken());
        
        //Parse ELSE statement (if exists)
        var hasElseClause = WaebricToken.KEYWORD.ELSE.equals(this.currentToken.nextToken().value);
        if (hasElseClause) {
            elseStatement = this.parseStatement(this.currentToken.nextToken().nextToken());
			this.parserStack.removeParser();
            return new IfElseStatement(predicate, ifStatement, elseStatement);
        }
		this.parserStack.removeParser();
        return new IfStatement(predicate, ifStatement);
    }
	
	/**
	 * Checks whether the input value is the start of an {EachStatement}
	 * 
	 * @param {WaebricParserToken} token The token to evaluate
	 * @return {Boolean}
	 */
	this.isStartEachStatement = function(token){
        return WaebricToken.KEYWORD.EACH.equals(token.value)
    }
	
	/**
	 * Parses the input to an {EachStatement}
	 * 
	 * @param {WaebricParserToken} token The token to parse
	 * @return {EachStatement}
	 */
    this.parseEachStatement = function(token){
        this.parserStack.addParser('EachStatement');
		this.setCurrentToken(token);  
        
        var identifier;
        var expression;
        var statement;
        
        //Parse LEFT ROUND BRACKET
        if (this.currentToken.nextToken().value == WaebricToken.SYMBOL.LEFTRBRACKET) {
            this.setCurrentToken(this.currentToken.nextToken()); //Skip
        } else {
			throw new WaebricSyntaxException(this, WaebricToken.SYMBOL.LEFTRBRACKET, 'Opening evaluation');
        }
        
        //Parse IDENTIFIER
        if (this.expressionParser.isIdentifier(this.currentToken.nextToken())) {
			this.setCurrentToken(this.currentToken.nextToken())
            identifier = this.currentToken.value.toString();
        } else {
			throw new WaebricSyntaxException(this, 'Identifier', 'Variable of EachStatement');
        }
        
        //Parse COLON
        if (this.currentToken.nextToken().value == WaebricToken.SYMBOL.COLON) {
            this.setCurrentToken(this.currentToken.nextToken()); //Skip
        } else {
			this.setCurrentToken(this.currentToken.nextToken());
			throw new WaebricSyntaxException(this, WaebricToken.SYMBOL.COLON, 'Identifier/Expression seperator');
        }
        
        //Parse EXPRESSION
        if (this.expressionParser.isExpression(this.currentToken.nextToken())) {
			this.setCurrentToken(this.currentToken.nextToken());
            expression = this.expressionParser.parse(this);
        } else {
			throw new WaebricSyntaxException(this, 'Expression', 'Expression of EachStatement');
        }
        
        //Parse RIGHT ROUND BRACKET
        if (this.currentToken.nextToken().value == WaebricToken.SYMBOL.RIGHTRBRACKET) {
            this.setCurrentToken(this.currentToken.nextToken()); //Skip
        } else {
			throw new WaebricSyntaxException(this, WaebricToken.SYMBOL.RIGHTRBRACKET, 'Closing evaluation');
        }
        
        //Parse STATEMENT
        statement = this.parseStatement(this.currentToken.nextToken());
		
		this.parserStack.removeParser();
        return new EachStatement(identifier, expression, statement);
    }

	/**
	 * Checks whether the input value is the start of a {LetStatement}
	 * 
	 * @param {WaebricParserToken} token The token to evaluate
	 * @return {Boolean}
	 */
    this.isStartLetStatement = function(token){
        return WaebricToken.KEYWORD.LET.equals(token.value);
    }
    
	/**
	 * Parses the input to a {LetStatement}
	 * 
	 * @param {WaebricParserToken} token The token to parse
	 * @return {LetStatement}
	 */
    this.parseLetStatement = function(token){
        this.parserStack.addParser('LetStatement');
		this.setCurrentToken(token);  
        var assignments = this.parseAssignments(this.currentToken);		
        var statements = this.parseStatements(this.currentToken.nextToken());  
		
        var hasValidClosing = WaebricToken.KEYWORD.END.equals(this.currentToken.value);
        if (!hasValidClosing) {
			throw new WaebricSyntaxException(this, WaebricToken.KEYWORD.END, 'Closing LetStatement');
        }      
		this.parserStack.removeParser();
		return new LetStatement(assignments, statements);
    }
    
    /**
     * Checks whether the input value is the start of a {BlockStatement}
     * 
     * @param {WaebricParserToken} token The token to evaluate
     * @return {Boolean}
     */
    this.isStartBlockStatement = function(token){
        return token.value == WaebricToken.SYMBOL.LEFTCBRACKET;
    }
    
	/**
	 * Parses the input to a {BlockStatement}
	 * 
	 * @param {WaebricParserToken} token The token to parse
	 * @return {BlockStatement}
	 */
    this.parseBlockStatement = function(token){
        this.parserStack.addParser('BlockStatement');
		this.setCurrentToken(token.nextToken());   //Skip "{" opening
		
        var statements = new Array();
        while (this.currentToken.value != WaebricToken.SYMBOL.RIGHTCBRACKET) {
            var statement = this.parseStatement(this.currentToken);
            statements.push(statement);
            this.setCurrentToken(this.currentToken.nextToken());
        }
		
		this.parserStack.removeParser();
        return new BlockStatement(statements);
    }
    
	/**
	 * Checks whether the input value is the start of a {CommentStatement}
	 * 
	 * @param {WaebricParserToken} token The token to evaluate
	 * @return {Boolean}
	 */
    this.isStartCommentStatement = function(token){
        return WaebricToken.KEYWORD.COMMENT.equals(token.value);
    }
    
	/**
	 * Parses the input to a {CommentStatement}
	 * 
	 * @param {WaebricParserToken} token The token to parse
	 * @return {CommenStatement}
	 */
    this.parseCommentStatement = function(token){
        this.parserStack.addParser('CommentStatement');
		this.setCurrentToken(token.nextToken());  //Skip comment keyword
        
        var comment;
        if (this.isStrCon(this.currentToken)) {
            comment = this.currentToken.value.toString();
        } else {
			throw new WaebricSyntaxException(this, 'StrCon', 'content of comment');
        }
		
		var hasValidClosing = this.currentToken.nextToken().value == WaebricToken.SYMBOL.SEMICOLON;
        if (!hasValidClosing) {
			this.setCurrentToken(this.currentToken.nextToken());
            throw new WaebricSyntaxException(this, WaebricToken.SYMBOL.SEMICOLON, 'Closing of statement');
        }	
			
		this.setCurrentToken(this.currentToken.nextToken()); //Skip ";" end
		this.parserStack.removeParser();
        return new CommentStatement(comment);
    }
	
	/**
	 * Checks whether the input value is a valid input for the {CommentStatement} value
	 * 
	 * @param {WaebricParserToken} token The token to evaluate
	 * @return {Boolean}
	 */
	this.isStrCon = function(token){
		var regExp = new RegExp('^([^\x00-\x1F\"\\\\]*(\\\\n)*(\\\\t)*(\\\\")*(\\\\[0-9]{3})*)*$');
        return token.value instanceof WaebricToken.TEXT && token.value.match(regExp) != null;		
	}
    	
	/**
	 * Checks whether the input value is an {EchoEmbeddingStatement}
	 * 
	 * @param {WaebricParserToken} token The token to evaluate
	 * @return {Boolean}
	 */
	this.isEchoEmbeddingStatement = function(token){
		var isValidOpening = WaebricToken.KEYWORD.ECHO.equals(token.value);
		var isValidEmbedding = isValidOpening && this.embeddingParser.isStartEmbedding(token.nextToken());
		return isValidEmbedding;
	}
	
	/**
	 * Parses the input to an {EchoEmbeddingStatement}
	 * 
	 * @param {WaebricParserToken} token  The token to parse
	 * @return {EchoEmbeddingStatement}
	 */
	this.parseEchoEmbeddingStatement = function(token){
		this.parserStack.addParser('EchoEmbeddingStatement');
		this.setCurrentToken(token.nextToken()); //Skip echo keyword
		
		var embedding = this.embeddingParser.parse(this);
		
		var hasValidClosing = this.currentToken.nextToken().value == WaebricToken.SYMBOL.SEMICOLON;
        if (!hasValidClosing) {
			this.setCurrentToken(this.currentToken.nextToken());
            throw new WaebricSyntaxException(this, WaebricToken.SYMBOL.SEMICOLON, 'Closing of statement');
        }
		this.setCurrentToken(this.currentToken.nextToken()); //Skip ";" ending
		this.parserStack.removeParser();
		return new EchoEmbeddingStatement(embedding);		
	}
	
	/**
	 * Checks whether the input value is an {EchoStatement}
	 * 
	 * @param {WaebricParserToken} token
	 * @return {Boolean}
	 */
	this.isEchoStatement = function(token){
		return WaebricToken.KEYWORD.ECHO.equals(token.value);
	}
	
	/**
	 * Parses the input to an {EchoStatement}
	 * 
	 * @param {WaebricParserToken} token  The token to parse
	 * @return {EchoStatement}
	 */
	this.parseEchoStatement = function(token){
		this.parserStack.addParser('EchoStatement')
		this.setCurrentToken(token.nextToken()); //Skip echo keyword
		var expression = this.expressionParser.parse(this);
		
		var hasValidClosing = this.currentToken.nextToken().value == WaebricToken.SYMBOL.SEMICOLON;
        if (!hasValidClosing) {
			this.setCurrentToken(this.currentToken.nextToken());
            throw new WaebricSyntaxException(this, WaebricToken.SYMBOL.SEMICOLON, 'Closing of statement');
        }
		this.setCurrentToken(this.currentToken.nextToken()); //Skip ";" ending
		this.parserStack.removeParser();
		return new EchoStatement(expression);		
	}
	
	/**
	 * Checks whether the input value is a {CDataStatement}
	 * 
	 * @param {WaebricParserToken} token The token to evaluate
	 * @return {Boolean}
	 */
	this.isCDataStatement = function(token){
		var isValidOpening = WaebricToken.KEYWORD.CDATA.equals(token.value);
		var isValidExpression = isValidOpening && this.expressionParser.isExpression(token.nextToken());
		return isValidExpression;
	}
	
	/**
	 * Parses the input to a {CDataStatement}
	 * 
	 * @param {WaebricParserToken} token  The token to parse
	 * @return {CDataStatement}
	 */
	this.parseCDataStatement = function(token){
		this.parserStack.addParser('CDataStatement');
		this.setCurrentToken(token.nextToken()); //Skip CData keyword
		
		var expression = this.expressionParser.parse(this);
		
		var hasValidClosing = this.currentToken.nextToken().value == WaebricToken.SYMBOL.SEMICOLON;
        if (!hasValidClosing) {
			this.setCurrentToken(this.currentToken.nextToken());
            throw new WaebricSyntaxException(this, WaebricToken.SYMBOL.SEMICOLON, 'Closing of statement');
        }
		this.setCurrentToken(this.currentToken.nextToken()); //Skip ";" ending
		this.parserStack.removeParser();
		return new CDataExpression(expression);		
	}
	
	/**
	 * Checks whether the input value is a {YieldStatement}
	 * 
	 * @param {WaebricParserToken} token The token to evaluate
	 * @return {Boolean}
	 */
	this.isYieldStatement = function(token){
		return WaebricToken.KEYWORD.YIELD.equals(token.value);
	}
	
	/**
	 * Parses the input to a {YieldStatement}
	 * 
	 * @param {WaebricParserToken} token  The token to parse
	 * @return {YieldStatement}
	 */
	this.parseYieldStatement = function(token){
		this.parserStack.addParser('YieldStatement');
		this.setCurrentToken(token);
		
		var hasValidClosing = this.currentToken.nextToken().value == WaebricToken.SYMBOL.SEMICOLON;
        if (!hasValidClosing) {
			this.setCurrentToken(this.currentToken.nextToken());
            throw new WaebricSyntaxException(this, WaebricToken.SYMBOL.SEMICOLON, 'Closing of statement');
        }
		this.setCurrentToken(this.currentToken.nextToken()); //Skip ";" ending
		this.parserStack.removeParser();
		return new YieldStatement();		
	}

	/**
	 * Checks whether the input value is a {MarkupStatement}
	 * 
	 * @param {WaebricParserToken} token The token to evaluate
	 * @return {MarkupStatement}
	 */
	this.isMarkupStatement = function(token){
        var hasValidMarkup = token.value instanceof WaebricToken.IDENTIFIER;
        if (!hasValidMarkup) {
            return false;
        }
        var tokenAfterMarkup = this.markupParser.getTokenAfterMarkup(token);
        var hasValidClosing = (tokenAfterMarkup.value == WaebricToken.SYMBOL.SEMICOLON);			
        return hasValidClosing;
    }
	
	/**
     * Parses the input to a {MarkupStatement}
     *
     * @param {WaebricParserToken} token  The token to parse
     * @return {MarkupStatement}
     */
    this.parseMarkupStatement = function(token){
        this.parserStack.addParser('MarkupStatement');
		this.setCurrentToken(token);
        
        var markup = this.markupParser.parseSingle(this);
        
		var hasValidClosing = this.currentToken.nextToken().value == WaebricToken.SYMBOL.SEMICOLON;
        if (!hasValidClosing) {
			this.setCurrentToken(this.currentToken.nextToken());
            throw new WaebricSyntaxException(this, WaebricToken.SYMBOL.SEMICOLON, 'Closing of statement');
        }
		this.setCurrentToken(this.currentToken.nextToken()); //Skip ";" ending
		this.parserStack.removeParser();
		return new MarkupStatement(markup);		
    }
	
	/**
	 * Checks whether the input value is a {MarkupMarkupStatement}
	 * 
	 * @param {WaebricParserToken} token The token to evaluate
	 * @return {MarkupMarkupStatement}
	 */
	this.isMarkupMarkupStatement = function(token){
        var hasValidMarkup = token.value instanceof WaebricToken.IDENTIFIER;
        if (!hasValidMarkup) {
            return false;
        }
        
        var tokenAfterMarkup = this.markupParser.getTokenAfterMarkup(token);
        var isMarkupStatement = tokenAfterMarkup.value == WaebricToken.SYMBOL.SEMICOLON;
        if (isMarkupStatement) {
            return false;
        }
        
        var tokenAfterMarkups = this.markupParser.getTokenAfterMarkups(tokenAfterMarkup);
		var lastMarkup = this.markupParser.getLastMarkup(token);
        var hasMarkupClosing = !this.expressionParser.isExpression(tokenAfterMarkups) 
			&& !this.embeddingParser.isStartEmbedding(tokenAfterMarkups)
			&& !this.isStartStatement(tokenAfterMarkups) 
			&& (lastMarkup != null && this.markupParser.isMarkupCall(lastMarkup));		
        return hasMarkupClosing;
    }
	
	/**
	 * Parses the input to a {MarkupEmbeddingStatement}
	 * 
	 * @param {WaebricParserToken} token The token to parse
	 * @return {MarkupEmbeddingStatement}
	 */
    this.parseMarkupMarkupStatement = function(token){
       	this.parserStack.addParser('MarkupMarkupStatement');
		this.setCurrentToken(token);
		
        var markups = this.markupParser.parseMultiple(this);
		
		var hasValidClosing = this.currentToken.nextToken().value == WaebricToken.SYMBOL.SEMICOLON;
        if (!hasValidClosing) {
			this.setCurrentToken(this.currentToken.nextToken());
            throw new WaebricSyntaxException(this, WaebricToken.SYMBOL.SEMICOLON, 'Closing of statement');
        }
		
		this.setCurrentToken(this.currentToken.nextToken()); //Skip ";" ending
		this.parserStack.removeParser();
		return new MarkupMarkupStatement(markups);		
    }
 	
	/**
	 * Checks whether the input value is a {MarkupEmbeddingStatement}
	 * @param {WaebricParserToken} token The token to evaluate
	 * @return {Boolean}
	 */
	this.isMarkupEmbeddingStatement = function(token){		
		var hasValidMarkup = token.value instanceof WaebricToken.IDENTIFIER;
        if (!hasValidMarkup) {
            return false
        };
        var tokenAfterMarkups = this.markupParser.getTokenAfterMarkups(token);
        var hasNoClosingAfterMarkups = (tokenAfterMarkups.value != WaebricToken.SYMBOL.SEMICOLON);
        var hasEmbeddingAfterMarkups = hasNoClosingAfterMarkups && this.embeddingParser.isStartEmbedding(tokenAfterMarkups);
        return hasEmbeddingAfterMarkups;
	}
	
	/**
	 * Parses the input to a {MarkupEmbeddingStatement}
	 * 
	 * @param {WaebricParserToken} token The token to parse
	 * @return {MarkupEmbeddingStatement}
	 */
	this.parseMarkupEmbeddingStatement = function(token){
        this.parserStack.addParser('MarkupEmbeddingStatement');
		this.setCurrentToken(token);
        
        var markups = this.markupParser.parseMultiple(this);
		this.setCurrentToken(this.currentToken.nextToken());
        var embedding = this.embeddingParser.parse(this);
        
        var hasValidClosing = this.currentToken.nextToken().value == WaebricToken.SYMBOL.SEMICOLON;
        if (!hasValidClosing) {
			this.setCurrentToken(this.currentToken.nextToken());
			throw new WaebricSyntaxException(this, WaebricToken.SYMBOL.SEMICOLON, 'Closing of statement');
        }
        
        this.setCurrentToken(this.currentToken.nextToken()); //Skip ";" ending
        this.parserStack.removeParser();
        return new MarkupEmbeddingStatement(markups, embedding)
    }
 	
	/**
	 * Parses the input to a {MarkupMarkupStatement}
	 * 
	 * @param {WaebricParserToken} token The token to parse
	 * @return {MarkupMarkupStatement}
	 */
	this.parseMarkupExpressionStatement = function(token){
        this.parserStack.addParser('MarkupExpressionStatement');
		this.setCurrentToken(token);
        
		var startTokenLastMarkup = this.markupParser.getLastMarkup(token);
        var markups = this.markupParser.parseMultiple(this);		

		//Make sure that Markup is processed correctly
		// p; --> Markup
		// p p; --> Markup, Variable
		// p p(); --> Markup, Markup
		var expression;
		if(this.expressionParser.isExpression(this.currentToken.nextToken())){
			this.setCurrentToken(this.currentToken.nextToken());
			expression = this.expressionParser.parse(this); 			
		}else if(startTokenLastMarkup != null && !this.markupParser.isMarkupCall(startTokenLastMarkup)){	
			markups = markups.slice(0, markups.length-1);	
			this.setCurrentToken(startTokenLastMarkup);
			expression = this.expressionParser.parse(this);
		}else{
			this.setCurrentToken(this.currentToken.nextToken());
			throw new WaebricSyntaxException(this, 'DesignatorTag', 'MarkupExpressionStatement should end with an Expression');
		}		
				
        var hasValidClosing = this.currentToken.nextToken().value == WaebricToken.SYMBOL.SEMICOLON;
        if (!hasValidClosing) {
			this.setCurrentToken(this.currentToken.nextToken());
            throw new WaebricSyntaxException(this, WaebricToken.SYMBOL.SEMICOLON, 
					'Closing of statement');
        }
        
        this.setCurrentToken(this.currentToken.nextToken()); //Skip ";" ending
        this.parserStack.removeParser();
        return new MarkupExpressionStatement(markups, expression)
    }
    
    /**
     * Checks whether the input value is a {MarkupExpressionStatement}
     * 
     * @param {WaebricParserToken} token The token to evaluate
     * @return {MarkupExpressionStatement}
     */
    this.isMarkupExpressionStatement = function(token){		
        var hasValidMarkup = token.value instanceof WaebricToken.IDENTIFIER;
        if (!hasValidMarkup) {
            return false
        };
        
        var tokenAfterMarkups = this.markupParser.getTokenAfterMarkups(token);
        var hasNoClosingAfterMarkups = (tokenAfterMarkups.value != WaebricToken.SYMBOL.SEMICOLON);
		var lastMarkup = this.markupParser.getLastMarkup(token);
        var hasExpressionAfterMarkup = hasNoClosingAfterMarkups && tokenAfterMarkups != null && this.expressionParser.isExpression(tokenAfterMarkups);
		var hasIdentifierAfterMarkup = lastMarkup != null && !this.markupParser.isMarkupCall(lastMarkup);
		
		var tokenAfterExpression = this.expressionParser.getTokenAfterExpression(tokenAfterMarkups);
		var hasValidClosingExpression = hasExpressionAfterMarkup 
			&& (tokenAfterExpression.value == WaebricToken.SYMBOL.SEMICOLON || tokenAfterExpression.value == WaebricToken.SYMBOL.DOT)
		var hasValidClosingIdentifier = hasIdentifierAfterMarkup && tokenAfterMarkups.value == WaebricToken.SYMBOL.SEMICOLON
        var result = (hasExpressionAfterMarkup && hasValidClosingExpression) || (hasValidClosingIdentifier && hasIdentifierAfterMarkup);
		
		return result
    }
    		
	/**
	 * Parses the input to a {MarkupMarkupStatement}
	 * 
	 * @param {WaebricParserToken} token The token to parse
	 * @return {MarkupMarkupStatement}
	 */
	this.parseMarkupStatementStatement = function(token){
        this.parserStack.addParser('MarkupStatementStatement');
		this.setCurrentToken(token);        
		
        var markups = this.markupParser.parseMultiple(this);
        var statement = this.parseStatement(this.currentToken.nextToken())
		this.parserStack.removeParser();
        return new MarkupStatementStatement(markups, statement)
    }
    
    /**
     * Checks whether the input value is a {MarkupExpressionStatement}
     * 
     * @param {WaebricParserToken} token The token to evaluate
     * @return {MarkupExpressionStatement}
     */
    this.isMarkupStatementStatement = function(token){		
        var hasValidMarkup = token.value instanceof WaebricToken.IDENTIFIER;
        if (!hasValidMarkup) {
            return false
        };
        
        var tokenAfterMarkups = this.markupParser.getTokenAfterMarkups(token);
        var hasNoClosingAfterMarkups = (tokenAfterMarkups.value != WaebricToken.SYMBOL.SEMICOLON);
		var hasValidClosing =  !this.expressionParser.isExpression(tokenAfterMarkups);
        return hasNoClosingAfterMarkups && hasValidClosing;
    }
     	
   	/**
	 * Parses the input to a {FunctionBinding} or {VariableBinding}
	 * 
	 * @param {WaebricParserToken} token The token to parse
	 * @return {FunctionBinding} or {VariableBinding}
	 */
	this.parseAssignment = function(token){
		this.parserStack.addParser('Assignment');
		this.setCurrentToken(token);
		
		var assignment;
		if(this.isVariableBinding(this.currentToken)){
			assignment = this.parseVariableBinding(this.currentToken);
		}else if(this.isFunctionBinding(this.currentToken)){
			assignment = this.parseFunctionBinding(this.currentToken);
		}else{			
			throw new WaebricSyntaxException(this, "FunctionAssignment or VariableAssignment", '?');
		}
		
		this.parserStack.removeParser();
		return assignment;
	}
	
	/**
	 * Parses the input to a collection of {FunctionBinding} or {VariableBinding}
	 * 
	 * @param {WaebricParserToken} token The token to parse
	 * @return {Array} Collection of {FunctionBinding, VariableBinding}
	 */
	this.parseAssignments = function(token){
		this.parserStack.addParser('Assignments');
		this.setCurrentToken(token.nextToken());

		var assignments = new Array();
		while(!WaebricToken.KEYWORD.IN.equals(this.currentToken.value)){
			var assignment = this.parseAssignment(this.currentToken)
			assignments.push(assignment);
			this.setCurrentToken(this.currentToken.nextToken());	
		}
		this.parserStack.removeParser();
		return assignments;
	}
    
	/**
	 * Checks whether the input value is a {VariableBinding}
	 * 
	 * @param {WaebricParserToken} token The token to evaluate
	 * @return {Boolean}
	 */
	this.isVariableBinding = function(token){
		var validIdentifier = this.expressionParser.isIdentifier(token);
		var validSeperator = token.nextToken().value == WaebricToken.SYMBOL.EQ;
		return validIdentifier && validSeperator;
	}
	
	/**
	 * Parses the input to a {VariableBinding}
	 * 
	 * @param {WaebricParserToken} token The token to parse
	 * @return {VariableBinding}
	 */
	this.parseVariableBinding = function(token){
		this.parserStack.addParser('VariableBinding');
		this.setCurrentToken(token);
				
		var identifier = this.currentToken.value.toString();
		this.setCurrentToken(this.currentToken.nextToken().nextToken());
		var expression = this.expressionParser.parse(this);
		
		var hasValidClosing = this.currentToken.nextToken().value == WaebricToken.SYMBOL.SEMICOLON;		
		if (!hasValidClosing) {
			this.setCurrentToken(this.currentToken.nextToken());
			throw new WaebricSyntaxException(this, WaebricToken.SYMBOL.SEMICOLON, 'Closing statement');			
		}
		this.setCurrentToken(this.currentToken.nextToken()); //Skip ";" ending
		this.parserStack.removeParser();
		return new VariableBinding(identifier, expression);
	}
	
	/**
	 * Checks whether the input value is a {FunctionBinding}
	 * 
	 * @param {WaebricParserToken} token The token to evaluate
	 * @return {Boolean}
	 */
	this.isFunctionBinding = function(token){
		var validIdentifier = this.expressionParser.isIdentifier(token);
		var validFormal = validIdentifier && this.isFormal(token.nextToken());
		return validIdentifier && validFormal;
	}
	
	/**
	 * Parses the input to a {FunctionBinding}
	 * 
	 * @param {WaebricParserToken} token The token to parse
	 * @return {FunctionBinding}
	 */
	this.parseFunctionBinding = function(token){
		this.parserStack.addParser('FunctionBinding');
		this.setCurrentToken(token);
			
		//Parse Identifier	
		var identifier = this.currentToken.value.toString();	
		var formals = this.parseFormals(this.currentToken.nextToken().nextToken());
		
		//Parse seperator
		var hasValidSeperator = this.currentToken.nextToken().value == WaebricToken.SYMBOL.EQ;		
		if(!hasValidSeperator){
			this.setCurrentToken(this.currentToken.nextToken());
			throw new WaebricSyntaxException(this, WaebricToken.SYMBOL.EQ, 'Designator/Statement seperator');	
		}
		
		//Parse Statement
		var statement = this.parseStatement(this.currentToken.nextToken().nextToken());
		
		this.parserStack.removeParser();
		return new FunctionBinding(identifier, formals, statement);
	}
	
	    
    /**
     * Checks whether the input value equals the start of a formal
	 *
     * @param {WaebricParserToken} token
     * @return {Array} A collection of {Formal}
     */
    this.isFormal = function(token){
        return (token.value == WaebricToken.SYMBOL.LEFTRBRACKET);
    }
    
    /**
     * Parses formals
     *
     * @param {WaebricParserToken} token  The token to parse
     * @return {Array} A collection of {Formal}
     */
    this.parseFormals = function(token){
        this.parserStack.addParser('Formals');
		this.setCurrentToken(token);

        var formals = new Array();
        while (this.currentToken.value != WaebricToken.SYMBOL.RIGHTRBRACKET) {
			this.parserStack.addParser('Formal');
            var hasCommaSeperator = (formals.length > 0 && this.currentToken.value == WaebricToken.SYMBOL.COMMA)
            if (hasCommaSeperator) {
                this.setCurrentToken(this.currentToken.nextToken());
            } else if (formals.length > 0) {
				throw new WaebricSyntaxException(this, WaebricToken.SYMBOL.COMMA, 'Formal seperator');	
            }
			
            if (this.expressionParser.isIdentifier(this.currentToken)) {
                formals.push(this.currentToken.value.toString());
                this.setCurrentToken(this.currentToken.nextToken());
            } else {
				throw new WaebricSyntaxException(this, 'Identifier', 'Formal');	
            }
			this.parserStack.removeParser();
        }		
		this.parserStack.removeParser();
        return formals;
    }

	/**
	 * Checks whether the input value is a statement.
	 * Only the beginning of the statement if evaluated.
	 * 
	 * The statements that start with Markup are ignored.
	 * This means that if the statement start with a Markup,
	 * then the result is false, even if it is in fact the 
	 * beginning of a statement. 
	 * 
	 * @param {WaebricParserToken} token The token to evaluata
	 * @return {Boolean}
	 */
	this.isStartStatement = function(token){
		return (WaebricToken.KEYWORD.IF.equals(token.value) || WaebricToken.KEYWORD.EACH.equals(token.value)
			|| WaebricToken.KEYWORD.LET.equals(token.value) || WaebricToken.KEYWORD.COMMENT.equals(token.value)
			|| WaebricToken.KEYWORD.ECHO.equals(token.value) || WaebricToken.KEYWORD.CDATA.equals(token.value)
			|| WaebricToken.KEYWORD.YIELD.equals(token.value) ||token.value == WaebricToken.SYMBOL.LEFTCBRACKET)
			
	}
}
WaebricStatementParser.prototype = new WaebricBaseParser();