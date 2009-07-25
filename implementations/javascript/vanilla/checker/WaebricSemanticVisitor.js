/** 
 * Performs a semantic validation on the module object
 *
 * The semantic validation includes:
 *
 * - Undefined functions: If for a markup-call f, no function definition can be
 * 	 found in the current module or one of its transitive imports, and if f is
 * 	 not a tag defined in the XHTML 1.0 Transitional standard, then this is an error.
 *
 * - Undefined variables: If a variable reference x cannot be traced back to an
 *   enclosing let-definition or function parameter, this is an error.
 *
 * - Non-existing module: If for an import directive import m no corresponding file
 * 	 m.wae can be found, this is an error.
 *
 * - Duplicate definitions: Multiple function definitions with the same name are
 *   disallowed.
 *
 * - Arity mismathces: If a function is called with an incorrect numer of arguments
 *   (as follows from its definition), this is an error.
 */

/**
 * Visitor for Module
 * 
 */
function ModuleVisitor(){
	this.visit = function(module, env){ 
		//Assign name to environment for exception logging
		if(env.name == ''){
			env.name = module.moduleId.identifier;
		}
		
		//Visit dependencies
		//Should be done before the current module is visited			
		for(var i = 0; i < module.dependencies.length; i++){
			//Visit only unprocessed dependencies
			var dependency = module.dependencies[i];
			var dependencyName = dependency.moduleId.identifier;
			var existingDependency = env.getDependency(dependencyName);
			
			//If dependency is not processed before, visit it
			if (existingDependency == null) {			
				var new_env = env.addDependency('module');
				new_env.name = dependency.moduleId.identifier;	
				dependency.accept(new ModuleVisitor(), new_env);
			}
			//If dependency was processed before, add existing dependecy to
			//current environment but do not visit it again.
			else{
				env.addExistingDependency(existingDependency);
			}
		}
		
		//Store Function Definitions
		//Should be done before the FunctionDefinitionVisitor is called
		for(var i = 0; i < module.functionDefinitions.length; i++){
			var functionDefinition = module.functionDefinitions[i];
			if (!env.containsFunction(functionDefinition.functionName)) {
				env.addFunction(functionDefinition);
			}else{
				env.addException(new DuplicateDefinitionException(functionDefinition));
			}
		}
		
		//Visit Function Definitions
		for(var i = 0; i < module.functionDefinitions.length; i++){
			var functionDefinition = module.functionDefinitions[i];
			functionDefinition.accept(new FunctionDefinitionVisitor(), env);
		}		
		
		//Visit Mappings
		for(var i = 0; i < module.site.mappings.length; i++){
			var mapping = module.site.mappings[i];
			mapping.markup.accept(new MarkupVisitor(), env);
		}		
	}
}

/**
 * Visitor for FunctionDefinition
 * 
 */
function FunctionDefinitionVisitor(){
	this.visit = function(functionDefinition, env){		
		//Add Arguments of function to new environment		
		var new_env = env.addEnvironment('func-def');		
		for (var i = 0; i < functionDefinition.formals.length; i++) {
			var formal = functionDefinition.formals[i];
			new_env.addVariable(formal);
		}
		
		//VisitStatements
		for(var i = 0; i < functionDefinition.statements.length; i++){
			var statement = functionDefinition.statements[i];
			statement.accept(new StatementVisitor(), new_env);
		}
	}
}

/**
 * Visitor for Statement
 */
function StatementVisitor(){
	this.visit = function(statement, env){
		if(statement instanceof IfStatement){
			statement.accept(new IfStatementVisitor(), env);
		}
		else if(statement instanceof IfElseStatement){
			statement.accept(new IfElseStatementVisitor(), env);
		}
		else if(statement instanceof EachStatement){
			statement.accept(new EachStatementVisitor(), env);
		}
		else if(statement instanceof LetStatement){
			statement.accept(new LetStatementVisitor(), env);
		}
		else if(statement instanceof BlockStatement){
			statement.accept(new BlockStatementVisitor(), env);
		}
		else if(statement instanceof CommentStatement){
			//No action required
		}
		else if(statement instanceof EchoStatement){
			statement.accept(new EchoStatementVisitor(), env)
		}
		else if(statement instanceof EchoEmbeddingStatement){
			statement.accept(new EchoEmbeddingVisitor(), env)
		}
		else if(statement instanceof CDataExpression){
			statement.accept(new CDataExpressionVisitor(), env)
		}
		else if(statement instanceof YieldStatement){
			//No action required
		}
		else if(statement instanceof MarkupStatement){
			statement.accept(new MarkupStatementVisitor(), env);			
		}
		else if(statement instanceof MarkupMarkupStatement){
			statement.accept(new MarkupMarkupStatementVisitor(), env)
		}
		else if(statement instanceof MarkupEmbeddingStatement){
			statement.accept(new MarkupEmbeddingStatementVisitor(), env)
		}
		else if(statement instanceof MarkupStatementStatement){
			statement.accept(new MarkupStatementStatementVisitor(), env)
		}
		else if(statement instanceof MarkupExpressionStatement){
			statement.accept(new MarkupExpressionStatementVisitor(), env)
		}		
		else{	//Statement is not recognized
			print("Unrecognized statement found. Parser should have thrown an error.");
		}
		
	}
}

/**
 * Visitor for CData
 */
function CDataExpressionVisitor(){
	this.visit = function(cdataExpr, env){
		//Visit Expression
		cdataExpr.expression.accept(new ExpressionVisitor(), env);
	}
}

/**
 * Visitor for Echo Embedding
 */
function EchoEmbeddingVisitor(){
	this.visit = function(echoEmbeddingStmt, env){
		//Visit Embedding
		echoEmbeddingStmt.embedding.accept(new EmbeddingVisitor(), env);
	}
}

/**
 * Visitor for Embedding
 */
function EmbeddingVisitor(){
	this.visit = function(embedding, env){
		//Visit Embed
		embedding.embed.accept(new EmbedVisitor(), env);
		
		//Visit Tail
		embedding.tail.accept(new TextTailVisitor(), env);
	}
}

/**
 * Visitor for TextTail
 */
function TextTailVisitor(){
	this.visit = function(textTail, env){
		if (textTail instanceof MidTextTail){
			textTail.accept(new MidTextTailVisitor(), env);
		}else if(textTail instanceof PostTextTail){
			//No action required
		}else{ //TextTail is not recognized
			print("Unrecognized TextTail found. Parser should have thrown an error.");
		}
	}
}

/**
 * Visitor for Mid Text Tail
 */
function MidTextTailVisitor(){
	this.visit = function(midTextTail, env){
		//Visit Embed
		midTextTail.embed.accept(new EmbedVisitor(), env);
		
		//Visit Tail
		midTextTail.tail.accept(new TextTailVisitor(), env);
	}
}

/**
 * Visitor Embed
 */
function EmbedVisitor(){
	this.visit = function(embed, env){
		if(embed instanceof ExpressionEmbedding){
			embed.accept(new ExpressionEmbeddingVisitor(), env);			
		}else if(embed instanceof MarkupEmbedding){			
			embed.accept(new MarkupEmbeddingVisitor(), env);
		}else{	//Embed is not recognized
			print("Unrecognized Embed found. Parser should have thrown an error.");
		}
	}
}

/**
 * Visitor ExpressionEmbedding
 */
function ExpressionEmbeddingVisitor(){
	this.visit = function(exprEmbed, env){	
		//Visit Markups	
		for(var i = 0; i < exprEmbed.markups.length; i++){
			var markup = exprEmbed.markups[i];
			markup.accept(new MarkupVisitor(), env);
		}
		
		//Visit expression
		exprEmbed.expression.accept(new ExpressionVisitor(), env);
	}
}

/**
 * Visitor MarkupEmbedding
 */
function MarkupEmbeddingVisitor(){
	this.visit = function(markupEmbed, env){
		//Visit Markups
		for(var i = 0; i < markupEmbed.markups.length; i++){
			var markup = markupEmbed.markups[i];
			markup.accept(new MarkupVisitor(), env);
		}
	}
}

/**
 * Visitor Echo Statement
 */
function EchoStatementVisitor(){
	this.visit = function(echoStmt, env){	
		//Visit Expression	
		echoStmt.expression.accept(new ExpressionVisitor(), env);	
	}
}

/**
 * Visitor Block Statement
 */
function BlockStatementVisitor(){
	this.visit = function(blockStmt, env){		
		//Visit statements
		for(var i = 0; i < blockStmt.statements.length; i++){			
			var statement = blockStmt.statements[i];
			statement.accept(new StatementVisitor(), env);
		}	
	}
}

/**
 * Visitor Let Statement
 */
function LetStatementVisitor(){
	this.visit = function(letStmt, env){
		//Assignments and statements in a let statement require a new environment under
		//the current environment since the scope of the assignments (function/variable declaration)
		// is limited to the statements inside the let statement
		var new_env = env.addEnvironment('let-stmt');
		
		//Visit Assignments
		for(var i = 0; i < letStmt.assignments.length; i++){
			var assignment = letStmt.assignments[i];
			assignment.accept(new AssignmentVisitor(), new_env);
		}
		
		//Visit Statements
		for(var j = 0; j < letStmt.statements.length; j++){
			var statement = letStmt.statements[j];
			statement.accept(new StatementVisitor(), new_env);
		}		
	}
}

/**
 * Visitor Assignment
 */
function AssignmentVisitor(){
	this.visit = function(assignment, env){		
		if(assignment instanceof VariableBinding){
			assignment.accept(new VariableBindingVisitor(), env);
		}else if(assignment instanceof FunctionBinding){
			assignment.accept(new FunctionBindingVisitor(), env);
		}else{	//Assignment is not recognized
			print("Unrecognized Assignment found. Parser should have thrown an error.");
		}	
	}
}

/**
 * Visitor Variablebinding
 */
function VariableBindingVisitor(){
	this.visit = function(varbind, env){
		//Add variable to current environment (let statement)
		env.addVariable(varbind.variable);
		
		//Visit expression
		varbind.expression.accept(new ExpressionVisitor(), env);
	}
}

/**
 * Visitor Functionbinding
 */
function FunctionBindingVisitor(){
	this.visit = function(funcbind, env){
		//Convert function binding into function definition
		var newFunctionName = funcbind.variable;
        var newFunctionFormals = funcbind.formals;
        var newFunctionStatements = [funcbind.statement];
        var newFunction = new FunctionDefinition(newFunctionName, newFunctionFormals, newFunctionStatements);
		
		//Add function to current environment (let statement)
		//(not done by FunctionDefinitionVisitor)
		if (!env.containsFunction(newFunctionName)) {
			env.addFunction(newFunction);
		}else{
			env.addException(new DuplicateDefinitionException(newFunction, env));
		}
		
		//Visit FunctionDefinition
		newFunction.accept(new FunctionDefinitionVisitor(), env);
		
	}
}

/**
 * Visitor IfStatement
 */
function IfStatementVisitor(){
	this.visit = function(ifStmt, env){
		//Visit predicate
		ifStmt.predicate.accept(new PredicateVisitor(), env);
		
		//Visit IfStatement
		ifStmt.ifStatement.accept(new StatementVisitor(), env);
	}
} 

/**
 * Visitor IfElseStatement
 */
function IfElseStatementVisitor(){
	this.visit = function(ifElseStmt, env){
		//Visit predicate
		ifElseStmt.predicate.accept(new PredicateVisitor(), env);
		
		//Visit IfStatement
		ifElseStmt.ifStatement.accept(new StatementVisitor(), env);
		
		//Visit ElseStatement
		ifElseStmt.elseStatement.accept(new StatementVisitor(), env);
	}
} 

/**
 * Visitor Predicate
 */
function PredicateVisitor(){
	this.visit = function(predicate, env){
		if(predicate instanceof NotPredicate){
			predicate.predicate.accept(new PredicateVisitor(), env);
		}else if(predicate instanceof AndPredicate){
			predicate.predicateLeft.accept(new PredicateVisitor(), env);
			predicate.predicateRight.accept(new PredicateVisitor(), env);
		}else if(predicate instanceof OrPredicate){
			predicate.predicateLeft.accept(new PredicateVisitor(), env);
			predicate.predicateRight.accept(new PredicateVisitor(), env);
		}else if(predicate instanceof IsAPredicate){
			predicate.expression.accept(new ExpressionVisitor(), env);
		}else{	//Predicate is not recognized
			predicate.accept(new ExpressionVisitor(), env); //Predicate is an Expression
		}
	}
} 

/**
 * Visitor Markup Statement
 */
function MarkupStatementVisitor(){
	this.visit = function(markupStmt, env){
		//Visit Markup
		markupStmt.markup.accept(new MarkupVisitor(), env);
	}
}

/**
 * Visitor MarkupMarkup Statement
 */
function MarkupMarkupStatementVisitor(){
	this.visit = function(markupMarkupStmt, env){
		//Visit Markups
		for(var i = 0; i < markupMarkupStmt.markups.length; i++){
			var markup = markupMarkupStmt.markups[i];
			markup.accept(new MarkupVisitor(), env);
		}		
	}
}

/**
 * Visitor MarkupStatement Statement
 */
function MarkupStatementStatementVisitor(){
	this.visit = function(markupStmtStmt, env){
		//Visit Markups
		for(var i = 0; i < markupStmtStmt.markups.length; i++){
			var markup = markupStmtStmt.markups[i];
			markup.accept(new MarkupVisitor(), env);
		}	
		
		//Visit Statement
		markupStmtStmt.statement.accept(new StatementVisitor(), env);	
	}
}

/**
 * Visitor MarkupEmbedding Statement
 */
function MarkupEmbeddingStatementVisitor(){
	this.visit = function(markupEmbeddingStmt, env){
		//Visit Markups
		for(var i = 0; i < markupEmbeddingStmt.markups.length; i++){
			var markup = markupEmbeddingStmt.markups[i];
			markup.accept(new MarkupVisitor(), env);
		}	
		
		//Visit Embedding
		markupEmbeddingStmt.embedding.accept(new EmbeddingVisitor(), env);	
	}
}

/**
 * Visitor MarkupExpression Statement
 */
function MarkupExpressionStatementVisitor(){
	this.visit = function(markupExprStmt, env){
		//Visit Markups
		for(var i = 0; i < markupExprStmt.markups.length; i++){
			var markup = markupExprStmt.markups[i];
			markup.accept(new MarkupVisitor(), env);
		}	
		
		//Visit Expression
		markupExprStmt.expression.accept(new ExpressionVisitor(), env);
	}
}

/**
 * Visitor Each Statement
 */
function EachStatementVisitor(){
	this.visit = function(eachStmt, env){
		//Add new environment because the scope of the variable declaration (identifier)
		// is limited to the statement inside the each statement.
		var new_env = env.addEnvironment('each-stmt');
		
		//Add variable to new environment of each statement
		new_env.addVariable(eachStmt.identifier);
		
		//Visit Expression
		eachStmt.expression.accept(new ExpressionVisitor(), new_env);
		
		//Visit Statement
		eachStmt.statement.accept(new StatementVisitor(), new_env);
	}
}

/**
 * Visitor Expression
 */
function ExpressionVisitor(){
	this.visit = function(expression, env){
		if(expression instanceof VarExpression){
			expression.accept(new VarExpressionVisitor(), env);
		}
		else if(expression instanceof SymbolExpression){
			//No action required			
		}
		else if(expression instanceof NatExpression){
			//No action required
		}
		else if(expression instanceof TextExpression){
			//No action required
		}
		else if(expression instanceof FieldExpression){
			expression.accept(new FieldExpressionVisitor(), env);
		}
		else if(expression instanceof CatExpression){
			expression.accept(new CatExpressionVisitor(), env);
		}
		else if(expression instanceof ListExpression){
			expression.accept(new ListExpressionVisitor(), env)
		}
		else if(expression instanceof RecordExpression){
			expression.accept(new RecordExpressionVisitor(), env);
		}
		else{ //Expression is not recognized
			print("Unrecognized Expression found. Parser should have thrown an error.");	
		}
	}
}

/**
 * Visitor Category Expression
 */
function CatExpressionVisitor(){
	this.visit = function(catExpr, env){
		//Visit Expression Left
		catExpr.expressionLeft.accept(new ExpressionVisitor(), env);
		
		//Visit Expression Right
		catExpr.expressionRight.accept(new ExpressionVisitor(), env);
	}
}

/**
 * Visitor Field Expression
 */
function FieldExpressionVisitor(){
	this.visit = function(fieldExpr, env){
		//Visit Expression
		fieldExpr.expression.accept(new VarExpressionVisitor(), env);
	}
}

/**
 * Visitor List Expression
 */
function ListExpressionVisitor(){
	this.visit = function(listExpr, env){
		//Visit Expressions
		for(var i = 0; i < listExpr.list.length; i++){		
			var expression = listExpr.list[i];	
			expression.accept(new ExpressionVisitor(), env);
		}
	}
}

/**
 * Visitor Record Expression
 */
function RecordExpressionVisitor(){
	this.visit = function(recordExpr, env){
		//Visit KeyValuePairs
		for(var i = 0; i < recordExpr.record.length; i++){		
			var keyValuePair = recordExpr.record[i];	
			keyValuePair.accept(new KeyValueVisitor(), env);
		}
	}
}

/**
 * Visitor KeyValue
 */
function KeyValueVisitor(){
	this.visit = function(keyValueExpr, env){
		//Visit Expression value
		keyValueExpr.value.accept(new ExpressionVisitor(), env);
	}
}

/**
 * Visitor Markup
 */
function MarkupVisitor(){
	this.visit = function(markup, env){ 
		if(markup instanceof MarkupCall){	
			//Check if function already exists in current environment (incl dependecies)
			var functionDefinition = env.getLocalFunction(markup.designator.idCon);
			if(functionDefinition != null){
				//Check if number of the arguments equals
				if (functionDefinition.formals.length != markup.arguments.length) {
					 //If arguments do not equal and the designator tag is not part
					//of XHTML, then it must be a function call with incorrect arguments.
					if (!XHTML.isXHTMLTag(markup.designator.idCon)) {
						env.addException(new IncorrectArgumentsException(markup, env));
					}
				}							
			}else if(!XHTML.isXHTMLTag(markup.designator.idCon)){
				//If function does not exists and the designator tag is not part of
				//XHTML, then it must be an undefined function						
				env.addException(new UndefinedFunctionException(markup, env))
			}else{//XHTML tag
				//No action required
			}
			
			//Visit arguments/formals
			for(var i = 0; i < markup.arguments.length; i++){									
				var argument = markup.arguments[i];
				argument.accept(new ArgumentVisitor(), env);
			}	
		}else{	//Markup is MarkupTag Expression			
			//No action required
		}
	}
}

/**
 * Visitor Variable Expression
 */
function VarExpressionVisitor(){
	this.visit = function(variable, env){
		//Check if variable exists
		if(env.getVariable(variable) == null){
			env.addException(new UndefinedVariableException(variable, env));
		}
	}
}

/**
 * Visitor Argument
 */
function ArgumentVisitor(){
	this.visit = function(argument, env){
		if(argument instanceof Argument) {
			argument.accept(new ArgumentExpressionVisitor(), env);
		}else{  //Argument is expression
			argument.accept(new ExpressionVisitor(), env);
		}
	}
}

/**
 * Visitor Argument Expression
 */
function ArgumentExpressionVisitor(){
	this.visit = function(argument, env){		
		//Visit Expression
		argument.expression.accept(new ExpressionVisitor(), env);
	}
}

