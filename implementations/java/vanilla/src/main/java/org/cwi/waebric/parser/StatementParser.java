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
	
		// Delegate parse to sub-parse function
		Token peek = tokens.peek(1);
		if(peek.getLexeme().equals(WaebricKeyword.IF)) {
			// If(-else) statement
			return parseIfStatement();
		} else if(peek.getLexeme().equals(WaebricKeyword.EACH)) {
			// Each statement
			return parseEachStatement();
		} else if(peek.getLexeme().equals(WaebricKeyword.LET)) {
			// Let statement
			return parseLetStatement();
		} else if(peek.getLexeme().equals(WaebricSymbol.LCBRACKET)) {
			// Statement collection
			return parseStatementCollection();
		} else if(peek.getLexeme().equals(WaebricKeyword.COMMENT)) {
			// Comment statement
			return parseCommentStatement();
		} else if(peek.getLexeme().equals(WaebricKeyword.ECHO)) {
			// Echo statement
			if(tokens.peek(2).getSort().equals(TokenSort.TEXT)) {
				// Echo embedding (text)
				return parseEchoEmbeddingStatement();
			} else {
				// Echo expression
				return parseEchoExpressionStatement();
			}
		} else if(peek.getLexeme().equals(WaebricKeyword.CDATA)) {
			// CData statement
			return parseCDataStatement();
		} else if(peek.getLexeme().equals(WaebricKeyword.YIELD)) {
			// Yield statement
			return parseYieldStatement();
		} else {
			reportUnexpectedToken(peek, "statement", 
					"\"if\", \"each\", \"let\", \"{\", \"comment\", \"echo\", \"cdata\" or \"yield\"");
			return null;
		}
	}
	
	/**
	 * @see Statement.IfStatement
	 * @return
	 */
	public Statement.IfStatement parseIfStatement() {
		next("if keyword", "\"if\" \"(\"", WaebricKeyword.IF);
		
		next("predicate opening", "\"if\" \"(\" predicate", WaebricSymbol.LPARANTHESIS);
		Predicate predicate = parsePredicate(); // Parse predicate
		next("predicate closure", "\"(\" predicate \")\"", WaebricSymbol.RPARANTHESIS);
		
		// Parse statement
		Statement subStatement = parseStatement(
				"if statement", "\"if\" \"(\" predicate \")\" statement");

		Statement.IfStatement statement = null;
		if(tokens.hasNext() && tokens.peek(1).getLexeme().equals(WaebricKeyword.ELSE)) {
			statement = new Statement.IfElseStatement(); // If-else statement
			tokens.next(); // Skip else keyword
			
			// Parse else sub-statement
			Statement secondStatement = parseStatement("else statement", "\"else\" statement");
			((Statement.IfElseStatement) statement).setSecondStatement(secondStatement);
		} else {
			statement = new Statement.IfStatement(); // If statement
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
		
		next("each keyword", "\"each\"", WaebricKeyword.EACH);
		next("each left parenthesis", "\"each\" \"(\" var", WaebricSymbol.LPARANTHESIS);
		
		// Parse variable
		Var var = parseVar("each var", "\"(\" var \":\"");
		statement.setVar(var);
		
		next("each colon separator", "var \":\" expression", WaebricSymbol.COLON);
		
		// Parse expression
		Expression expression = parseExpression("each expression", "\":\" expression \")\"");
		statement.setExpression(expression);
		
		next("each right parenthesis", "expression \")\" statement", WaebricSymbol.RPARANTHESIS);
		
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
		
		next("let keyword", "\"let\"", WaebricKeyword.LET);
	
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
		
		next("let end keyword", "\"in\" statements \"end\"", WaebricKeyword.END);
		
		return statement;
	}
	
	/**
	 * @see Statement.StatementCollection
	 * @return
	 */
	public Statement.StatementCollection parseStatementCollection() {
		Statement.StatementCollection statement = new Statement.StatementCollection();
		
		next("statement collection opening", "\"{\" statements", WaebricSymbol.LCBRACKET);
		
		// Parse sub-statements
		while(tokens.hasNext() && tokens.peek(1).getLexeme().equals(WaebricSymbol.RCBRACKET)) {
			Statement subStatement = parseStatement("statement collection", "\"{\" { statement, \",\" } \"}\"");
			statement.addStatement(subStatement);
		}
		
		next("statement collection closure", "statements \"}\"", WaebricSymbol.RCBRACKET);
		
		return statement;
	}
	
	/**
	 * @see Statement.CommentStatement
	 * @return
	 */
	public Statement.CommentStatement parseCommentStatement() {
		Statement.CommentStatement statement = new Statement.CommentStatement();
		
		next("comment keyword", "\"comment\"", WaebricKeyword.COMMENT);
		
		if(next("comments statement text", "\"comments\" text", TokenSort.TEXT)) {
			StrCon comment = new StrCon(current.getLexeme().toString());
			statement.setComment(comment);
		}
		
		return statement;
	}
	
	/**
	 * @see Statement.EchoEmbeddingStatement
	 * @return
	 */
	public Statement.EchoEmbeddingStatement parseEchoEmbeddingStatement() {
		Statement.EchoEmbeddingStatement statement = new Statement.EchoEmbeddingStatement();
		
		next("echo keyword", "\"echo\"", WaebricKeyword.ECHO);
		
		if(next("echo embedding", "\"echo\" embedding", TokenSort.TEXT)) {
			// TODO: Embedding!
		}
		
		next("echo closure", "\"echo\" embedding \";\"", WaebricSymbol.SEMICOLON);
		
		return statement;
	}
	
	/**
	 * @see Statement.EchoExpressionStatement
	 * @return
	 */
	public Statement.EchoExpressionStatement parseEchoExpressionStatement() {
		Statement.EchoExpressionStatement statement = new Statement.EchoExpressionStatement();
		
		next("echo keyword", "\"echo\"", WaebricKeyword.ECHO);
		
		Expression expression = parseExpression("echo expression", "\"echo\" expression \";\"");
		statement.setExpression(expression);
		
		next("echo closure", "\"echo\" expression \";\"", WaebricSymbol.SEMICOLON);
		
		return statement;
	}
	
	/**
	 * @see Statement.CDataStatement
	 * @return
	 */
	public Statement.CDataStatement parseCDataStatement() {
		Statement.CDataStatement statement = new Statement.CDataStatement();
		
		next("cdata keyword", "\"cdata\"", WaebricKeyword.CDATA);

		Expression expression = parseExpression("cdata expression", "\"cdata\" expression \";\"");
		statement.setExpression(expression);
		
		next("cdata closure", "\"echo\" expression \";\"", WaebricSymbol.SEMICOLON);
		
		return statement;
	}
	
	/**
	 * @see Statement.YieldStatement
	 * @return
	 */
	public Statement.YieldStatement parseYieldStatement() {
		Statement.YieldStatement statement = new Statement.YieldStatement();
		
		next("yield keyword", "\"yield\"", WaebricKeyword.YIELD);
		next("yield closure", "\"yield\" \";\"", WaebricSymbol.SEMICOLON);
		
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
		if(next("assignment identifier", "identifier \"(\")", TokenSort.IDENTIFIER)) {
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
		Var var = new Var();
		expressionParser.parse(var);
		return var;
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