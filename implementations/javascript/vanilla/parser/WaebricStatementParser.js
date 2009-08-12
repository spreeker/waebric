/**
 * Waebric Statement Parser
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 */
function WaebricStatementParser(){

    this.currentToken;
	
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
        var statement = this.parseStatement(parentParser.currentToken);
        parentParser.currentToken = this.currentToken;
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
        var statements = this.parseStatements(parentParser.currentToken);
        parentParser.currentToken = this.currentToken;
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
        this.currentToken = token;
        if (this.isStartIfStatement(this.currentToken)) {
            return this.parseIfElseStatement(this.currentToken);
        } else if (this.isStartEachStatement(this.currentToken)) {
            return this.parseEachStatement(this.currentToken);
        } else if (this.isStartLetStatement(this.currentToken)) {
            return this.parseLetStatement(this.currentToken);
        } else if (this.isStartBlockStatement(this.currentToken)) {
            return this.parseBlockStatement(this.currentToken);
        } else if (this.isStartCommentStatement(this.currentToken)) {
            return this.parseCommentStatement(this.currentToken);
        } else if (this.isEchoEmbeddingStatement(this.currentToken)) {
            return this.parseEchoEmbeddingStatement(this.currentToken);
        } else if (this.isEchoStatement(this.currentToken)) {
            return this.parseEchoStatement(this.currentToken);
        } else if (this.isCDataStatement(this.currentToken)) {
            return this.parseCDataStatement(this.currentToken);
        } else if (this.isYieldStatement(this.currentToken)) {
            return this.parseYieldStatement(this.currentToken);
        } else if (this.isMarkupStatement(this.currentToken)) {
            return this.parseMarkupStatement(this.currentToken)
        } else if (this.isMarkupEmbeddingStatement(this.currentToken)) {
            return this.parseMarkupEmbeddingStatement(this.currentToken)
        } else if (this.isMarkupExpressionStatement(this.currentToken)) {
            return this.parseMarkupExpressionStatement(this.currentToken)
        } else if (this.isMarkupMarkupStatement(this.currentToken)) {
            return this.parseMarkupMarkupStatement(this.currentToken)
        } else if (this.isMarkupStatementStatement(this.currentToken)) {
            return this.parseMarkupStatementStatement(this.currentToken)
        } else {
            print('Error parsing statement. Expected start of a statement but found ' + this.currentToken.nextToken().nextToken().value);
            throw new Error('Error parsing statement. Expected start of a statement')
        }
        return new Array();
    }
    
	/**
     * Parses the input a collection of Statements
     *
     * @param {WaebricParserToken} token The token to parse
     * @return {Array} A collection of statements
     */
    this.parseStatements = function(token){
        this.currentToken = token;
        
        var statements = new Array();
        while (!WaebricToken.KEYWORD.END.equals(this.currentToken.value)) {
            statement = this.parseStatement(this.currentToken);
            statements.push(statement);
            this.currentToken = this.currentToken.nextToken();
        }
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
        this.currentToken = token.nextToken(); //Skip IF keyword
        var predicate;
        var ifStatement;
        var elseStatement;
        
        //Parse predicate
        if (this.predicateParser.isStartPredicate(this.currentToken)) {
			this.currentToken = this.currentToken.nextToken();
            predicate = this.predicateParser.parse(this);
        } else {
            print('Error parsing IF/ELSE statement. Expected predicate but found ' + this.currentToken.nextToken().value);
        }
        
        //Predicate should be ended correctly
        var hasValidEnding = this.currentToken.nextToken().value == WaebricToken.SYMBOL.RIGHTRBRACKET;
        if (hasValidEnding) {
            this.currentToken = this.currentToken.nextToken();
        } else {
            print('Error parsing IF/ELSE statement. Expected ending of predicate ")" but found ' + this.currentToken.nextToken().value);
        }
        
        //Parse IF statement
        ifStatement = this.parseStatement(this.currentToken.nextToken());
        
        //Parse ELSE statement (if exists)
        var hasElseClause = WaebricToken.KEYWORD.ELSE.equals(this.currentToken.nextToken().value);
        if (hasElseClause) {
            elseStatement = this.parseStatement(this.currentToken.nextToken().nextToken());
            return new IfElseStatement(predicate, ifStatement, elseStatement);
        }
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
        this.currentToken = token;
        
        var identifier;
        var expression;
        var statement;
        
        //Parse LEFT ROUND BRACKET
        if (this.currentToken.nextToken().value == WaebricToken.SYMBOL.LEFTRBRACKET) {
            this.currentToken = this.currentToken.nextToken(); //Skip
        } else {
            print('Error parsing Each statement. Expected LEFT ROUND BRACKET but found ' + this.currentToken.value);
        }
        
        //Parse IDENTIFIER
        if (this.expressionParser.isIdentifier(this.currentToken.nextToken())) {
			this.currentToken = this.currentToken.nextToken()
            identifier = this.currentToken.value.toString();
        } else {
            print('Error parsing Each statement. Expected IDENTIFIER but found ' + this.currentToken.value);
        }
        
        //Parse COLON
        if (this.currentToken.nextToken().value == WaebricToken.SYMBOL.COLON) {
            this.currentToken = this.currentToken.nextToken(); //Skip
        } else {
            print('Error parsing Each statement. Expected COLON after identifier but found ' + this.currentToken.nextToken().value);
        }
        
        //Parse EXPRESSION
        if (this.expressionParser.isExpression(this.currentToken.nextToken())) {
			this.currentToken = this.currentToken.nextToken();
            expression = this.expressionParser.parse(this);
        } else {
            print('Error parsing Each statement. Expected EXPRESSION but found ' + this.currentToken.value);
        }
        
        //Parse RIGHT ROUND BRACKET
        if (this.currentToken.nextToken().value == WaebricToken.SYMBOL.RIGHTRBRACKET) {
            this.currentToken = this.currentToken.nextToken(); //Skip
        } else {
            print('Error parsing Each statement. Expected LEFT ROUND BRACKET but found ' + this.currentToken.value);
        }
        
        //Parse STATEMENT
        statement = this.parseStatement(this.currentToken.nextToken());
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
        this.currentToken = token;
        
        var assignments = this.parseAssignments(this.currentToken);
        var statements = this.parseStatements(this.currentToken.nextToken());
        
        var hasValidEnding = WaebricToken.KEYWORD.END.equals(this.currentToken.value);
        if (!hasValidEnding) {
            print('Error parsing LET statement. Expected end of let statement (END) but found ' + this.currentToken.value);
            throw new Error();
        } else {
            return new LetStatement(assignments, statements);
        }
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
        this.currentToken = token.nextToken(); //Skip "{" opening
        var statements = new Array();
        while (this.currentToken.value != WaebricToken.SYMBOL.RIGHTCBRACKET) {
            var statement = this.parseStatement(this.currentToken);
            statements.push(statement);
            this.currentToken = this.currentToken.nextToken();
        }
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
        this.currentToken = token.nextToken();
        
        var comment;
        if (this.isStrCon(this.currentToken)) {
            comment = this.currentToken.value.toString();
        } else {
            print('Error parsing comment. Expected StrCon after keyword COMMENT but found ' + this.currentToken.value)
        }
        
        var isValidClosing = (this.currentToken.nextToken().value == WaebricToken.SYMBOL.SEMICOLON);
        if (isValidClosing) {
            this.currentToken = this.currentToken.nextToken(); //Skip ";" end
            return new CommentStatement(comment);
        } else {
            print('Error parsing comment. Expected semicolon after comment but found ' + this.currentToken.nextToken().value)
        }
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
	 * Checks whether the input value is an {EchoStatement}
	 * 
	 * @param {WaebricParserToken} token
	 * @return {Boolean}
	 */
	this.isEchoStatement = function(token){
		var isValidOpening = WaebricToken.KEYWORD.ECHO.equals(token.value);
		var isValidExpression = isValidOpening && this.expressionParser.isExpression(token.nextToken());
		return isValidExpression;
	}
	
	/**
	 * Parses the input to an {EchoStatement}
	 * 
	 * @param {WaebricParserToken} token  The token to parse
	 * @return {EchoStatement}
	 */
	this.parseEchoStatement = function(token){
		this.currentToken = token.nextToken(); 
		
		var expression = this.expressionParser.parse(this);
		var isValidClosing = (this.currentToken.nextToken().value == WaebricToken.SYMBOL.SEMICOLON);
		if (isValidClosing) {
			this.currentToken = this.currentToken.nextToken(); //Skip ";" ending
			return new EchoStatement(expression);
		}else{
			print('Error parsing EchoStatement. Expected ";" but found ' + this.currentToken.nextToken().value)
		}	
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
		this.currentToken = token.nextToken(); 
		
		var expression = this.expressionParser.parse(this);
		
		var isValidClosing = this.currentToken.nextToken().value == WaebricToken.SYMBOL.SEMICOLON;
		if (isValidClosing) {
			this.currentToken = this.currentToken.nextToken(); //Skip ";" ending
			return new CDataExpression(expression);
		}else{
			print('Error parsing CDataStatement. Expected ";" but found ' + this.currentToken.nextToken().value)
		}
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
		this.currentToken = token;
		
		var isValidClosing = this.currentToken.nextToken().value == WaebricToken.SYMBOL.SEMICOLON;
		if (isValidClosing) {
			this.currentToken = this.currentToken.nextToken(); //Skip ";" ending
			return new YieldStatement();
		}else{
			print('Error parsing YieldStatement. Expected ";" but found ' + this.currentToken.nextToken().value)
		}
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
		this.currentToken = token.nextToken()
		var embedding = this.embeddingParser.parse(this);
		
		var hasValidClosing = this.currentToken.nextToken().value == WaebricToken.SYMBOL.SEMICOLON;
		if(!hasValidClosing){
			print('Error parsing EchoEmbeddingStatement. Expected ";" but found ' + this.currentToken.nextToken().value);			
		}		
		this.currentToken = this.currentToken.nextToken(); //Skip ";" ending
		return new EchoEmbeddingStatement(embedding);
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
        this.currentToken = token;
        
        var markup = this.markupParser.parseSingle(this);
        
        var hasValidClosing = this.currentToken.nextToken().value == WaebricToken.SYMBOL.SEMICOLON;
        if (!hasValidClosing) {
            print('Error parsing MarkupStatement. Expected ";" but found ' + this.currentToken.nextToken().value);
        }
        
        this.currentToken = this.currentToken.nextToken(); //Skip ";" ending
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
        this.currentToken = token;
        var markups = this.markupParser.parseMultiple(this);
        var hasValidClosing = this.currentToken.nextToken().value == WaebricToken.SYMBOL.SEMICOLON;
        if (hasValidClosing) {
            this.currentToken = this.currentToken.nextToken();
            return new MarkupMarkupStatement(markups);
        } else {
            print('Error parsing MarkupMarkupStatement. Expected ";" but found ' + this.currentToken.nextToken().value);
            throw new Error();
        }
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
        this.currentToken = token;
        
        var markups = this.markupParser.parseMultiple(this);
		this.currentToken = this.currentToken.nextToken();
        var embedding = this.embeddingParser.parse(this);
        
        var hasValidClosing = this.currentToken.nextToken().value == WaebricToken.SYMBOL.SEMICOLON;
        if (!hasValidClosing) {
            print('Error parsing MarkupEmbeddingStatement. Expected ";" but found ' + this.currentToken.nextToken().value);
        }
        
        this.currentToken = this.currentToken.nextToken(); //Skip ";" ending
        return new MarkupEmbeddingStatement(markups, embedding)
    }
 	
	/**
	 * Parses the input to a {MarkupMarkupStatement}
	 * 
	 * @param {WaebricParserToken} token The token to parse
	 * @return {MarkupMarkupStatement}
	 */
	this.parseMarkupExpressionStatement = function(token){
        this.currentToken = token;
        
		var startTokenLastMarkup = this.markupParser.getLastMarkup(token);
        var markups = this.markupParser.parseMultiple(this);		

		//Make sure that Markup is processed correctly
		// p; --> Markup
		// p p; --> Markup, Variable
		// p p(); --> Markup, Markup
		var expression;
		if(this.expressionParser.isExpression(this.currentToken.nextToken())){
			this.currentToken = this.currentToken.nextToken();
			expression = this.expressionParser.parse(this); 
		}else if(startTokenLastMarkup != null && !this.markupParser.isMarkupCall(startTokenLastMarkup)){	
			markups = markups.slice(0, markups.length-1);	
			this.currentToken = startTokenLastMarkup;
			expression = this.expressionParser.parse(this);//new VarExpression(startTokenLastMarkup.value);			
		}else{
			print('Error parsing MarkupExpressionStatement. Expected Expression or DesignatorTag but found ' + this.currentToken.nextToken().value);
						throw new Error();
		}
		
        var hasValidClosing = this.currentToken.nextToken().value == WaebricToken.SYMBOL.SEMICOLON;
        if (!hasValidClosing) {
            print('Error parsing MarkupExpressionStatement. Expected ";" but found ' + this.currentToken.nextToken().value);
        }
        
        this.currentToken = this.currentToken.nextToken(); //Skip ";" ending
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
		
		var hasValidClosingExpression = hasExpressionAfterMarkup && this.expressionParser.getTokenAfterExpression(tokenAfterMarkups).value == ";"
		var hasValidClosingIdentifier = hasIdentifierAfterMarkup && tokenAfterMarkups.value == ";"
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
        this.currentToken = token;
        
        var markups = this.markupParser.parseMultiple(this);
        var statement = this.parseStatement(this.currentToken.nextToken())
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
		this.currentToken = token;

		if(this.isVariableBinding(this.currentToken)){
			return this.parseVariableBinding(this.currentToken);
		}else if(this.isFunctionBinding(this.currentToken)){
			return this.parseFunctionBinding(this.currentToken);
		}else{
			print('Error parsing assignment. Expected FunctionAssignment or VariableAssignment but found ' + this.currentToken.value)
			throw new Error();
		}
	}
	
	/**
	 * Parses the input to a collection of {FunctionBinding} or {VariableBinding}
	 * 
	 * @param {WaebricParserToken} token The token to parse
	 * @return {Array} Collection of {FunctionBinding, VariableBinding}
	 */
	this.parseAssignments = function(token){
		this.currentToken = token.nextToken();

		var assignments = new Array();
		while(!WaebricToken.KEYWORD.IN.equals(this.currentToken.value)){
			var assignment = this.parseAssignment(this.currentToken)
			assignments.push(assignment);
			this.currentToken = this.currentToken.nextToken();	
		}
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
		var validExpression = validSeperator && this.expressionParser.isExpression(token.nextToken().nextToken());
		return validIdentifier && validSeperator && validExpression;
	}
	
	/**
	 * Parses the input to a {VariableBinding}
	 * 
	 * @param {WaebricParserToken} token The token to parse
	 * @return {VariableBinding}
	 */
	this.parseVariableBinding = function(token){
		this.currentToken = token;
				
		var identifier = this.currentToken.value.toString();
		this.currentToken = this.currentToken.nextToken().nextToken();
		var expression = this.expressionParser.parse(this);
		var isValidEnding = this.currentToken.nextToken().value == WaebricToken.SYMBOL.SEMICOLON;
		
		if (isValidEnding) {
			this.currentToken = this.currentToken.nextToken(); //Skip ";" ending
			return new VariableBinding(identifier, expression);
		} else {
			print('Error parsing Variable Assignment. Expected end of statement ";" but found ' +  this.currentToken.nextToken().value)
		}
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
		this.currentToken = token;
				
		var identifier = this.currentToken.value.toString();	
		var formals = this.parseFormals(this.currentToken.nextToken().nextToken());
		
		var hasValidSeperator = this.currentToken.nextToken().value == WaebricToken.SYMBOL.EQ;		
		if(hasValidSeperator){
			var statement = this.parseStatement(this.currentToken.nextToken().nextToken());
			return new FunctionBinding(identifier, formals, statement);
		}else{
			print('Error parsing Function Assignment. Expected FunctionAssignment but found ' + this.currentToken.nextToken().value)
		}
	}
	
	    
    /**
     * Checks whether the input value equals the start of a formal
	 *
     * @param {WaebricParserToken} token
     * @return {Array} A collection of {Formal}
     */
    this.isFormal = function(token){
		var tempToken = token;
		
		//Validate opening formal
        var isValidOpening = (tempToken.value == WaebricToken.SYMBOL.LEFTRBRACKET);		
		if(!isValidOpening){
			
			return false;
		}
		
		//Validate content and ending formal	
		tempToken = tempToken.nextToken();
		while (tempToken.value != WaebricToken.SYMBOL.RIGHTRBRACKET) {	
			if(!this.expressionParser.isIdentifier(tempToken)){	
				return false;
			}else if(tempToken.nextToken().value != ',' && tempToken.nextToken().value != ')'){
				return false;
			}else if( tempToken.nextToken().value == ')'){
				tempToken = tempToken.nextToken();
			}else{
				tempToken = tempToken.nextToken().nextToken()
			}
        }		
		
		return true;
    }
    
    /**
     * Parses formals
     *
     * @param {WaebricParserToken} token  The token to parse
     * @return {Array} A collection of {Formal}
     */
    this.parseFormals = function(token){
        this.currentToken = token;
        
        var formals = new Array();
        while (this.currentToken.value != WaebricToken.SYMBOL.RIGHTRBRACKET) {
            var isCommaSeperator = (formals.length > 0 && this.currentToken.value == WaebricToken.SYMBOL.COMMA)
            if (isCommaSeperator) {
                this.currentToken = this.currentToken.nextToken();
            } else if (formals.length > 0) {
                print('Error parsing Formals. Expected COMMA after previous formal but found ' + this.currentToken.value);
            }
            
            if (this.expressionParser.isIdentifier(this.currentToken)) {
                formals.push(this.currentToken.value.toString());
                this.currentToken = this.currentToken.nextToken();
            } else {
                print('Error parsing Formals. Expected formal but found ' + this.currentToken.value);
            }
        }
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
