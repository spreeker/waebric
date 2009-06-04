package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.AbstractSyntaxNodeList;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.basic.StrCon;
import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.expression.Var;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.ast.statement.Assignment;
import org.cwi.waebric.parser.ast.statement.Formals;
import org.cwi.waebric.parser.ast.statement.Statement;
import org.cwi.waebric.parser.ast.statement.embedding.Embedding;
import org.cwi.waebric.parser.ast.statement.predicate.Predicate;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.WaebricScanner;
import org.cwi.waebric.scanner.token.WaebricToken;
import org.cwi.waebric.scanner.token.WaebricTokenIterator;
import org.cwi.waebric.scanner.token.WaebricTokenSort;

/**
 * Statement parser
 * 
 * module languages/waebric/syntax/Statements
 * 
 * @author Jeroen van Schagen
 * @date 27-05-2009
 */
class StatementParser extends AbstractParser {

	private final EmbeddingParser embeddingParser;
	private final ExpressionParser expressionParser;
	private final PredicateParser predicateParser;
	private final MarkupParser markupPaser;
	
	public StatementParser(WaebricTokenIterator tokens, List<ParserException> exceptions) {
		super(tokens, exceptions);
		
		// Construct sub parsers
		expressionParser = new ExpressionParser(tokens, exceptions);
		predicateParser = new PredicateParser(tokens, exceptions);
		embeddingParser = new EmbeddingParser(tokens, exceptions);
		markupPaser = new MarkupParser(tokens, exceptions);
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
	
		WaebricToken peek = tokens.peek(1); // Determine statement type based on look-ahead
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
			if(tokens.peek(2).getSort().equals(WaebricTokenSort.QUOTE)) {
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
		} else if(peek.getSort().equals(WaebricTokenSort.IDCON)) {
			return parseMarkupStatements();
		} else {
			// Token stream cannot be seen as statement
			reportUnexpectedToken(peek, "statement", 
					"\"if\", \"each\", \"let\", \"{\", \"comment\", " +
					"\"echo\", \"cdata\", \"yield\" or Markup");
			tokens.next(); // Skip token to prevent infinite loops
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
		
		next("each keyword", "\"each\"", WaebricKeyword.EACH);
		next("each left parenthesis", "\"each\" \"(\" var", WaebricSymbol.LPARANTHESIS);
		
		// Parse variables
		statement.setVar(parseVar("each var", "\"(\" var \":\""));
		
		next("each colon separator", "var \":\" expression", WaebricSymbol.COLON);
		
		// Parse expression
		statement.setExpression(parseExpression("each expression", "\":\" expression \")\""));
		
		next("each right parenthesis", "expression \")\" statement", WaebricSymbol.RPARANTHESIS);
		
		// Parse (sub) statement
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
		if(next("comments text", "\"comments\" text", WaebricTokenSort.QUOTE)) {
			if(WaebricScanner.isStringChars(current.getLexeme().toString())) {
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
		next("echo keyword", "\"echo\"", WaebricKeyword.ECHO);
	
		Statement.EchoEmbeddingStatement statement = new Statement.EchoEmbeddingStatement();
		statement.setEmbedding(parseEmbedding());
		
		next("echo closure", "\"echo\" embedding \";\"", WaebricSymbol.SEMICOLON);
		
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
		if(next("assignment identifier", "identifier", WaebricTokenSort.IDCON)) {
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
		
		if(tokens.hasNext() && tokens.peek(1).getLexeme().equals(WaebricSymbol.LPARANTHESIS)) {
			tokens.next(); // Accept '(' and go to next symbol
			
			// Parse variables
			while(tokens.hasNext()) {
				if(tokens.peek(1).getLexeme().equals(WaebricSymbol.RPARANTHESIS)) {
					break; // End of formals found, break while
				}
				
				formals.add(parseVar("formals variable", "\"( var \")\""));
				
				// While not end of formals, comma separator is expected
				if(tokens.hasNext() && ! tokens.peek(1).getLexeme().equals(WaebricSymbol.RPARANTHESIS)) {
					next("arguments separator", "argument \",\" argument", WaebricSymbol.COMMA);
				}
			}
			
			// Expect right parenthesis
			next("formals opening parenthesis", "left parenthesis", WaebricSymbol.RPARANTHESIS);
		}

		return formals;
	}
	
	/**
	 * 
	 * @return
	 */
	public Statement parseMarkupStatements() {
		Markup markup = parseMarkup(); // Retrieve mark-up
		
		if(tokens.hasNext() && tokens.peek(1).getLexeme().equals(WaebricSymbol.SEMICOLON)) {
			Statement.MarkupStatement statement = new Statement.MarkupStatement();
			statement.setMarkup(markup);
			return statement;
		} else {
			// Retrieve remaining mark-up tokens
			AbstractSyntaxNodeList<Markup> markups = new AbstractSyntaxNodeList<Markup>();
			markups.add(markup);
			
			// TODO: Work with actual formals
			while(isMarkup(tokens.current(), new Formals())) {
				markups.add(parseMarkup());
			}
			
			if(tokens.hasNext()) {
				WaebricToken peek = tokens.peek(1); // Determine mark-ups statement type
				if(peek.getLexeme().equals(WaebricSymbol.SEMICOLON)) {
					Markup end = markups.remove(markups.size()-1);
					Statement.MarkupMarkupsStatement statement = 
						new Statement.MarkupMarkupsStatement(markups);
					statement.setMarkup(end);
					return statement;
				} else if(peek.getSort() == WaebricTokenSort.IDCON) {
					return parseStatementMarkupsStatement(markups);
				} else if(peek.getSort() == WaebricTokenSort.QUOTE) {
					return parseEmbeddingMarkupsStatement(markups);
				} else {
					// Only remaining alternatives are expressions or statements
					if(isStatement()) {
						return parseStatementMarkupsStatement(markups);
					} else if(isExpression()) {
						return parseExpressionMarkupsStatement(markups);
					} else {
						reportUnexpectedToken(peek, "Markups statement", 
								"Markup+ { Markup, Expression, Embedding or Statement }");
					}
				}
			} else {
				reportMissingToken("Markups statement", "Markup+ { Markup, Expression, Embedding or Statement }");
			}
		}
		
		return null;
	}

	/**
	 * 
	 * @param markups
	 * @return
	 */
	private Statement.EmbeddingMarkupsStatement parseEmbeddingMarkupsStatement(
			AbstractSyntaxNodeList<Markup> markups) {
		Statement.EmbeddingMarkupsStatement statement = 
			new Statement.EmbeddingMarkupsStatement(markups);
		statement.setEmbedding(parseEmbedding());
		next("Markup statement closure ;", "Markup+ Embedding \";\"", WaebricSymbol.SEMICOLON);
		return statement;
	}
	
	/**
	 * 
	 * @param markups
	 * @return
	 */
	private Statement.StatementMarkupsStatement parseStatementMarkupsStatement(
			AbstractSyntaxNodeList<Markup> markups) {
		Statement.StatementMarkupsStatement statement = 
			new Statement.StatementMarkupsStatement(markups);
		statement.setStatement(parseStatement("Markup statement", "Markup+ Statement \";\""));
		next("Markup statement closure ;", "Markup+ Embedding \";\"", WaebricSymbol.SEMICOLON);
		return statement;
	}

	private boolean isStatement() {
		List<ParserException> e = new java.util.ArrayList<ParserException>();
		WaebricTokenIterator i = tokens.clone();
		StatementParser p = new StatementParser(i, e);
		p.parseStatement("","");
		return e.size() == 0;
	}
	
	/**
	 * 
	 * @param markups
	 * @return
	 */
	private Statement.ExpressionMarkupsStatement parseExpressionMarkupsStatement(
			AbstractSyntaxNodeList<Markup> markups) {
		Statement.ExpressionMarkupsStatement statement =
			new Statement.ExpressionMarkupsStatement(markups);
		statement.setExpression(parseExpression("Markup statement", "Markup+ Expression \";\""));
		next("Markup statement closure ;", "Markup+ Expression \";\"", WaebricSymbol.SEMICOLON);
		return statement;
	}
	
	private boolean isExpression() {
		List<ParserException> e = new java.util.ArrayList<ParserException>();
		WaebricTokenIterator i = tokens.clone();
		ExpressionParser p = new ExpressionParser(i, e);
		p.parseExpression("","");
		return e.size() == 0;
	}
	
	private boolean isVar(WaebricToken token, Formals formals) {
		for(Var var: formals) {
			String name = var.getIdentifier().getLiteral().toString();
			if(token.getLexeme().equals(name)) { return true; }
		}
		
		return false;
	}
	
	private boolean isMarkup(WaebricToken token, Formals formals) {
		if(isVar(token, formals)) { return false; } 
		
		List<ParserException> e = new java.util.ArrayList<ParserException>();
		WaebricTokenIterator i = tokens.clone();
		MarkupParser p = new MarkupParser(i, e);
		p.parseMarkup();
		return e.size() == 0;
	}

	// Parse delegations
	public Expression parseExpression(String name, String syntax) {
		return expressionParser.parseExpression(name, syntax);
	}
	
	public Var parseVar(String name, String syntax) {
		return expressionParser.parseVar(name, syntax);
	}

	public Predicate parsePredicate() {
		return predicateParser.parsePredicate();
	}
	
	private Embedding parseEmbedding() {
		return embeddingParser.parseEmbedding();
	}
	
	private Markup parseMarkup() {
		return markupPaser.parseMarkup();
	}

}