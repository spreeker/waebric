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
import org.cwi.waebric.parser.ast.statement.Statement.*;
import org.cwi.waebric.parser.ast.statement.embedding.Embedding;
import org.cwi.waebric.parser.ast.statement.predicate.Predicate;
import org.cwi.waebric.parser.exception.SyntaxException;
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
	
	public StatementParser(WaebricTokenIterator tokens, List<SyntaxException> exceptions) {
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
	public Statement parseStatement(Formals formals) {
		if(! tokens.hasNext()) {
		//	reportMissingToken(name, syntax);
			return null;
		}
	
		WaebricToken peek = tokens.peek(1); // Determine statement type based on look-ahead
		if(peek.getLexeme().equals(WaebricKeyword.IF)) {
			// If(-else) statements start with an if keyword
			return parseIfStatement(formals);
		} else if(peek.getLexeme().equals(WaebricKeyword.EACH)) {
			// Each statements start with an each keyword
			return parseEachStatement(formals);
		} else if(peek.getLexeme().equals(WaebricKeyword.LET)) {
			// Let statements start with a let keyword
			return parseLetStatement(formals);
		} else if(peek.getLexeme().equals(WaebricSymbol.LCBRACKET)) {
			// Statement collections start with a {
			return parseStatementCollection(formals);
		} else if(peek.getLexeme().equals(WaebricKeyword.COMMENT)) {
			// Comment statements start with a comments keyword
			return parseCommentStatement(formals);
		} else if(peek.getLexeme().equals(WaebricKeyword.ECHO)) {
			// Embedding echo production is followed by a text
			if(tokens.peek(2).getSort().equals(WaebricTokenSort.QUOTE)) {
				return parseEchoEmbeddingStatement(formals);
			} 
			// Embedding echo production is followed by an expression
			else { return parseEchoExpressionStatement(formals); }
		} else if(peek.getLexeme().equals(WaebricKeyword.CDATA)) {
			// CData statements start with a cdata keyword
			return parseCDataStatement(formals);
		} else if(peek.getLexeme().equals(WaebricKeyword.YIELD)) {
			// Yield statements start with a yield keyword
			return parseYieldStatement(formals);
		} else if(peek.getSort().equals(WaebricTokenSort.IDCON)) {
			return parseMarkupStatements(formals);
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
	public Statement.IfStatement parseIfStatement(Formals formals) {
		if(next("if keyword", "\"if\" \"(\"", WaebricKeyword.IF)) {
			// Parse ( predicate )
			if(next("predicate opening", "\"if\" \"(\" predicate", WaebricSymbol.LPARANTHESIS)) {
				Predicate predicate = parsePredicate();
				if(predicate != null) {
					if(next("predicate closure", "\"(\" predicate \")\"", WaebricSymbol.RPARANTHESIS)) {
						// Parse statement
						Statement trueStatement = parseStatement(formals);
						if(trueStatement != null) {
							Statement.IfStatement statement;
							
							// Determine statement type by checking for "else" keyword
							if(tokens.hasNext() && tokens.peek(1).getLexeme().equals(WaebricKeyword.ELSE)) {
								tokens.next(); // Accept and skip "else" keyword
								Statement elseStatement = parseStatement(formals);
								if(elseStatement == null) {
									reportUnexpectedToken(tokens.current(), "False statement", "else statement");
									return null; // Return empty statement
								}
								
								statement = new Statement.IfElseStatement(elseStatement);
							} else {
								statement = new Statement.IfStatement();
							}
							
							statement.setPredicate(predicate);
							statement.setStatement(trueStatement);
							
							return statement; // Successful parse
						} else {
							reportUnexpectedToken(tokens.current(), "True statement", "if(predicate) statement");
						}
					}
				} else {
					reportUnexpectedToken(tokens.current(), "Predicate", "if(predicate)");
				}
			}
		}

		return null; // Unsuccessful parse, return empty node
	}
	
	/**
	 * @see Statement.EachStatement
	 * @return
	 */
	public Statement.EachStatement parseEachStatement(Formals formals) {
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
		Statement subStatement = parseStatement(formals);
		statement.setStatement(subStatement);
		
		return statement;
	}
	
	/**
	 * @see Statement.LetStatement
	 * @return
	 */
	public Statement.LetStatement parseLetStatement(Formals formals) {
		Statement.LetStatement statement = new Statement.LetStatement();
		
		if(! next("let keyword", "\"let\"", WaebricKeyword.LET)) {
			return null; // Invalid syntax
		}
	
		// Parse assignments
		while(tokens.hasNext() && ! tokens.peek(1).getLexeme().equals(WaebricKeyword.IN)) {
			Assignment assignment = parseAssignment(formals);
			statement.addAssignment(assignment);
		}
		
		if(statement.getAssignmentCount() == 0) {
			reportMissingToken("let assignment", "\"let\" assignments+ \"in\"");
		}
		
		next("let in keyword", "assignments \"in\" statements", WaebricKeyword.IN);
		
		// Parse sub-statements
		while(tokens.hasNext() && ! tokens.peek(1).getLexeme().equals(WaebricKeyword.END)) {
			Statement subStatement = parseStatement(formals);
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
	public Statement.StatementCollection parseStatementCollection(Formals formals) {
		Statement.StatementCollection statement = new Statement.StatementCollection();
		
		if(! next("statement collection opening", "\"{\" statements", WaebricSymbol.LCBRACKET)) {
			return null; // Invalid syntax
		}
		
		// Parse sub-statements
		while(tokens.hasNext() && ! tokens.peek(1).getLexeme().equals(WaebricSymbol.RCBRACKET)) {
			Statement subStatement = parseStatement(formals);
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
	public Statement.CommentStatement parseCommentStatement(Formals formals) {
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
	public Statement.EchoEmbeddingStatement parseEchoEmbeddingStatement(Formals formals) {
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
	public Statement.EchoExpressionStatement parseEchoExpressionStatement(Formals formals) {
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
	public Statement.CDataStatement parseCDataStatement(Formals formals) {
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
	public Statement.YieldStatement parseYieldStatement(Formals formals) {
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
	 * 
	 * @return
	 */
	public Statement parseMarkupStatements(Formals formals) {
		/**
		 * PSEUDO IMPLEMENTATION
		 * 
		 * Parse: Markup+
		 * while(current token is identifier and not var, and thus a markup)
		 * 	identifier is var when it is contained in formals of related functiondef
		 * add parse markup
		 * 
		 * Determine if next token is Statement, Expression, Embedding or Markup
		 * if next is ; then previous token is markup
		 * 	store markup and reduce markup list by 1
		 * if next is quote and embedding "*\w<\w*>\w*"
		 * 	parse embedding and store
		 * check if statement parser can process token stream, then its statement
		 * else check if expression parser can process etc..
		 * else report unexpected token
		 * 	
		 */
		
		
		Markup markup = parseMarkup(); // Retrieve mark-up
		
		if(tokens.hasNext() && tokens.peek(1).getLexeme().equals(WaebricSymbol.SEMICOLON)) {
			// Markup ";"
			Statement.MarkupStatement statement = new Statement.MarkupStatement();
			statement.setMarkup(markup);
			return statement;
		} else {
			// Retrieve remaining mark-up tokens (Markup+)
			AbstractSyntaxNodeList<Markup> markups = new AbstractSyntaxNodeList<Markup>();
			markups.add(markup);
			while(tokens.hasNext() && isMarkup(tokens.peek(1), formals)) {
				markups.add(parseMarkup());
			}
			
			if(tokens.hasNext()) {
				// Determine mark-ups statement type
				WaebricToken peek = tokens.peek(1);
				if(peek.getLexeme().equals(WaebricSymbol.SEMICOLON)) {
					// Markup+ Markup ";"
					Markup end = markups.remove(markups.size()-1);
					Statement.MarkupMarkupsStatement statement = 
						new Statement.MarkupMarkupsStatement(markups);
					statement.setMarkup(end);
					return statement;
				} else if(peek.getSort() == WaebricTokenSort.QUOTE) {
					// Markup+ Embedding TODO: Filter between text and embedding
					EmbeddingMarkupsStatement statement = new EmbeddingMarkupsStatement(markups);
					statement.setEmbedding(parseEmbedding());
					next("Markup embedding closure ;", "Markup+ Embedding \";\"", WaebricSymbol.SEMICOLON);
					return statement;
				} else {
					if(isExpression()) {
						// Markup+ Expression
						ExpressionMarkupsStatement statement = new ExpressionMarkupsStatement(markups);
						statement.setExpression(parseExpression("Markup expression", "Markup+ Expression \";\""));
						next("Markup expression closure ;", "Markup+ Expression \";\"", WaebricSymbol.SEMICOLON);
						return statement;
					} else if(isStatement(formals)) {
						// Markup+ Statement ";"
						StatementMarkupsStatement statement = new StatementMarkupsStatement(markups);
						statement.setStatement(parseStatement(formals));
						return statement;
					} else {
						// Unknown
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
	
	private boolean isStatement(Formals formals) {
		List<SyntaxException> e = new java.util.ArrayList<SyntaxException>();
		WaebricTokenIterator i = tokens.clone();
		StatementParser p = new StatementParser(i, e);
		p.parseStatement(formals);
		return e.size() == 0;
	}
	
	private boolean isExpression() {
		List<SyntaxException> e = new java.util.ArrayList<SyntaxException>();
		WaebricTokenIterator i = tokens.clone();
		ExpressionParser p = new ExpressionParser(i, e);
		p.parseExpression("","");
		return e.size() == 0;
	}
	
	public static boolean isMarkup(WaebricToken token, Formals formals) {
		if(token.getSort() == WaebricTokenSort.IDCON) {
			// Check if token matches a specified variable
			return ! isVar(token, formals);
		}
		
		return false;
	}
	
	private static boolean isVar(WaebricToken token, Formals formals) {
		for(Var var: formals) {
			String name = var.getIdentifier().getLiteral().toString();
			if(token.getLexeme().equals(name)) { return true; }
		}
		
		return false;
	}
	
	/**
	 * @see Assignment
	 * @return
	 */
	public Assignment parseAssignment(Formals formals) {
		if(! tokens.hasNext(2)) {
			reportMissingToken("assignment", "var \"=\" or identifier \"(\"");
			return null;
		}
			
		if(tokens.peek(2).getLexeme().equals(WaebricSymbol.EQUAL_SIGN)) {
			return parseVarAssignment();
		} else if(tokens.peek(2).getLexeme().equals(WaebricSymbol.LPARANTHESIS)) {
			return parseIdConAssignment(formals);
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
	public Assignment.IdConAssignment parseIdConAssignment(Formals formals) {
		Assignment.IdConAssignment assignment = new Assignment.IdConAssignment();
		
		// Parse identifier
		if(next("assignment identifier", "identifier", WaebricTokenSort.IDCON)) {
			IdCon identifier = new IdCon(current.getLexeme().toString());
			assignment.setIdentifier(identifier);
		}
		
		// Parse formals
		assignment.setFormals(parseFormals());
		
		next("id assignment equals", "formals \"=\" statement", WaebricSymbol.EQUAL_SIGN);
		
		// Parse statement
		Statement subStatement = parseStatement(formals);
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