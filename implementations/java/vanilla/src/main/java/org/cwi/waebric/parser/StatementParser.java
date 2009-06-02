package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.basic.StrCon;
import org.cwi.waebric.parser.ast.expressions.Expression;
import org.cwi.waebric.parser.ast.expressions.Var;
import org.cwi.waebric.parser.ast.predicates.Predicate;
import org.cwi.waebric.parser.ast.statements.Assignment;
import org.cwi.waebric.parser.ast.statements.Formals;
import org.cwi.waebric.parser.ast.statements.Statement;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.WaebricScanner;
import org.cwi.waebric.scanner.token.Token;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.cwi.waebric.scanner.token.TokenSort;

/**
 * Statement parser
 * 
 * module languages/waebric/syntax/Statements
 * 
 * @author Jeroen van Schagen
 * @date 27-05-2009
 */
class StatementParser extends AbstractParser {

	private final ExpressionParser expressionParser;
	private final PredicateParser predicateParser;
	
	public StatementParser(TokenIterator tokens, List<ParserException> exceptions) {
		super(tokens, exceptions);
		
		// Construct sub parsers
		expressionParser = new ExpressionParser(tokens, exceptions);
		predicateParser = new PredicateParser(tokens, exceptions);
	}
	
	/**
	 * Recognise and construct statement
	 * 
	 * @see Statement
	 * 
	 * @param name Construct name in which statement is used, used for error reporting.
	 * @param syntax Syntax notation in which statement is called.
	 * @return Statement
	 */
	public Statement parseStatement(String name, String syntax) {
		if(! tokens.hasNext()) {
			reportMissingToken(name, syntax);
			return null;
		}
	
		Token peek = tokens.peek(1); // Determine statement type based on look-ahead
		if(peek.getLexeme().equals(WaebricKeyword.IF)) {
			// If(-else) statements start with an if keyword
			return parseIfStatement();
		} else if(peek.getLexeme().equals(WaebricKeyword.EACH)) {
			// Each statements start with an each keyword
			return parseEachStatement();
		} else if(peek.getLexeme().equals(WaebricKeyword.LET)) {
			// Let statements start with a let keyword
			return parseLetStatement();
		} else if(peek.getLexeme().equals(WaebricSymbol.LCBRACKET)) {
			// Statement collections start with a {
			return parseStatementCollection();
		} else if(peek.getLexeme().equals(WaebricKeyword.COMMENT)) {
			// Comment statements start with a comments keyword
			return parseCommentStatement();
		} else if(peek.getLexeme().equals(WaebricKeyword.ECHO)) {
			// Embedding echo production is followed by a text
			if(tokens.peek(2).getSort().equals(TokenSort.STRCON)) {
				return parseEchoEmbeddingStatement();
			} 
			// Embedding echo production is followed by an expression
			else { return parseEchoExpressionStatement(); }
		} else if(peek.getLexeme().equals(WaebricKeyword.CDATA)) {
			// CData statements start with a cdata keyword
			return parseCDataStatement();
		} else if(peek.getLexeme().equals(WaebricKeyword.YIELD)) {
			// Yield statements start with a yield keyword
			return parseYieldStatement();
		} else {
			// Token stream cannot be seen as statement
			reportUnexpectedToken(peek, "statement", 
					"\"if\", \"each\", \"let\", \"{\", \"comment\", " +
					"\"echo\", \"cdata\" or \"yield\"");
		}
		
		return null;
	}
	
	/**
	 * @see Statement.IfStatement
	 * @return
	 */
	public Statement.IfStatement parseIfStatement() {
		if(! next("if keyword", "\"if\" \"(\"", WaebricKeyword.IF)) {
			return null; // Invalid syntax
		}
		
		if(! next("predicate opening", "\"if\" \"(\" predicate", WaebricSymbol.LPARANTHESIS)) {
			return null; // Invalid syntax
		}
		Predicate predicate = parsePredicate(); // Parse predicate
		if(! next("predicate closure", "\"(\" predicate \")\"", WaebricSymbol.RPARANTHESIS)) {
			return null; // Invalid syntax
		}
		
		// Parse if sub-statement
		Statement subStatement = 
			parseStatement("if statement", "\"if\" \"(\" predicate \")\" statement");

		Statement.IfStatement statement = null; // Determine type
		if(tokens.hasNext() && tokens.peek(1).getLexeme().equals(WaebricKeyword.ELSE)) {
			tokens.next(); // Skip else keyword
			
			// Parse else sub-statement
			Statement elseStatement = parseStatement("else statement", "\"else\" statement");
			statement = new Statement.IfElseStatement(elseStatement);
		} else {
			statement = new Statement.IfStatement();
		}
		
		statement.setPredicate(predicate); // Store predicate
		statement.setStatement(subStatement); // Store if sub-statement
		
		return statement;
	}
	
	/**
	 * @see Statement.EachStatement
	 * @return
	 */
	public Statement.EachStatement parseEachStatement() {
		Statement.EachStatement statement = new Statement.EachStatement();
		
		if(! next("each keyword", "\"each\"", WaebricKeyword.EACH)) {
			return null; // Invalid syntax
		}
		
		if(! next("each left parenthesis", "\"each\" \"(\" var", WaebricSymbol.LPARANTHESIS)) {
			return null; // Invalid syntax
		}
		
		Var var = parseVar("each var", "\"(\" var \":\"");
		statement.setVar(var);
		
		if(! next("each colon separator", "var \":\" expression", WaebricSymbol.COLON)) {
			return null; // Invalid syntax
		}
		
		// Parse expression
		Expression expression = parseExpression("each expression", "\":\" expression \")\"");
		statement.setExpression(expression);
		
		if(! next("each right parenthesis", "expression \")\" statement", WaebricSymbol.RPARANTHESIS)) {
			return null; // Invalid syntax
		}
		
		// Parse sub-statement
		Statement subStatement = parseStatement("each statement", "\") statement");
		statement.setStatement(subStatement);
		
		return statement;
	}
	
	/**
	 * @see Statement.LetStatement
	 * @return
	 */
	public Statement.LetStatement parseLetStatement() {
		Statement.LetStatement statement = new Statement.LetStatement();
		
		if(! next("let keyword", "\"let\"", WaebricKeyword.LET)) {
			return null; // Invalid syntax
		}
	
		// Parse assignments
		while(tokens.hasNext() && ! tokens.peek(1).getLexeme().equals(WaebricKeyword.IN)) {
			Assignment assignment = parseAssignment();
			statement.addAssignment(assignment);
		}
		
		if(statement.getAssignmentCount() == 0) {
			reportMissingToken("let assignment", "\"let\" assignments+ \"in\"");
		}
		
		next("let in keyword", "assignments \"in\" statements", WaebricKeyword.IN);
		
		// Parse sub-statements
		while(tokens.hasNext() && ! tokens.peek(1).getLexeme().equals(WaebricKeyword.END)) {
			Statement subStatement = parseStatement("let sub-statement", "\"in\" statements \"end\"");
			statement.addStatement(subStatement);
		}
		
		if(! next("let end keyword", "\"in\" statements \"end\"", WaebricKeyword.END)) {
			return null; // Invalid syntax
		}
		
		return statement;
	}
	
	/**
	 * @see Statement.StatementCollection
	 * @return
	 */
	public Statement.StatementCollection parseStatementCollection() {
		Statement.StatementCollection statement = new Statement.StatementCollection();
		
		if(! next("statement collection opening", "\"{\" statements", WaebricSymbol.LCBRACKET)) {
			return null; // Invalid syntax
		}
		
		// Parse sub-statements
		while(tokens.hasNext() && tokens.peek(1).getLexeme().equals(WaebricSymbol.RCBRACKET)) {
			Statement subStatement = parseStatement("statement collection", "\"{\" { statement, \",\" } \"}\"");
			statement.addStatement(subStatement);
		}
		
		if(! next("statement collection closure", "statements \"}\"", WaebricSymbol.RCBRACKET)) {
			return null; // Invalid syntax
		}
		
		return statement;
	}
	
	/**
	 * @see Statement.CommentStatement
	 * @return
	 */
	public Statement.CommentStatement parseCommentStatement() {
		Statement.CommentStatement statement = new Statement.CommentStatement();
		
		// Expect comment keyword
		if(! next("comment keyword", "\"comment\"", WaebricKeyword.COMMENT)) {
			return null; // Invalid keyword, quit statement parse
		}
		
		// Parse text, which expects a text
		if(next("comments text", "\"comments\" text", TokenSort.TEXT)) {
			if(WaebricScanner.isString(current.getLexeme().toString())) {
				StrCon comment = new StrCon(current.getLexeme().toString());
				statement.setComment(comment);
			} else {
				reportUnexpectedToken(current, "comments text", "\"comments\" \" text \"");
			}
		}
		
		return statement;
	}
	
	/**
	 * @see Statement.EchoEmbeddingStatement
	 * @return
	 */
	public Statement.EchoEmbeddingStatement parseEchoEmbeddingStatement() {
		Statement.EchoEmbeddingStatement statement = new Statement.EchoEmbeddingStatement();
		
		if(! next("echo keyword", "\"echo\"", WaebricKeyword.ECHO)) {
			return null; // Invalid syntax
		}
		
		if(next("echo embedding", "\"echo\" embedding", TokenSort.STRCON)) {
			// TODO: Embedding!
		} else {
			return null; // Invalid syntax
		}
		
		if(! next("echo closure", "\"echo\" embedding \";\"", WaebricSymbol.SEMICOLON)) {
			return null; // Invalid syntax
		}
		
		return statement;
	}
	
	/**
	 * @see Statement.EchoExpressionStatement
	 * @return
	 */
	public Statement.EchoExpressionStatement parseEchoExpressionStatement() {
		Statement.EchoExpressionStatement statement = new Statement.EchoExpressionStatement();
		
		if(! next("echo keyword", "\"echo\"", WaebricKeyword.ECHO)) { 
			return null; // Invalid syntax
		}
		
		Expression expression = parseExpression("echo expression", "\"echo\" expression \";\"");
		statement.setExpression(expression);
		
		if(! next("echo closure", "\"echo\" expression \";\"", WaebricSymbol.SEMICOLON)) {
			return null; // Invalid syntax
		}
		
		return statement;
	}
	
	/**
	 * @see Statement.CDataStatement
	 * @return
	 */
	public Statement.CDataStatement parseCDataStatement() {
		Statement.CDataStatement statement = new Statement.CDataStatement();
		
		if(! next("cdata keyword", "\"cdata\"", WaebricKeyword.CDATA)) {
			return null; // Invalid syntax
		}

		Expression expression = parseExpression("cdata expression", "\"cdata\" expression \";\"");
		statement.setExpression(expression);
		
		if(! next("cdata closure", "\"echo\" expression \";\"", WaebricSymbol.SEMICOLON)) {
			return null; // Invalid syntax
		}
		
		return statement;
	}
	
	/**
	 * @see Statement.YieldStatement
	 * @return
	 */
	public Statement.YieldStatement parseYieldStatement() {
		Statement.YieldStatement statement = new Statement.YieldStatement();
		
		if(! next("yield keyword", "\"yield\"", WaebricKeyword.YIELD)) {
			return null; // Invalid syntax
		}
		
		if(! next("yield closure", "\"yield\" \";\"", WaebricSymbol.SEMICOLON)) {
			return null; // Invalid syntax
		}
		
		return statement;
	}
	
	/**
	 * @see Assignment
	 * @return
	 */
	public Assignment parseAssignment() {
		if(! tokens.hasNext(2)) {
			reportMissingToken("assignment", "var \"=\" or identifier \"(\"");
			return null;
		}
			
		if(tokens.peek(2).getLexeme().equals(WaebricSymbol.EQUAL_SIGN)) {
			return parseVarAssignment();
		} else if(tokens.peek(2).getLexeme().equals(WaebricSymbol.LPARANTHESIS)) {
			return parseIdConAssignment();
		} else {
			reportUnexpectedToken(tokens.peek(2), "assignment", "var \"=\" or identifier \"(\"");
			return null;
		}
	}
	
	/**
	 * @see Assignment.VarAssignment
	 * @return
	 */
	public Assignment.VarAssignment parseVarAssignment() {
		Assignment.VarAssignment assignment = new Assignment.VarAssignment();
		
		// Parse variable
		Var var = parseVar("assignment var", "var \"=\"");
		assignment.setVar(var);
		
		next("assignment equals sign", "var \"=\"", WaebricSymbol.EQUAL_SIGN);
		
		// Parse expression
		Expression expression = parseExpression("var assignment expression", "var \"=\" expression");
		assignment.setExpression(expression);
		
		return assignment;
	}
	
	/**
	 * @see Assignment.IdConAssignment
	 * @return
	 */
	public Assignment.IdConAssignment parseIdConAssignment() {
		Assignment.IdConAssignment assignment = new Assignment.IdConAssignment();
		
		// Parse identifier
		if(next("assignment identifier", "identifier \"(\")", TokenSort.IDCON)) {
			IdCon identifier = new IdCon(current.getLexeme().toString());
			assignment.setIdentifier(identifier);
		}
		
		// Parse formals
		Formals formals = parseFormals();
		assignment.setFormals(formals);
		
		next("id assignment equals", "formals \"=\" statement", WaebricSymbol.EQUAL_SIGN);
		
		// Parse statement
		Statement subStatement = parseStatement("id assignment statement ", "\"=\" statement");
		assignment.setStatement(subStatement);
		
		return assignment;
	}
	
	/**
	 * @see Formals
	 * @param formals
	 */
	public Formals parseFormals() {
		Formals formals = new Formals();
		
		// Expect left parenthesis
		next("formals opening parenthesis", "left parenthesis", WaebricSymbol.LPARANTHESIS);
		
		while(tokens.hasNext()) {
			if(tokens.peek(1).getLexeme().equals(WaebricSymbol.RPARANTHESIS)) {
				break; // End of formals found, break while
			}
			
			// Parse variable
			Var var = parseVar("formals variable", "\"( var \")\"");
			formals.addVar(var);
			
			// While not end of formals, comma separator is expected
			if(tokens.hasNext() && ! tokens.peek(1).getLexeme().equals(WaebricSymbol.RPARANTHESIS)) {
				next("arguments separator", "argument \",\" argument", WaebricSymbol.COMMA);
			}
		}
		
		// Expect right parenthesis
		next("formals opening parenthesis", "left parenthesis", WaebricSymbol.RPARANTHESIS);
		
		return formals;
	}

	/**
	 * @see Expression
	 * @see ExpressionParser
	 * 
	 * @param name
	 * @param syntax
	 * @return
	 */
	public Expression parseExpression(String name, String syntax) {
		// Delegate parse to expression parser
		return expressionParser.parseExpression(name, syntax);
	}
	
	/**
	 * @see Var
	 * @see ExpressionParser
	 * 
	 * @param name
	 * @param syntax
	 * @return
	 */
	public Var parseVar(String name, String syntax) {
		// Delegate parse to expression parser
		return expressionParser.parseVar(name, syntax);
	}
	
	/**
	 * @see Predicate
	 * @see PredicateParser
	 * @return
	 */
	public Predicate parsePredicate() {
		// No error reporting arguments needed, as predicates are only used in if-statements
		return predicateParser.parsePredicate();
	}

}