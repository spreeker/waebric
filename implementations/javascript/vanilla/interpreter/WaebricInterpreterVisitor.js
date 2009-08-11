function WaebricInterpreterVisitor(){	
	
	/**
	 * Returns a module visitor
	 */
	this.getModuleVisitor = function(env, dom){		
		return new ModuleVisitor(env, dom);
	}
	
	/**
	 * Returns a main visitor
	 */
	this.getMainVisitor = function(env, dom){		
		return new MainVisitor(env, dom);
	}
	
	/**
	 * Returns a mapping visitor
	 */
	this.getMappingVisitor = function(env, dom){		
		return new MappingVisitor(env, dom);
	}
	
	/**
	 * Visitor for main function
	 * 
	 */
	function MainVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(module){			
			//Get main function and output its statements			
			var mainFunction = this.env.getLocalFunction('main');
			if (mainFunction != null) {
				//Set path for output location
				this.env.path = module.moduleId.identifier + '.htm';
				
				//Output main function
				this.dom.createXHTMLRoot();
				mainFunction.accept(new FunctionDefinitionVisitor(this.env, this.dom));
			}else{
				print('No main function found!')
			}
		}
	}
	
	/**
	 * Visitor for mappings
	 * 
	 */
	function MappingVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(mapping){
			//Set path for output location
			this.env.path = mapping.path;
			
			//Output mapping markup			
			this.dom.createXHTMLRoot();		
			mapping.markup.accept(new MarkupVisitor(this.env, this.dom));
		}
	}
	
	/**
	 * Visitor for Module
	 * 
	 * Preprocessor
	 */
	function ModuleVisitor(env, dom){	
		this.env = env;
		this.dom = dom;	
		this.visit = function(module){					
			//Assign name to environment
			this.env.name = module.moduleId.identifier;			
			
			//Store Function Definitions dependencies
			//Preprocessing: should be done before the FunctionDefinitionVisitor is called
			for (var i = 0; i < module.dependencies.length; i++) {
				var dependency = module.dependencies[i];
				dependency.accept(new DependencyVisitor(this.env, this.dom));
			}
			
			//Store Function Definitions local module
			//Preprocessing: should be done before the FunctionDefinitionVisitor is called
			for (var i = 0; i < module.functionDefinitions.length; i++) {
				var functionDefinition = module.functionDefinitions[i];
				if (!this.env.containsFunction(functionDefinition.functionName)) {
					this.env.addFunction(functionDefinition);
				}
			}
		}
	}	
	
	/**
	 * Visitor for dependencies (imports)
	 */
	function DependencyVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(dependency){
			//Visit only unprocessed dependencies
			var dependencyName = dependency.moduleId.identifier;
			var existingDependency = this.env.getDependency(dependencyName);

			//If dependency is not processed before, visit it
			if (existingDependency == null) {
				var new_env = this.env.addDependency('module');
				new_env.name = dependency.moduleId.identifier;
				dependency.accept(new ModuleVisitor(new_env));
			}    //If dependency was processed before, add existing dependecy to
			//current environment but do not visit it again.
			else {
				this.env.addExistingDependency(existingDependency);
			}
		}
	}
	
	/**
	 * Visitor for FunctionDefinition
	 *
	 */
	function FunctionDefinitionVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(functionDefinition){
			//Add Arguments of function to new environment	
			var new_env;
			if (!functionDefinition.isFunctionBinding) {
				new_env = this.env.addEnvironment('func-def');
			}else{
				new_env = this.env.addEnvironment('func-bind')
			}
			
			//Process formals
			for (var i = 0; i < functionDefinition.formals.length; i++){
				//Get variable name from arguments 				
				var variableName = functionDefinition.formals[i];
				
				//Retrieve variable value from parent environment (markup call)
				//The value of the first function argument equals the first argument
				//in the function call (markup call)
				var variableValue;
				if (this.env.variables.length > i) {
					 variableValue = this.env.variables[i].value;
				}else{
					variableValue = 'undef';
				}
				
				//Save variable
				new_env.addVariable(variableName, variableValue);
			}
			
			//Visit Statements
			for (var i = 0; i < functionDefinition.statements.length; i++) {
				var statement = functionDefinition.statements[i];
				statement.accept(new StatementVisitor(new_env, this.dom));
			}
		}
	}
	
	/**
	 * Visitor for Statement
	 */
	function StatementVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(statement){
			//Remember last element to be set back after statement is processed
			var lastElement = this.dom.lastElement;			
			
			if (statement instanceof IfStatement) {
				statement.accept(new IfStatementVisitor(this.env, this.dom));
			} else if (statement instanceof IfElseStatement) {
				statement.accept(new IfElseStatementVisitor(this.env, this.dom));
			} else if (statement instanceof EachStatement) {
				statement.accept(new EachStatementVisitor(this.env, this.dom));
			} else if (statement instanceof LetStatement) {
				statement.accept(new LetStatementVisitor(this.env, this.dom));
			} else if (statement instanceof BlockStatement) {
				statement.accept(new BlockStatementVisitor(this.env, this.dom));
			} else if (statement instanceof CommentStatement) {
				statement.accept(new CommentStatementVisitor(this.env, this.dom));
			} else if (statement instanceof EchoStatement) {
				statement.accept(new EchoStatementVisitor(this.env, this.dom));
			} else if (statement instanceof EchoEmbeddingStatement) {
				statement.accept(new EchoEmbeddingVisitor(this.env, this.dom));
			} else if (statement instanceof CDataExpression) {
				statement.accept(new CDataExpressionVisitor(this.env, this.dom));
			} else if (statement instanceof YieldStatement) {
				statement.accept(new YieldStatementVisitor(this.env, this.dom));
			} else if (statement instanceof MarkupStatement) {
				statement.accept(new MarkupStatementVisitor(this.env, this.dom));
			} else if (statement instanceof MarkupMarkupStatement) {
				statement.accept(new MarkupMarkupStatementVisitor(this.env, this.dom));
			} else if (statement instanceof MarkupEmbeddingStatement) {
				statement.accept(new MarkupEmbeddingStatementVisitor(this.env, this.dom));
			} else if (statement instanceof MarkupStatementStatement) {
				statement.accept(new MarkupStatementStatementVisitor(this.env, this.dom));
			} else if (statement instanceof MarkupExpressionStatement) {
				statement.accept(new MarkupExpressionStatementVisitor(this.env, this.dom));
			} else { //Statement is not recognizederror.");
				print('unrecognized statement')
			}
						
			//After statement is processed, the tags should be closed.
			this.dom.lastElement = lastElement;
		}
	}
	
	function isValidExpression(expression, env){
		if(expression instanceof FieldExpression){
			return isValidFieldExpression(expression, env);
		}else if(expression instanceof CatExpression){
			return isValidCatExpression(expression, env)
		}else if(expression instanceof AndPredicate){
			return isValidExpression(expression.predicateLeft, env) && isValidExpression(expression.predicateRight, env);
		}else if(expression instanceof OrPredicate){
			return isValidExpression(expression.predicateLeft, env) || isValidExpression(expression.predicateRight, env);	
		}else if(expression instanceof NotPredicate){
			return !isValidExpression(expression.predicate, env)
		}else if(expression instanceof IsAPredicate){
			return getExpressionValue(expression.expression, env) instanceof expression.type.type;
		}else{//Return true for text, symbols, natcons and concat
			return true;
		}
	}
	
	function isValidFieldExpression(expression, env){
		return getFieldExpressionValue(expression, env) != null;
	}
	
	function isValidCatExpression(expression, env){
		var isValidLeftExpression = isValidExpression(expression.expressionLeft, env);
		var isValidRightExpression = isValidExpression(expression.expressionRight, env);
		return isValidLeftExpression && isValidRightExpression;
	}
	
	function getExpressionValue(expression, env){
		if (expression instanceof FieldExpression) {
			return getFieldExpressionValue(expression, env);
		}else if(expression instanceof VarExpression){
			return env.getVariable(expression).value;
		}else if(expression instanceof RecordExpression){
			return getRecordExpressionValue(expression, env);
		}else if(expression instanceof ListExpression){
			return getListExpressionValue(expression, env);
		}else{
			return expression;
		}
	}
	
	function getFieldExpressionValue(expression, env){		
		//Store fields in array
		var fields = new Array();
		var expr = expression;
		while(expr instanceof FieldExpression){
			fields.push(expr.field);
			expr = expr.expression;
		}
		fields = fields.reverse();
		
		//Get root variable value
		var data = getFieldExpressionRootValue(expression, env);
		
		//Data should be record expression or a variable
		if (!(data instanceof RecordExpression)) {
			return null;
		}
		
		//Get data by fields in array
		for(var i = 0; i < fields.length; i++){
			//Get field data
			var field = fields[i];	
			var fieldValue = data.getValue(field);
			
			//Get incapsulated data
			data = getExpressionValue(fieldValue, env);
		}
		return data;
	}
	
	function getFieldExpressionRootValue(expression, env){
		var root = expression;
		while (root instanceof FieldExpression) {
			root = root.expression;
		}
		
		//Accept variables or record expressions as value
		if(root instanceof VarExpression){		
			return env.getVariable(root).value;
		}else if (root instanceof RecordExpression) {
			return root;
		}else {
			return null;
		}
	}
	
	function getRecordExpressionValue(recordExpr, env){
		for (var i = 0; i < recordExpr.records.length; i++) {
			var keyValuePair = recordExpr.records[i];
			recordExpr.records[i].value = getExpressionValue(keyValuePair.value, env);
		}
		return recordExpr;
	}
	
	function getListExpressionValue(listExpr, env){
		for (var i = 0; i < listExpr.list.length; i++) {
			var item = listExpr.list[i];
			listExpr.list[i] = getExpressionValue(item, env);			
		}
		
		return listExpr;
	}
		
	function getRecordExpressionStringValue(recordExpr, env){
		var records = '{'
		for (var i = 0; i < recordExpr.records.length; i++) {
			var keyValuePair = recordExpr.records[i];
			records = records.concat(keyValuePair.key).concat(':').concat(getExpressionValue(keyValuePair.value)).concat(',');
		}
		return records.substr(0, records.length - 1).concat('}')
	}
		
	function getListExpressionStringValue(listExpr, env){
		var list = '[';
		for (var i = 0; i < listExpr.list.length; i++) {
			var expression = listExpr.list[i];			
			list = list.concat(getExpressionValue(expression)).concat(',');
		}
		return list.substr(0, list.length - 1).concat(']')
	}
			
	/**
	 * Visitor IfStatement
	 */
	function IfStatementVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(ifStmt){	
			if (isValidExpression(ifStmt.predicate, this.env)) {
				ifStmt.ifStatement.accept(new StatementVisitor(this.env, this.dom))
			}
		}
	}
		
	/**
	 * Visitor IfElseStatement
	 */
	function IfElseStatementVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(ifElseStmt){			
			if (isValidExpression(ifElseStmt.predicate, this.env)) {
				ifElseStmt.ifStatement.accept(new StatementVisitor(this.env, this.dom));
			}else{
				ifElseStmt.elseStatement.accept(new StatementVisitor(this.env, this.dom));
			}			
		}
	}
	
	/**
	 * Visitor Each Statement
	 */
	function EachStatementVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(eachStmt){
			//Add new environment because the scope of the variable declaration (identifier)
			// is limited to the statement inside the each statement.
			var new_env = this.env.addEnvironment('each-stmt');
			
			//Add variable to new environment of each statement
			new_env.addVariable(eachStmt.identifier);
			
			//Get expression value
			var expressionValue = getExpressionValue(eachStmt.expression, this.env);
			
			//Expression value should be an array
			var lastElement = this.dom.lastElement;
			if(expressionValue instanceof ListExpression){				
				//Loop all records and output statement
				for(var i = 0; i < expressionValue.list.length; i++){					
					var listValue = expressionValue.list[i];			
					//Save variable to environment
					new_env.addVariable(eachStmt.identifier, listValue);	
					//Visit statement				
					eachStmt.statement.accept(new StatementVisitor(new_env, this.dom));
					//Set last element back to original (otherwise nesting)
					this.dom.lastElement = lastElement;
				}				
			}
		}
	}
		
	/**
	 * Visitor Let Statement
	 */
	function LetStatementVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(letStmt){
			//Assignments and statements in a let statement require a new environment under
			//the current environment since the scope of the assignments (function/variable declaration)
			// is limited to the statements inside the let statement
			var new_env = this.env;
			
			//Visit Assignments
			for (var i = 0; i < letStmt.assignments.length; i++) {
				new_env = new_env.addEnvironment('let-stmt');
				var assignment = letStmt.assignments[i];
				assignment.accept(new AssignmentVisitor(new_env, this.dom));
			}
			
			//Visit Statements
			var lastElement = this.dom.lastElement;
			for (var j = 0; j < letStmt.statements.length; j++) {
				var statement = letStmt.statements[j];
				statement.accept(new StatementVisitor(new_env, this.dom));
				this.dom.lastElement = lastElement; //Prevent nested nodes
			}
		}
	}

	/**
	 * Visitor Block Statement
	 */
	function BlockStatementVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(blockStmt){
			//Visit statements
			for (var i = 0; i < blockStmt.statements.length; i++) {
				var statement = blockStmt.statements[i];
				statement.accept(new StatementVisitor(this.env, this.dom));
			}
		}
	}
	
	/**
	 * Visitor Comment Statement
	 */
	function CommentStatementVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(commentStmt){
			//Create comment
			var comment = this.dom.document.createComment(commentStmt.comment.toString());
			this.dom.lastElement.appendChild(comment);
		}
	}

	/**
	 * Visitor Echo Statement
	 */
	function EchoStatementVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(echoStmt){
			//Visit Expression	
			echoStmt.expression.accept(new ExpressionVisitor(this.env, this.dom));
			
			//Create textnode
			var text = this.dom.document.createTextNode(normalize(this.dom.lastValue));			
			this.dom.lastElement.appendChild(text);
		}
	}
	
	/**
	 * Visitor for Echo Embedding
	 */
	function EchoEmbeddingVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(echoEmbeddingStmt){
			//Visit Embedding
			echoEmbeddingStmt.embedding.accept(new EmbeddingVisitor(this.env, this.dom));
		}
	}
	
	/**
	 * Visitor for Yield
	 */
	function YieldStatementVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(yieldStmt){
			//Visit yield markup
			var lastYield = this.dom.getLastYield();

			if (lastYield != null) {	
				lastYield.value.accept(new StatementVisitor(lastYield.env, this.dom));
			}else{
				throw new Error('Yield field is empty!');
			}
		}
	}

	/**
	 * Visitor for CData
	 */
	function CDataExpressionVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(cdataExpr){			
			//Visit Expression
			cdataExpr.expression.accept(new ExpressionVisitor(this.env, this.dom));
			
			//Create CDATA element
			var cdata = this.dom.document.createCDATASection(this.dom.lastValue.toString());
			this.dom.lastElement.appendChild(cdata);
		}
	}
	
	/**
	 * Visitor Markup Statement
	 */
	function MarkupStatementVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(markupStmt){
			//Visit Markup
			markupStmt.markup.accept(new MarkupVisitor(this.env, this.dom));
		}
	}
	
	/**
	 * Constructs a new Markup Statement
	 * 
	 * Utility function for the YIELD statement workaround.
	 * If a MarkupCall is found in a Statement, then the remaining 
	 * items that follow the MarkupCall are ignored and not processed.
	 * The remaining items are reconstructed into a new statement
	 * which is processed when a YIELD Statement is found. This function
	 * reconstructs the statement based on the head (= markups) and the 
	 * tail (= Empty, Embedding, Expression, statement). 
	 * 
	 * @param {Object} markups
	 * @param {Object} tail
	 */
	function constructNewMarkupStatement(markups, tail){
		if(tail == null && markups.length == 1){
			return new MarkupStatement(markups[0]);
		}else if(tail == null && markups.length > 1){
			return new MarkupMarkupStatement(markups);
		}else if(tail instanceof Embedding){
			return new MarkupEmbeddingStatement(markups, tail);
		}else if(tail instanceof VarExpression){
			return new MarkupExpressionStatement(markups, tail);
		}else if(tail instanceof TextExpression){
			return new MarkupExpressionStatement(markups, tail);
		}else if(tail instanceof SymbolExpression){
			return new MarkupExpressionStatement(markups, tail);
		}else if(tail instanceof NatExpression){
			return new MarkupExpressionStatement(markups, tail);
		}else if(tail instanceof FieldExpression){
			return new MarkupExpressionStatement(markups, tail);
		}else if(tail instanceof CatExpression){
			return new MarkupExpressionStatement(markups, tail);
		}else if(tail instanceof ListExpression){
			return new MarkupExpressionStatement(markups, tail);
		}else if(tail instanceof RecordExpression){
			return new MarkupExpressionStatement(markups, tail);
		}else{
			return new MarkupStatementStatement(markups, tail);
		}
	}
	
	/**
	 * Checks whether the requested markup is a function call to a valid function;
	 * 
	 * @param {Object} markup
	 * @param {Object} env
	 */
	function isValidMarkupCall(markup, env){
		if(env.type == 'func-bind'){
			env = env.parent.parent.parent;	
		}
		
		if (markup instanceof MarkupCall) {
			return env.containsLocalFunction(markup.designator.idCon);
		}else if(markup instanceof DesignatorTag){
			return env.containsLocalFunction(markup.idCon);
		}
	}
	
	/**
	 * Visitor MarkupMarkup Statement
	 */
	function MarkupMarkupStatementVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(markupMarkupStmt){
			//Visit Markups
			for (var i = 0; i < markupMarkupStmt.markups.length; i++) {
				var markup = markupMarkupStmt.markups[i];					
				//If a MarkupCall is found, then the remaining markups/statements are
				//intended for the YIELD statement 			
				isLastMarkup = (i == markupMarkupStmt.markups.length-1); //Last markup has no remaining yield value
				if(isValidMarkupCall(markup, this.env) && !isLastMarkup){
					var newMarkupStmt = constructNewMarkupStatement(markupMarkupStmt.markups.slice(i+1));					
					if (newMarkupStmt) {
						this.dom.addYield(newMarkupStmt, this.env);
					}
					markup.accept(new MarkupVisitor(this.env, this.dom));				
					break;
				}else{
					markup.accept(new MarkupVisitor(this.env, this.dom));
				}
			}
		}
	}
	
	/**
	 * Visitor MarkupEmbedding Statement
	 */
	function MarkupEmbeddingStatementVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(markupEmbeddingStmt){
			//Visit Markups
			var markup;
			for (var i = 0; i < markupEmbeddingStmt.markups.length; i++) {
				markup = markupEmbeddingStmt.markups[i];								
				//If a MarkupCall is found, then the remaining markups are
				//intended for the YIELD statement 											
				if(isValidMarkupCall(markup, this.env)){	
					var newMarkupStmt = constructNewMarkupStatement(markupEmbeddingStmt.markups.slice(i+1), markupEmbeddingStmt.embedding);
					if (newMarkupStmt) {
						this.dom.addYield(newMarkupStmt, this.env);
					}
					markup.accept(new MarkupVisitor(this.env, this.dom));				
					return;
				}else{
					markup.accept(new MarkupVisitor(this.env, this.dom));
				}				
			}
			
			//Visit Embedding
			markupEmbeddingStmt.embedding.accept(new EmbeddingVisitor(this.env, this.dom));
			
		}
	}
	
	/**
	 * Visitor MarkupStatement Statement
	 */
	function MarkupStatementStatementVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(markupStmtStmt){
			//Visit Markups
			for (var i = 0; i < markupStmtStmt.markups.length; i++) {
				var markup = markupStmtStmt.markups[i];
				//If a MarkupCall is found, then the remaining markups are
				//intended for the YIELD statement
				if(isValidMarkupCall(markup, this.env)){
					var newMarkupStmt = constructNewMarkupStatement(markupStmtStmt.markups.slice(i+1), markupStmtStmt.statement);
					if(newMarkupStmt){
						this.dom.addYield(newMarkupStmt, this.env);
					}
					markup.accept(new MarkupVisitor(this.env, this.dom));				
					return;
				}else{
					markup.accept(new MarkupVisitor(this.env, this.dom));
				}		
			}
			
			//Visit Statement
			markupStmtStmt.statement.accept(new StatementVisitor(this.env, this.dom));
		}
	}
		
	/**
	 * Visitor MarkupExpression Statement
	 */
	function MarkupExpressionStatementVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(markupExprStmt){
			//Visit Markups
			for (var i = 0; i < markupExprStmt.markups.length; i++) {
				var markup = markupExprStmt.markups[i];
				//If a MarkupCall is found, then the remaining markups are
				//intended for the YIELD statement 			
				if(isValidMarkupCall(markup, this.env)){					
				    var newMarkupStmt = constructNewMarkupStatement(markupExprStmt.markups.slice(i+1), markupExprStmt.expression);
					if(newMarkupStmt){
						this.dom.addYield(newMarkupStmt, this.env);
					}					
					markup.accept(new MarkupVisitor(this.env, this.dom));	
					return;
				}else{
					markup.accept(new MarkupVisitor(this.env, this.dom));
				}		
			}
			
			//Visit Expression
			markupExprStmt.expression.accept(new ExpressionVisitor(this.env, this.dom));
			
			//Print out HTML from expression
			var text = this.dom.document.createTextNode(normalize(this.dom.lastValue));	
			this.dom.lastElement.appendChild(text);
		}
	}
	
	/**
	 * Visitor Expression
	 */
	function ExpressionVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(expression){
			if (expression instanceof VarExpression) {
				expression.accept(new VarExpressionVisitor(this.env, this.dom));
			} else if (expression instanceof SymbolExpression) {
				expression.accept(new SymbolExpressionVisitor(this.env, this.dom));	
			} else if (expression instanceof NatExpression) {
				expression.accept(new NatExpressionVisitor(this.env, this.dom));
			} else if (expression instanceof TextExpression) {
				expression.accept(new TextExpressionVisitor(this.env, this.dom));
			} else if (expression instanceof FieldExpression) {
				expression.accept(new FieldExpressionVisitor(this.env, this.dom));
			} else if (expression instanceof CatExpression) {
				expression.accept(new CatExpressionVisitor(this.env, this.dom));
			} else if (expression instanceof ListExpression) {
				expression.accept(new ListExpressionVisitor(this.env, this.dom));
			} else if (expression instanceof RecordExpression) {
				expression.accept(new RecordExpressionVisitor(this.env, this.dom));
			} else { //Expression is not recognized
				print("Unrecognized Expression found. Parser should have thrown an error.");
			}
		}
	}	
		
	/**
	 * Visitor Variable Expression
	 */
	function VarExpressionVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(variable){
			var _var = this.env.getVariable(variable);	
			if (_var != null) {
				this.dom.lastValue = _var.value;
			}else{
				this.dom.lastValue = 'undef';				
			}
		}
	}
	
	/**
	 * Visitor Symbol Expression
	 */
	function SymbolExpressionVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(symbol){
			//Save value temporary
			this.dom.lastValue = symbol;
		}
	}
	
	/**
	 * Visitor Variable Expression
	 */
	function NatExpressionVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(natural){
			//Save value temporary
			this.dom.lastValue = natural;
		}
	}
	
	/**
	 * Visitor Text Expression
	 */
	function TextExpressionVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(text){
			//Save value temporary
			this.dom.lastValue = normalize(text);
		}
	}
	
	/**
	 * Visitor Field Expression
	 */
	function FieldExpressionVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(fieldExpr){
			this.dom.lastValue = getFieldExpressionValue(fieldExpr, this.env);
		}
	}	
			
	/**
	 * Visitor Category Expression
	 */
	function CatExpressionVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(catExpr){
			var cat = '';
						
			//Visit Expression Left			
			catExpr.expressionLeft.accept(new ExpressionVisitor(this.env, this.dom));
			cat += this.dom.lastValue;
			
			//Visit Expression Right
			catExpr.expressionRight.accept(new ExpressionVisitor(this.env, this.dom));
			cat += this.dom.lastValue;
			
			this.dom.lastValue = cat;
		}
	}	
	
	/**
	 * Visitor List Expression
	 * 
	 * [value1, value2, value3, ...]
	 */
	function ListExpressionVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(listExpr){			
			this.dom.lastValue = getListExpressionStringValue(listExpr, this.env);
		}
	}
	
	/**
	 * Visitor Record Expression
	 * 
	 * {key1  value1, key2  value2};'
	 */
	function RecordExpressionVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(recordExpr){			
			this.dom.lastValue = getRecordExpressionValue(recordExpr, this.env);
		}
	}
		
	/**
	 * Visitor for Embedding
	 */
	function EmbeddingVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(embedding){
			//Add PreText as normal text to document
			if (normalize(embedding.head) != "") {
				var text = this.dom.document.createTextNode(normalize(embedding.head));
				this.dom.lastElement.appendChild(text);
			}
				
			//Prepare swap	
			var lastElement = this.dom.lastElement;
			
			//Visit Embed
			embedding.embed.accept(new EmbedVisitor(this.env, this.dom));
					
			//Swap last element	(prevent adding the tail to the embed section)
			this.dom.lastElement = lastElement;				
				
			//Visit Tail		
			embedding.tail.accept(new TextTailVisitor(this.env, this.dom));
		}
	}
	
	/**
	 * Visitor Embed
	 */
	function EmbedVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(embed){
			if (embed instanceof ExpressionEmbedding) {
				embed.accept(new ExpressionEmbeddingVisitor(this.env, this.dom));
			} else if (embed instanceof MarkupEmbedding) {
				embed.accept(new MarkupEmbeddingVisitor(this.env, this.dom));
			} else { //Embed is not recognized
				print("Unrecognized Embed found. Parser should have thrown an error.");
			}
		}
	}

	/**
	 * Visitor for TextTail
	 */
	function TextTailVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(textTail){
			if (textTail instanceof MidTextTail) {
				textTail.accept(new MidTextTailVisitor(this.env, this.dom));
			} else if (textTail instanceof PostTextTail) {
				textTail.accept(new PostTextTailVisitor(this.env, this.dom))
			} else { //TextTail is not recognized
				print("Unrecognized TextTail found. Parser should have thrown an error.");
			}
		}
	}
	
	/**
	 * Visitor for Mid Text Tail
	 */
	function MidTextTailVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(midTextTail){				
			//Add mid text as normal text to document
			if ((normalize(midTextTail.mid)) != "") {
				var text = this.dom.document.createTextNode((normalize(midTextTail.mid)));
				this.dom.lastElement.appendChild(text);
			}
			
			//Prepare swap
			this.lastElement = this.dom.lastElement;
					
			//Visit Embed
			midTextTail.embed.accept(new EmbedVisitor(this.env, this.dom));
			
			//Swap last Element (prevent adding the tail to the embed section)
			this.dom.lastElement = this.lastElement;
			
			//Visit Tail
			midTextTail.tail.accept(new TextTailVisitor(this.env, this.dom));
		}
	}
	
	function normalize(str){
		str = str.toString().replace(/\n/g,' ');
		str = str.toString().replace(/[\r\t\\]/g,'');
		return str;
	}
	
	/**
	 * Visitor for Post Text Tail
	 */
	function PostTextTailVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(postText){	
			//Add mid post text to document			
			if ((normalize(postText.text)) != "") {
				var text = this.dom.document.createTextNode((normalize(postText.text)));
				this.dom.lastElement.appendChild(text);
			}	
		}
	}
			
	/**
	 * Visitor ExpressionEmbedding
	 */
	function ExpressionEmbeddingVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(exprEmbed){
			//Visit Markups	
			for (var i = 0; i < exprEmbed.markups.length; i++) {
				var markup = exprEmbed.markups[i];
				markup.accept(new MarkupVisitor(this.env, this.dom));
			}
			
			//Visit expression
			exprEmbed.expression.accept(new ExpressionVisitor(this.env, this.dom));
			
			//Add expression value to document			
			var text = this.dom.document.createTextNode(normalize(this.dom.lastValue));
			this.dom.lastElement.appendChild(text);
		}
	}
	
	/**
	 * Visitor MarkupEmbedding
	 */
	function MarkupEmbeddingVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(markupEmbed){
			//Visit Markups
			for (var i = 0; i < markupEmbed.markups.length; i++) {
				var markup = markupEmbed.markups[i];
				markup.accept(new MarkupVisitor(this.env, this.dom));
			}
		}
	}
			
	/**
	 * Visitor Assignment
	 */
	function AssignmentVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(assignment){
			if (assignment instanceof VariableBinding) {
				assignment.accept(new VariableBindingVisitor(this.env, this.dom));
			} else if (assignment instanceof FunctionBinding) {
				assignment.accept(new FunctionBindingVisitor(this.env, this.dom));
			} else { //Assignment is not recognized
				print("Unrecognized Assignment found. Parser should have thrown an error.");
			}
		}
	}

	/**
	 * Visitor Variablebinding
	 */
	function VariableBindingVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(varbind){
			//Add variable to current environment (let statement)
			this.env.addVariable(varbind.variable, getExpressionValue(varbind.expression));
			
			//Visit expression
			varbind.expression.accept(new ExpressionVisitor(this.env, this.dom));
		}
	}
	
	/**
	 * Visitor Functionbinding
	 */
	function FunctionBindingVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(funcbind){
			//Convert function binding into function definition
			var newFunctionName = funcbind.variable;
			var newFunctionFormals = funcbind.formals;
			var newFunctionStatements = [funcbind.statement];
			var newFunction = new FunctionDefinition(newFunctionName, newFunctionFormals, newFunctionStatements, true);
						
			//Add function to current environment (let statement)
			this.env.addFunction(newFunction);
		}
	}

	/**
	 * Visitor Markup
	 */
	function MarkupVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(markup){
			if (markup instanceof MarkupCall){
				//Call to function should exist, otherwise it's a tag
				if (this.env.containsLocalFunction(markup.designator.idCon)) {
					markup.accept(new MarkupCallVisitor(this.env, this.dom))
				}else{
					markup.accept(new MarkupXHTMLTagVisitor(this.env, this.dom))
				}
			}else{
				//Tag should not be a reference to a function, 
				//otherwise, it is processed as a MarkupCall
				if(!this.env.containsLocalFunction(markup.idCon)){
					markup.accept(new MarkupTagVisitor(this.env, this.dom))
				}else{
					//Convert to Markup Call
					markup = new MarkupCall(markup, [])
					markup.accept(new MarkupCallVisitor(this.env, this.dom))
				}
			}
		}
	}

	/**
	 * Visitor Markup Calls
	 */
	function MarkupCallVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(markup){			
			var new_env;
			var functionDefinition;
			
			//Check if function already exists in current environment (incl dependecies)
			if (this.env.type == 'func-bind') {	
				functionDefinition = this.env.parent.parent.parent.getLocalFunction(markup.designator.idCon);
				new_env = this.env.parent.parent;
			} else {
				functionDefinition = this.env.getLocalFunction(markup.designator.idCon);
				new_env = this.env.addEnvironment('markup-call');	
			}
						
			//Store variables
			for(var i = 0; i < markup.arguments.length; i++){
				var variableValue = getExpressionValue(markup.arguments[i], this.env);	
				new_env.addVariable('arg' + i, variableValue);
			}	

			//Visit function definition
			if (functionDefinition != null) {
				functionDefinition.accept(new FunctionDefinitionVisitor(new_env, this.dom));
			}else{
				markup.accept(new MarkupXHTMLTagVisitor(this.env, this.dom))
			}	
		}
	}
	
	/**
	 * Visitor Markup XHTML Tag
	 */
	function MarkupXHTMLTagVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(markup){	
			//Add tag/element to document (ignore html tags)
			if (markup.designator.idCon != 'html') {
				var element = this.dom.document.createElement(markup.designator.idCon.toString());
				this.dom.lastElement.appendChild(element);
				this.dom.lastElement = element;
			}			
			
			//Visit short-hand arguments of tag/element
			for(var i = 0; i < markup.designator.attributes.length; i++){
				var attribute = markup.designator.attributes[i];
				attribute.accept(new AttributeVisitor(this.env, this.dom));
			}
			
			//Visit arguments/formals
			for (var i = 0; i < markup.arguments.length; i++) {
				var argument = markup.arguments[i];
				argument.accept(new ArgumentExpressionVisitor(this.env, this.dom));
			}
		}
	}
	
	/**
	 * Visitor Markup Tags
	 */
	function MarkupTagVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(markup){
			//Add tag/element to document (ignore html tags)
			if (markup.idCon != 'html') {
				var element = this.dom.document.createElement(markup.idCon.toString());
				this.dom.lastElement.appendChild(element);
				this.dom.lastElement = element;
			}	
			
			//Visit short-hand arguments of tag/element
			for(var i = 0; i < markup.attributes.length; i++){
				var attribute = markup.attributes[i];
				attribute.accept(new AttributeVisitor(this.env, this.dom));
			}
		}
	}
	
	/**
	 * Visitor Attributes
	 */
	function AttributeVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(attribute){
			if(attribute instanceof IdAttribute){
				attribute.accept(new IdAttributeVisitor(this.env, this.dom));
			}else if(attribute instanceof ClassAttribute){
				attribute.accept(new ClassAttributeVisitor(this.env, this.dom));
			}else if(attribute instanceof NameAttribute){
				attribute.accept(new NameAttributeVisitor(this.env, this.dom));
			}else if(attribute instanceof TypeAttribute){
				attribute.accept(new TypeAttributeVisitor(this.env, this.dom));
			}else if(attribute instanceof WidthHeightAttribute){
				attribute.accept(new WidthHeightAttributeVisitor(this.env, this.dom));
			}else if(attribute instanceof WidthAttribute){
				attribute.accept(new WidthAttributeVisitor(this.env, this.dom));
			}
		}
	}
	
	/**
	 * Visitor ID Attribute
	 */
	function IdAttributeVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(attribute){
			this.previousValue = this.dom.lastElement.getAttribute('id');			
			if(this.previousValue != null){
				this.dom.lastElement.setAttribute('id', this.previousValue + ' ' + attribute.id.toString());
			}else{
				this.dom.lastElement.setAttribute('id', attribute.id.toString());
			}
		}
	}
	
	/**
	 * Visitor Class Attribute
	 */
	function ClassAttributeVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(attribute){
			this.previousValue = this.dom.lastElement.getAttribute('class');			
			if(this.previousValue != null){
				this.dom.lastElement.setAttribute('class', this.previousValue + ' ' + attribute.className.toString());
			}else{
				this.dom.lastElement.setAttribute('class', attribute.className.toString());
			}
		}
	}
	
	/**
	 * Visitor Name Attribute
	 */
	function NameAttributeVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(attribute){
			this.previousValue = this.dom.lastElement.getAttribute('name');			
			if(this.previousValue != null){
				this.dom.lastElement.setAttribute('name', this.previousValue + ' ' + attribute.name.toString());
			}else{
				this.dom.lastElement.setAttribute('name', attribute.name.toString());
			}
		}
	}
	
	/**
	 * Visitor Type Attribute
	 */
	function TypeAttributeVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(attribute){
			this.previousValue = this.dom.lastElement.getAttribute('type');			
			if(this.previousValue != null){
				this.dom.lastElement.setAttribute('type', this.previousValue + ' ' + attribute.type.toString());
			}else{
				this.dom.lastElement.setAttribute('type', attribute.type.toString());
			}
		}
	}
	
	/**
	 * Visitor Width Height Attribute
	 */
	function WidthHeightAttributeVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(attribute){
			this.previousWidthValue = this.dom.lastElement.getAttribute('width');	
			this.previousHeightValue = this.dom.lastElement.getAttribute('height');			
					
			if(this.previousWidthValue != null){
				this.dom.lastElement.setAttribute('width', this.previousWidthValue + ' ' + attribute.width.toString());
			}else{
				this.dom.lastElement.setAttribute('width', attribute.width.toString());
			}
			
			if(this.previousHeightValue != null){
				this.dom.lastElement.setAttribute('height', this.previousHeightValue + ' ' + attribute.height.toString());
			}else{
				this.dom.lastElement.setAttribute('height', attribute.height.toString());
			}
		}
	}
	
	/**
	 * Visitor Width Attribute
	 */
	function WidthAttributeVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(attribute){
			this.previousValue = this.dom.lastElement.getAttribute('width');			
			if(this.previousValue != null){
				this.dom.lastElement.setAttribute('width', this.previousValue + ' ' + attribute.width.toString());
			}else{
				this.dom.lastElement.setAttribute('width', attribute.width.toString());
			}
		}
	}
	
	
	/**
	 * Visitor Argument
	 */
	function ArgumentVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(argument){
			if (argument instanceof Argument) { 
				argument.accept(new ArgumentExpressionVisitor(this.env, this.dom));
			} else { //Argument is expression
				argument.accept(new ExpressionVisitor(this.env, this.dom));
			}
		}
	}
	
	/**
	 * Visitor Argument Expression
	 */
	function ArgumentExpressionVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(argument){
			//If argument is Expression, than this is an argument of an XHTML Tag.
			//The arguments should then be processed as key/value where key is always "value"
			if (argument instanceof Argument) {			
				//Visit Expression
				argument.expression.accept(new ExpressionVisitor(this.env, this.dom));
				
				//Add attribute to last element
				this.dom.lastElement.setAttribute(argument.variable.toString(), this.dom.lastValue.toString());
			}else{
				//Visit Expression
				argument.accept(new ExpressionVisitor(this.env, this.dom));
				
				//Add attribute to last element
				this.dom.lastElement.setAttribute("value", this.dom.lastValue.toString());
			}
		}
	}
	
	/**
	 * Visitor KeyValue
	 */
	function KeyValueVisitor(env, dom){
		this.env = env;
		this.dom = dom;
		this.visit = function(keyValueExpr){
			//Visit Expression value
			keyValueExpr.value.accept(new ExpressionVisitor(this.env, this.dom));
		}
	}
}