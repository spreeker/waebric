/**************************************************************************** 
 * Specifies a Statement used in FunctionDefinition
 * 
 * @author Nickolas Heirbaut 
 ****************************************************************************/

/**
 * If Statement Class
 * 
 * "if" "(" Predicate ")" Statement NoElseMayFollow -> Statement ("if")
 * 
 * @param {Object} predicate
 * @param {Object} ifStatement
 */
function IfStatement(predicate, ifStatement){
	this.predicate = predicate;
	this.ifStatement = ifStatement;
	
	this.toString = function(){
		return 'if(' + this.predicate.toString() + '){\n' + this.ifStatement.toString() + '\n}\n' 
	}
}
IfStatement.prototype = new Node(); //Inheritance base class

/**
 * IfElse Statement Class
 * 
 * "if" "(" Predicate ")" Statement "else" Statement -> Statement ("if-els")
 * 
 * @param {Object} predicate
 * @param {Object} ifStatement
 * @param {Object} elseStatement
 */
function IfElseStatement(predicate, ifStatement, elseStatement){
	this.predicate = predicate;
	this.ifStatement = ifStatement;
	this.elseStatement = elseStatement;
	
	this.toString = function(){
		return 'if(' + this.predicate.toString() + '){\n' + this.ifStatement.toString() + '\n}else{\n' + this.elseStatement.toString() + '\n}\n' 
	}
}
IfElseStatement.prototype = new Node(); //Inheritance base class

/**
 * Each Statement Class
 * 
 * "each" "(" IdCon ":" Expression ")" Statement -> Statement ("each")
 * 
 * @param {Object} identifier
 * @param {Object} expression
 * @param {Object} statement
 */
function EachStatement(identifier, expression, statement){
	this.identifier = identifier;
	this.expression = expression;
	this.statement = statement;
}
EachStatement.prototype = new Node(); //Inheritance base class

/**
 * Let Statement Class
 * 
 * "let" Assignment+ "in" Statement* "end" -> Statement ("let")
 * 
 * @param {Array} Array of assignments
 * @param {Array} Array of statements
 */
function LetStatement(assignments, statements){
	this.assignments = assignments;
	this.statements = statements;
}
LetStatement.prototype = new Node(); //Inheritance base class

/**
 * Block Statement Class
 * 
 * CB Statement* CB -> Statement ("block")
 * 
 * @param {Array} Array of statements
 */
function BlockStatement(statements){
	this.statements = statements;
}
BlockStatement.prototype = new Node(); //Inheritance base class

/**
 * Comment Statement Class
 * 
 * "comment" StrCon ";" -> Statement ("comment")
 * 
 * @param {Object} comment
 */
function CommentStatement(comment){
	this.comment = comment;
	
	this.toString = function(){
		return "comment: " + this.comment;
	}
}
CommentStatement.prototype = new Node(); //Inheritance base class

/**
 * Echo Statement Class
 * 
 * "echo" Expression ";" -> Statement ("echo")
 * 
 * @param {Object} expression
 */
function EchoStatement(expression){
	this.expression = expression;
	
	this.toString = function(){
		return "echo: " + this.expression.toString(); 
	}
}
EchoStatement.prototype = new Node(); //Inheritance base class

/**
 * Echo Embedding Statement Class
 * 
 * "echo" Embedding ";" -> Statement ("echo-embedding")
 * 
 * @param {Object} embedding
 */
function EchoEmbeddingStatement(embedding){
	this.embedding = embedding;
	
	this.toString = function(){
		return "echoembedding: " + this.embedding.toString(); 
	}
}
EchoEmbeddingStatement.prototype = new Node(); //Inheritance base class

/**
 * CData Statement Class
 * 
 * "cdata" Expression ";" -> Statement ("cdata")
 * 
 * @param {Object} expression
 */
function CDataExpression(expression){
	this.expression = expression;
}
CDataExpression.prototype = new Node(); //Inheritance base class

/**
 * Yield Statement Class
 * 
 * "yield" ";" -> Statement ("yield") 
 */
function YieldStatement(){

}
YieldStatement.prototype = new Node(); //Inheritance base class

/**
 * Markup Statement Class
 * 
 * Markup ";" -> Statement ("markup")
 * 
 * @param {Object} markup
 */
function MarkupStatement (markup){
	this.markup = markup;
}
MarkupStatement.prototype = new Node(); //Inheritance base class

/**
 * Markup Expression Statement Class
 * 
 * Markup+ Expression ";" -> Statement ("markup-exp")
 * 
 * @param {Object} markup
 * @param {Object} expression
 */
function MarkupExpressionStatement (markups, expression){
	this.markups = markups;
	this.expression = expression;
}
MarkupExpressionStatement.prototype = new Node(); //Inheritance base class

/**
 * Markup Markup Statement Class
 * 
 * Markup+ Markup -> Statement ("markup-markup")
 * 
 * @param {Object} markup
 */
function MarkupMarkupStatement (markups){
	this.markups = markups;
}
MarkupMarkupStatement.prototype = new Node(); //Inheritance base class

/**
 * Markup Statement Statement Class
 * 
 * Markup+ Statement -> Statement ("markup-stat")
 * 
 * @param {Object} markup
 * @param {Object} statement
 */
function MarkupStatementStatement (markups, statement){
	this.markups = markups;
	this.statement = statement;
}
MarkupStatementStatement.prototype = new Node(); //Inheritance base class

/**
 * Markup Embedding Statement Class
 * 
 * Markup+ Embedding ";" -> Statement ("markup-embedding")
 * 
 * @param {Object} markup
 * @param {Object} embedding
 */
function MarkupEmbeddingStatement (markups, embedding){
	this.markups = markups;
	this.embedding = embedding;
	
	this.toString = function(){
		return "[MarkupEmbeddingStmt: " + this.markups + "," + this.embedding + "]";
	}
}
MarkupEmbeddingStatement.prototype = new Node(); //Inheritance base class