/** 
 * Performs a semantic validation on the module object based on the Visitor pattern.
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
 *   
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 *   
 * @param {WaebricEnvironment} env The parent environment
 */
function WaebricValidatorVisitor(env){
	
	/**
	 * Returns a module visitor
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 * @reutrn {Object} A visitor for the {Module} object
	 */
	this.getModuleVisitor = function(env){
		return new ModuleVisitor(env);
	}
	
	/**
	 * Visitor for Module
	 *
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function ModuleVisitor(env){
		this.env = env
		this.visit = function(module){
			//Assign name to environment for exception logging
			if (this.env.name == '') {
				this.env.name = module.moduleId.identifier;
			}			
			
			//Visit dependencies
			//Should be done before the current module is visited			
			for (var i = 0; i < module.dependencies.length; i++) {
				var dependency = module.dependencies[i];
				dependency.accept(new DependencyVisitor(this.env));
			}
			
			//Store Function Definitions
			//Should be done before the FunctionDefinitionVisitor is called
			for (var i = 0; i < module.functionDefinitions.length; i++) {
				var functionDefinition = module.functionDefinitions[i];
				if (!this.env.containsFunction(functionDefinition.functionName)) {
					this.env.addFunction(functionDefinition);
				} else {
					this.env.addException(new DuplicateDefinitionException(functionDefinition));
				}
			}
			
			//Visit Function Definitions
			for (var i = 0; i < module.functionDefinitions.length; i++) {
				var functionDefinition = module.functionDefinitions[i];
				functionDefinition.accept(new FunctionDefinitionVisitor(this.env));
			}
			//Visit Mappings
			for (var i = 0; i < module.site.mappings.length; i++) {
				var mapping = module.site.mappings[i];
				mapping.markup.accept(new MarkupVisitor(this.env));
			}
		}
	}
		
	/**
	 * Visitor for dependencies (imports)
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function DependencyVisitor(env){
		this.env = env
		this.visit = function(module){
			//Visit only unprocessed dependencies
			var dependencyName = module.moduleId.identifier;
			var existingDependency = this.env.getDependency(dependencyName);
			//If dependency is not processed before, visit it
			if (existingDependency == null) {
				var new_env = this.env.addDependency('module');
				new_env.name = module.moduleId.identifier;
				module.accept(new ModuleVisitor(new_env));
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
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function FunctionDefinitionVisitor(env){
		this.env = env
		this.visit = function(functionDefinition){
			//Add Arguments of function to new environment	
			var new_env = this.env.addEnvironment('func-def');
			for (var i = 0; i < functionDefinition.formals.length; i++) {
				var formal = functionDefinition.formals[i];
				new_env.addVariable(formal);
			}
			
			//VisitStatements
			for (var i = 0; i < functionDefinition.statements.length; i++) {
				var statement = functionDefinition.statements[i];
				statement.accept(new StatementVisitor(new_env));
			}
		}
	}
	
	/**
	 * Visitor for Statement
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function StatementVisitor(env){
		this.env = env
		this.visit = function(statement){
			if (statement instanceof IfStatement) {
				statement.accept(new IfStatementVisitor(this.env));
			} else if (statement instanceof IfElseStatement) {
				statement.accept(new IfElseStatementVisitor(this.env));
			} else if (statement instanceof EachStatement) {
				statement.accept(new EachStatementVisitor(this.env));
			} else if (statement instanceof LetStatement) {
				statement.accept(new LetStatementVisitor(this.env));
			} else if (statement instanceof BlockStatement) {
				statement.accept(new BlockStatementVisitor(this.env));
			} else if (statement instanceof CommentStatement) {
				//No action required
			} else if (statement instanceof EchoStatement) {
				statement.accept(new EchoStatementVisitor(this.env));
			} else if (statement instanceof EchoEmbeddingStatement) {
				statement.accept(new EchoEmbeddingVisitor(this.env));
			} else if (statement instanceof CDataExpression) {
				statement.accept(new CDataExpressionVisitor(this.env));
			} else if (statement instanceof YieldStatement) {
				//No action required
			} else if (statement instanceof MarkupStatement) {
				statement.accept(new MarkupStatementVisitor(this.env));
			} else if (statement instanceof MarkupMarkupStatement) {
				statement.accept(new MarkupMarkupStatementVisitor(this.env));
			} else if (statement instanceof MarkupEmbeddingStatement) {
				statement.accept(new MarkupEmbeddingStatementVisitor(this.env));
			} else if (statement instanceof MarkupStatementStatement) {
				statement.accept(new MarkupStatementStatementVisitor(this.env));
			} else if (statement instanceof MarkupExpressionStatement) {
				statement.accept(new MarkupExpressionStatementVisitor(this.env));
			} else { //Statement is not recognized
				print("Unrecognized statement found. Parser should have thrown an error.");
			}
			
		}
	}
			
	/**
	 * Visitor IfStatement
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function IfStatementVisitor(env){
		this.env = env
		this.visit = function(ifStmt){
			//Visit predicate
			ifStmt.predicate.accept(new PredicateVisitor(this.env));
			
			//Visit IfStatement
			ifStmt.ifStatement.accept(new StatementVisitor(this.env));
		}
	}
	
	/**
	 * Visitor IfElseStatement
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function IfElseStatementVisitor(env){
		this.env = env
		this.visit = function(ifElseStmt){
			//Visit predicate
			ifElseStmt.predicate.accept(new PredicateVisitor(this.env));
			
			//Visit IfStatement
			ifElseStmt.ifStatement.accept(new StatementVisitor(this.env));
			
			//Visit ElseStatement
			ifElseStmt.elseStatement.accept(new StatementVisitor(this.env));
		}
	}
	
	/**
	 * Visitor Each Statement
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function EachStatementVisitor(env){
		this.env = env
		this.visit = function(eachStmt){
			//Add new environment because the scope of the variable declaration (identifier)
			// is limited to the statement inside the each statement.
			var new_env = this.env.addEnvironment('each-stmt');
			
			//Add variable to new environment of each statement
			new_env.addVariable(eachStmt.identifier);
			
			//Visit Expression
			eachStmt.expression.accept(new ExpressionVisitor(new_env));
			
			//Visit Statement
			eachStmt.statement.accept(new StatementVisitor(new_env));
		}
	}
	
	/**
	 * Visitor Let Statement
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function LetStatementVisitor(env){
		this.env = env
		this.visit = function(letStmt){
			//Assignments and statements in a let statement require a new environment under
			//the current environment since the scope of the assignments (function/variable declaration)
			// is limited to the statements inside the let statement
			var new_env = this.env.addEnvironment('let-stmt');
			
			//Visit Assignments
			for (var i = 0; i < letStmt.assignments.length; i++) {
				var assignment = letStmt.assignments[i];
				assignment.accept(new AssignmentVisitor(this.env));
			}
			
			//Visit Statements
			for (var j = 0; j < letStmt.statements.length; j++) {
				var statement = letStmt.statements[j];
				statement.accept(new StatementVisitor(this.env));
			}
		}
	}

	/**
	 * Visitor Block Statement
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function BlockStatementVisitor(env){
		this.env = env
		this.visit = function(blockStmt){
			//Visit statements
			for (var i = 0; i < blockStmt.statements.length; i++) {
				var statement = blockStmt.statements[i];
				statement.accept(new StatementVisitor(this.env));
			}
		}
	}

	/**
	 * Visitor Echo Statement
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function EchoStatementVisitor(env){
		this.env = env
		this.visit = function(echoStmt){
			//Visit Expression	
			echoStmt.expression.accept(new ExpressionVisitor(this.env));
		}
	}
	
	/**
	 * Visitor for Echo Embedding
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function EchoEmbeddingVisitor(env){
		this.env = env
		this.visit = function(echoEmbeddingStmt){
			//Visit Embedding
			echoEmbeddingStmt.embedding.accept(new EmbeddingVisitor(this.env));
		}
	}

	/**
	 * Visitor for CData
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function CDataExpressionVisitor(env){
		this.env = env
		this.visit = function(cdataExpr){
			//Visit Expression
			cdataExpr.expression.accept(new ExpressionVisitor(this.env));
		}
	}
	
	/**
	 * Visitor Markup Statement
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function MarkupStatementVisitor(env){
		this.env = env
		this.visit = function(markupStmt){
			//Visit Markup
			markupStmt.markup.accept(new MarkupVisitor(this.env));
		}
	}
	
	/**
	 * Visitor MarkupMarkup Statement
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function MarkupMarkupStatementVisitor(env){
		this.env = env
		this.visit = function(markupMarkupStmt){
			//Visit Markups
			for (var i = 0; i < markupMarkupStmt.markups.length; i++) {
				var markup = markupMarkupStmt.markups[i];
				markup.accept(new MarkupVisitor(this.env));
			}
		}
	}
	
	/**
	 * Visitor MarkupEmbedding Statement
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function MarkupEmbeddingStatementVisitor(env){
		this.env = env
		this.visit = function(markupEmbeddingStmt){
			//Visit Markups
			for (var i = 0; i < markupEmbeddingStmt.markups.length; i++) {
				var markup = markupEmbeddingStmt.markups[i];
				markup.accept(new MarkupVisitor(this.env));
			}
			
			//Visit Embedding
			markupEmbeddingStmt.embedding.accept(new EmbeddingVisitor(this.env));
		}
	}
	
	/**
	 * Visitor MarkupStatement Statement
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function MarkupStatementStatementVisitor(env){
		this.env = env
		this.visit = function(markupStmtStmt){
			//Visit Markups
			for (var i = 0; i < markupStmtStmt.markups.length; i++) {
				var markup = markupStmtStmt.markups[i];
				markup.accept(new MarkupVisitor(this.env));
			}
			
			//Visit Statement
			markupStmtStmt.statement.accept(new StatementVisitor(this.env));
		}
	}
		
	/**
	 * Visitor MarkupExpression Statement
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function MarkupExpressionStatementVisitor(env){
		this.env = env
		this.visit = function(markupExprStmt){
			//Visit Markups
			for (var i = 0; i < markupExprStmt.markups.length; i++) {
				var markup = markupExprStmt.markups[i];
				markup.accept(new MarkupVisitor(this.env));
			}
			
			//Visit Expression
			markupExprStmt.expression.accept(new ExpressionVisitor(this.env));
		}
	}
	
	/**
	 * Visitor Expression
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function ExpressionVisitor(env){
		this.env = env
		this.visit = function(expression){
			if (expression instanceof VarExpression) {
				expression.accept(new VarExpressionVisitor(this.env));
			} else if (expression instanceof SymbolExpression) {
				//No action required			
			} else if (expression instanceof NatExpression) {
				//No action required
			} else if (expression instanceof TextExpression) {
				//No action required
			} else if (expression instanceof FieldExpression) {
				expression.accept(new FieldExpressionVisitor(this.env));
			} else if (expression instanceof CatExpression) {
				expression.accept(new CatExpressionVisitor(this.env));
			} else if (expression instanceof ListExpression) {
				expression.accept(new ListExpressionVisitor(this.env));
			} else if (expression instanceof RecordExpression) {
				expression.accept(new RecordExpressionVisitor(this.env));
			} else { //Expression is not recognized
				print("Unrecognized Expression found. Parser should have thrown an error.");
			}
		}
	}	
		
	/**
	 * Visitor Variable Expression
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function VarExpressionVisitor(env){
		this.env = env
		this.visit = function(variable){
			//Check if variable exists
			if (this.env.getVariable(variable) == null) {
				this.env.addException(new UndefinedVariableException(variable, this.env));
			}
		}
	}
	
	/**
	 * Visitor Field Expression
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function FieldExpressionVisitor(env){
		this.env = env
		this.visit = function(fieldExpr){
			fieldExpr.expression.accept(new ExpressionVisitor(this.env));
		}
	}	
		
	/**
	 * Visitor Category Expression
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function CatExpressionVisitor(env){
		this.env = env
		this.visit = function(catExpr){
			//Visit Expression Left
			catExpr.expressionLeft.accept(new ExpressionVisitor(this.env));
			
			//Visit Expression Right
			catExpr.expressionRight.accept(new ExpressionVisitor(this.env));
		}
	}	
	
	/**
	 * Visitor List Expression
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function ListExpressionVisitor(env){
		this.env = env
		this.visit = function(listExpr){
			//Visit Expressions
			for (var i = 0; i < listExpr.list.length; i++) {
				var expression = listExpr.list[i];
				expression.accept(new ExpressionVisitor(this.env));
			}
		}
	}
	
	/**
	 * Visitor Record Expression
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function RecordExpressionVisitor(env){
		this.env = env
		this.visit = function(recordExpr){
			//Visit KeyValuePairs
			for (var i = 0; i < recordExpr.records.length; i++) {
				var keyValuePair = recordExpr.records[i];
				keyValuePair.accept(new KeyValueVisitor(this.env));
			}
		}
	}
		
	/**
	 * Visitor Predicate
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function PredicateVisitor(env){
		this.env = env
		this.visit = function(predicate){
			if (predicate instanceof NotPredicate) {
				predicate.predicate.accept(new PredicateVisitor(this.env));
			} else if (predicate instanceof AndPredicate) {
				predicate.predicateLeft.accept(new PredicateVisitor(this.env));
				predicate.predicateRight.accept(new PredicateVisitor(this.env));
			} else if (predicate instanceof OrPredicate) {
				predicate.predicateLeft.accept(new PredicateVisitor(this.env));
				predicate.predicateRight.accept(new PredicateVisitor(this.env));
			} else if (predicate instanceof IsAPredicate) {
				predicate.expression.accept(new ExpressionVisitor(this.env));
			} else { //Predicate is not recognized
				predicate.accept(new ExpressionVisitor(this.env)); //Predicate is an Expression
			}
		}
	}
		
	/**
	 * Visitor for Embedding
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function EmbeddingVisitor(env){
		this.env = env
		this.visit = function(embedding){
			//Visit Embed
			embedding.embed.accept(new EmbedVisitor(this.env));
			
			//Visit Tail
			embedding.tail.accept(new TextTailVisitor(this.env));
		}
	}
	
	/**
	 * Visitor Embed
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function EmbedVisitor(env){
		this.env = env
		this.visit = function(embed){
			if (embed instanceof ExpressionEmbedding) {
				embed.accept(new ExpressionEmbeddingVisitor(this.env));
			} else if (embed instanceof MarkupEmbedding) {
				embed.accept(new MarkupEmbeddingVisitor(this.env));
			} else { //Embed is not recognized
				print("Unrecognized Embed found. Parser should have thrown an error.");
			}
		}
	}

	/**
	 * Visitor for TextTail
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function TextTailVisitor(env){
		this.env = env
		this.visit = function(textTail){
			if (textTail instanceof MidTextTail) {
				textTail.accept(new MidTextTailVisitor(this.env));
			} else if (textTail instanceof PostTextTail) {
				//No action required
			} else { //TextTail is not recognized
				print("Unrecognized TextTail found. Parser should have thrown an error.");
			}
		}
	}
	
	/**
	 * Visitor for Mid Text Tail
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function MidTextTailVisitor(env){
		this.env = env
		this.visit = function(midTextTail){
			//Visit Embed
			midTextTail.embed.accept(new EmbedVisitor(this.env));
			
			//Visit Tail
			midTextTail.tail.accept(new TextTailVisitor(this.env));
		}
	}
			
	/**
	 * Visitor ExpressionEmbedding
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function ExpressionEmbeddingVisitor(env){
		this.env = env
		this.visit = function(exprEmbed){
			//Visit Markups	
			for (var i = 0; i < exprEmbed.markups.length; i++) {
				var markup = exprEmbed.markups[i];
				markup.accept(new MarkupVisitor(this.env));
			}
			
			//Visit expression
			exprEmbed.expression.accept(new ExpressionVisitor(this.env));
		}
	}
	
	/**
	 * Visitor MarkupEmbedding
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function MarkupEmbeddingVisitor(env){
		this.env = env
		this.visit = function(markupEmbed){
			//Visit Markups
			for (var i = 0; i < markupEmbed.markups.length; i++) {
				var markup = markupEmbed.markups[i];
				markup.accept(new MarkupVisitor(this.env));
			}
		}
	}
			
	/**
	 * Visitor Assignment
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function AssignmentVisitor(env){
		this.env = env
		this.visit = function(assignment){
			if (assignment instanceof VariableBinding) {
				assignment.accept(new VariableBindingVisitor(this.env));
			} else if (assignment instanceof FunctionBinding) {
				assignment.accept(new FunctionBindingVisitor(this.env));
			} else { //Assignment is not recognized
				print("Unrecognized Assignment found. Parser should have thrown an error.");
			}
		}
	}

	/**
	 * Visitor Variablebinding
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function VariableBindingVisitor(env){
		this.env = env
		this.visit = function(varbind){
			//Add variable to current environment (let statement)
			this.env.addVariable(varbind.variable);
			
			//Visit expression
			varbind.expression.accept(new ExpressionVisitor(this.env));
		}
	}
	
	/**
	 * Visitor Functionbinding
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function FunctionBindingVisitor(env){
		this.env = env
		this.visit = function(funcbind){
			//Convert function binding into function definition
			var newFunctionName = funcbind.variable;
			var newFunctionFormals = funcbind.formals;
			var newFunctionStatements = [funcbind.statement];
			var newFunction = new FunctionDefinition(newFunctionName, newFunctionFormals, newFunctionStatements);
			
			//Add function to current environment (let statement)
			//(not done by FunctionDefinitionVisitor)
			if (!this.env.containsFunction(newFunctionName)) {
				this.env.addFunction(newFunction);
			} else {
				this.env.addException(new DuplicateDefinitionException(newFunction, this.env));
			}
			
			//Visit FunctionDefinition
			newFunction.accept(new FunctionDefinitionVisitor(this.env));
			
		}
	}

	/**
	 * Visitor Markup
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function MarkupVisitor(env){
		this.env = env
		this.visit = function(markup){
			if (markup instanceof MarkupCall) {
				//Check if function already exists in current environment (incl dependecies)
				var functionDefinition = this.env.getLocalFunction(markup.designator.idCon);
				if (functionDefinition != null) {
					//Check if number of the arguments equals
					if (functionDefinition.formals.length != markup.arguments.length) {
						//If arguments do not equal and the designator tag is not part
						//of XHTML, then it must be a function call with incorrect arguments.
						if (!XHTML.isXHTMLTag(markup.designator.idCon)) {
							this.env.addException(new IncorrectArgumentsException(markup, this.env));
						}
					}
				} else if (!XHTML.isXHTMLTag(markup.designator.idCon)) {
					//If function does not exists and the designator tag is not part of
					//XHTML, then it must be an undefined function						
					this.env.addException(new UndefinedFunctionException(markup, this.env))
				} else {//XHTML tag
					//No action required
				}
				
				//Visit arguments/formals
				for (var i = 0; i < markup.arguments.length; i++) {
					var argument = markup.arguments[i];
					argument.accept(new ArgumentVisitor(this.env));
				}
			} else { //Markup is MarkupTag Expression			
				//No action required
			}
		}
	}
	
	/**
	 * Visitor Argument
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function ArgumentVisitor(env){
		this.env = env
		this.visit = function(argument){
			if (argument instanceof Argument) {
				argument.accept(new ArgumentExpressionVisitor(this.env));
			} else { //Argument is expression
				argument.accept(new ExpressionVisitor(this.env));
			}
		}
	}
	
	/**
	 * Visitor Argument Expression
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function ArgumentExpressionVisitor(env){
		this.env = env
		this.visit = function(argument){
			//Visit Expression
			argument.expression.accept(new ExpressionVisitor(this.env));
		}
	}
	
	/**
	 * Visitor KeyValue
	 * 
	 * @param {WaebricEnvironment} env The parent environment
	 */
	function KeyValueVisitor(env){
		this.env = env
		this.visit = function(keyValueExpr){
			//Visit Expression value
			keyValueExpr.value.accept(new ExpressionVisitor(this.env));
		}
	}
}
