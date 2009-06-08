package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.AbstractSyntaxNodeList;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.basic.StrCon;
import org.cwi.waebric.parser.ast.expression.Var;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.ast.statement.Assignment;
import org.cwi.waebric.parser.ast.statement.Formals;
import org.cwi.waebric.parser.ast.statement.Statement;
import org.cwi.waebric.parser.ast.statement.Statement.EmbeddingMarkupsStatement;
import org.cwi.waebric.parser.ast.statement.Statement.ExpressionMarkupsStatement;
import org.cwi.waebric.parser.ast.statement.Statement.StatementMarkupsStatement;
import org.cwi.waebric.parser.ast.statement.predicate.Predicate;
import org.cwi.waebric.parser.exception.SyntaxException;
import org.cwi.waebric.scanner.WaebricScanner;
import org.cwi.waebric.scanner.token.WaebricToken;
import org.cwi.waebric.scanner.token.WaebricTokenIterator;
import org.cwi.waebric.scanner.token.WaebricTokenSort;

/**
 * module languages/waebric/syntax/Statements
 * 
 * @author Jeroen van Schagen
 * @date 27-05-2009
 */
class StatementParser extends AbstractParser {

	private final EmbeddingParser embeddingParser;
	private final ExpressionParser expressionParser;
	private final PredicateParser predicateParser;
	private final MarkupParser markupParser;
	
	/**
	 * Construct statement parser.
	 * @param tokens
	 * @param exceptions
	 */
	public StatementParser(WaebricTokenIterator tokens, List<SyntaxException> exceptions) {
		super(tokens, exceptions);
		
		// Construct sub parsers
		expressionParser = new ExpressionParser(tokens, exceptions);
		predicateParser = new PredicateParser(tokens, exceptions);
		embeddingParser = new EmbeddingParser(tokens, exceptions);
		markupParser = new MarkupParser(tokens, exceptions);
	}
	
	/**
	 * @see Statement
	 * @param formals Used to determine if an identifier is a mark-up or variable.
	 * @return Statement
	 * @throws SyntaxException 
	 */
	public Statement parseStatement(Formals formals) throws SyntaxException {
		if(tokens.hasNext()) {
			WaebricToken peek = tokens.peek(1); // Retrieve first token of statement
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
				if(tokens.peek(2).getSort().equals(WaebricTokenSort.QUOTE)) {
					// Embedding echo production is followed by a text
					return parseEchoEmbeddingStatement(formals);
				} else {
					// Embedding echo production is followed by an expression
					return parseEchoExpressionStatement(formals);
				}
			} else if(peek.getLexeme().equals(WaebricKeyword.CDATA)) {
				// CData statements start with a cdata keyword
				return parseCDataStatement(formals);
			} else if(peek.getLexeme().equals(WaebricKeyword.YIELD)) {
				// Yield statements start with a yield keyword
				return parseYieldStatement(formals);
			} else if(peek.getSort().equals(WaebricTokenSort.IDCON)) {
				// Mark-up statements always begin with an identifier
				return parseMarkupStatements(formals);
			} else {
				reportUnexpectedToken(tokens.current(), "statement", 
						"\"if\", \"each\", \"let\", \"{\", \"comment\", " +
						"\"echo\", \"cdata\", \"yield\" or Markup");
			}
		}

		return null;
	}
	
	/**
	 * @see Statement.IfStatement
	 * @return IfStatement
	 * @throws SyntaxException 
	 */
	public Statement.IfStatement parseIfStatement(Formals formals) throws SyntaxException {
		Statement.IfStatement statement; 
		
		next(WaebricKeyword.IF, "if keyword", "\"if\" \"(\"");
		
		// Parse "(" Predicate ")
		next(WaebricSymbol.LPARANTHESIS, "Predicate opening \"(\"", "\"if\" \"(\" Predicate");
		Predicate predicate = null;
		try {
			predicate = predicateParser.parsePredicate();
		} catch(SyntaxException e) {
			reportUnexpectedToken(tokens.current(), "Predicate", "\"if(\" Predicate \")\"");
		}
		next(WaebricSymbol.RPARANTHESIS, "Predicate closure \")\"", "\"(\" Predicate \")\"");

		// Parse "true" sub-statement
		Statement trueStatement = null;
		try {
			trueStatement = parseStatement(formals);
		} catch(SyntaxException e) {
			reportUnexpectedToken(tokens.current(), "True statement", "\"if(args)\" Statement");
		}

		// Determine statement type by checking for "else" keyword
		if(tokens.hasNext() && tokens.peek(1).getLexeme().equals(WaebricKeyword.ELSE)) {
			tokens.next(); // Accept and skip "else" keyword
			Statement elseStatement = null;
			try {
				// Parse "false" sub-statement
				elseStatement = parseStatement(formals);
			} catch(SyntaxException e) {
				reportUnexpectedToken(tokens.current(), "False statement", "\"else\" Statement");
			}
			statement = new Statement.IfElseStatement(elseStatement);
		} else {
			statement = new Statement.IfStatement();
		}
							
		statement.setPredicate(predicate);
		statement.setStatement(trueStatement);
		return statement;
	}
	
	/**
	 * @see Statement.EachStatement
	 * @return EachStatement
	 * @throws SyntaxException 
	 */
	public Statement.EachStatement parseEachStatement(Formals formals) throws SyntaxException {
		next(WaebricKeyword.EACH, "Each keyword", "\"each\"");
		
		Statement.EachStatement statement = new Statement.EachStatement();

		// Parse "(" Var ":" Expression ")"
		next(WaebricSymbol.LPARANTHESIS, "Each opening", "\"each\" \"(\" Var");
		statement.setVar(expressionParser.parseVar());
		next(WaebricSymbol.COLON, "Each colon separator", "var \":\" Expression");
		
		try {
			statement.setExpression(expressionParser.parseExpression());
		} catch(SyntaxException e) {
			reportUnexpectedToken(tokens.current(), "Each expression", "each(Var:Expression)");
		}
		
		next(WaebricSymbol.RPARANTHESIS, "Each closure", "Expression \")\" Statement");
		
		// Parse sub-statement
		try {
			statement.setStatement(parseStatement(formals));
		} catch(SyntaxException e) {
			reportUnexpectedToken(tokens.current(), "Each sub-statement", "each(Var:Expression) Statement");
		}
		
		return statement;
	}
	
	/**
	 * @see Statement.LetStatement
	 * @return LetStatement
	 * @throws SyntaxException 
	 */
	public Statement.LetStatement parseLetStatement(Formals formals) throws SyntaxException {
		next(WaebricKeyword.LET, "Let keyword", "\"let\"");

		Statement.LetStatement statement = new Statement.LetStatement();
			
		// Parse assignments
		do {
			statement.addAssignment(parseAssignment(formals));
		} while(tokens.hasNext() && ! tokens.peek(1).getLexeme().equals(WaebricKeyword.IN));

		next(WaebricKeyword.IN, "Let-in keyword", "Assignment+ \"in\" Statement*");
		
		// Parse sub-statements
		while(tokens.hasNext() && ! tokens.peek(1).getLexeme().equals(WaebricKeyword.END)) {
			statement.addStatement(parseStatement(formals));
		}
		
		next(WaebricKeyword.END, "Let-end keyword", "\"in\" Statement* \"end\"");
		return statement;
	}
	
	/**
	 * @see Statement.StatementCollection
	 * @return StatementCollection
	 * @throws SyntaxException 
	 */
	public Statement.StatementCollection parseStatementCollection(Formals formals) throws SyntaxException {
		next(WaebricSymbol.LCBRACKET, "Statement collection opening", "\"{\" Statement*");
		
		Statement.StatementCollection statement = new Statement.StatementCollection();
		while(tokens.hasNext() && ! tokens.peek(1).getLexeme().equals(WaebricSymbol.RCBRACKET)) {
			statement.addStatement(parseStatement(formals));
		}
		
		next(WaebricSymbol.RCBRACKET, "Statement collection closure", "statement* \"}\"");
		return statement;
	}
	
	/**
	 * @see Statement.CommentStatement
	 * @return CommentStatement
	 * @throws SyntaxException 
	 */
	public Statement.CommentStatement parseCommentStatement(Formals formals) throws SyntaxException {
		next(WaebricKeyword.COMMENT, "Comment keyword", "\"comment\"");
		
		Statement.CommentStatement statement = new Statement.CommentStatement();
		next(WaebricTokenSort.QUOTE, "Comments text", "\"comments\" Text");
		if(WaebricScanner.isStringChars(tokens.current().getLexeme().toString())) {
			StrCon comment = new StrCon(tokens.current().getLexeme().toString());
			statement.setComment(comment);
		} else {
			reportUnexpectedToken(tokens.current(), "comments text", "\"comments\" \" Text \"");
		}
		return statement;
	}
	
	/**
	 * @see Statement.EchoEmbeddingStatement
	 * @return EchoEmbedding
	 * @throws SyntaxException 
	 */
	public Statement.EchoEmbeddingStatement parseEchoEmbeddingStatement(Formals formals) throws SyntaxException {
		next(WaebricKeyword.ECHO, "Echo keyword", "\"echo\"");
	
		Statement.EchoEmbeddingStatement statement = new Statement.EchoEmbeddingStatement();
		try {
			statement.setEmbedding(embeddingParser.parseEmbedding(formals));
		} catch(SyntaxException e) {
			reportUnexpectedToken(tokens.current(), "Embedding echo", "\"echo\" Embedding");
		}
		
		next(WaebricSymbol.SEMICOLON, "Echo closure", "\"echo\" Embedding \";\"");
		return statement;
	}

	/**
	 * @see Statement.EchoExpressionStatement
	 * @return EchoExpressionStatement
	 * @throws SyntaxException 
	 */
	public Statement.EchoExpressionStatement parseEchoExpressionStatement(Formals formals) throws SyntaxException {
		next(WaebricKeyword.ECHO, "Echo keyword", "\"echo\"");
		
		Statement.EchoExpressionStatement statement = new Statement.EchoExpressionStatement();
		try {
			statement.setExpression(expressionParser.parseExpression());
		} catch(SyntaxException e) {
			reportUnexpectedToken(tokens.current(), "Embedding echo", "\"echo\" Embedding");
		}

		next(WaebricSymbol.SEMICOLON, "Echo closure \";\"", "\"echo\" expression \";\"");
		return statement;
	}
	
	/**
	 * @see Statement.CDataStatement
	 * @return CData collection
	 * @throws SyntaxException 
	 */
	public Statement.CDataStatement parseCDataStatement(Formals formals) throws SyntaxException {
		next(WaebricKeyword.CDATA, "Cdata keyword", "\"cdata\"");
		
		Statement.CDataStatement statement = new Statement.CDataStatement();
		try {
			statement.setExpression(expressionParser.parseExpression());
		} catch(SyntaxException e) {
			reportUnexpectedToken(tokens.current(), "Embedding echo", "\"echo\" Expression");
		}
		
		next(WaebricSymbol.SEMICOLON, "Cdata closure", "\"echo\" expression \";\"");
		return statement;
	}
	
	/**
	 * @see Statement.YieldStatement
	 * @return YieldStatement
	 * @throws SyntaxException 
	 */
	public Statement.YieldStatement parseYieldStatement(Formals formals) throws SyntaxException {
		Statement.YieldStatement statement = new Statement.YieldStatement();
		next(WaebricKeyword.YIELD, "yield keyword", "\"yield\"");
		next(WaebricSymbol.SEMICOLON, "yield closure", "\"yield\" \";\"");
		return statement;
	}

	/**
	 * @see Statement.MarkupsStatement
	 * @return MarkupsStatement
	 * @throws SyntaxException 
	 */
	public Statement parseMarkupStatements(Formals formals) throws SyntaxException {
		Markup markup = markupParser.parseMarkup(); // Retrieve (first) mark-up
		
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
				markups.add(markupParser.parseMarkup());
			}
			
			if(tokens.hasNext()) {
				// Determine mark-ups statement type
				WaebricToken peek = tokens.peek(1);
				if(peek.getLexeme().equals(WaebricSymbol.SEMICOLON)) {
					// Markup+ Markup ";"
					Markup end = markups.remove(markups.size()-1);
					Statement.MarkupMarkupsStatement statement = new Statement.MarkupMarkupsStatement(markups);
					statement.setMarkup(end);
					return statement;
				} else if(isEmbedding(peek)) {
					EmbeddingMarkupsStatement statement = new EmbeddingMarkupsStatement(markups);
					try {
						statement.setEmbedding(embeddingParser.parseEmbedding(formals));
					} catch(Exception e) {
						reportUnexpectedToken(tokens.current(), "Embedding statement", "Markup+ Embedding");
					}
					next(WaebricSymbol.SEMICOLON, "Markup embedding closure ;", "Markup+ Embedding \";\"");
					return statement;
				} else {
					if(isMarkupFreeStatement(peek)) {
						// Markup+ Statement ";"
						StatementMarkupsStatement statement = new StatementMarkupsStatement(markups);
						statement.setStatement(parseStatement(formals));
						return statement;
					} else if(ExpressionParser.isExpression(peek)) {
						// Markup+ Expression
						ExpressionMarkupsStatement statement = new ExpressionMarkupsStatement(markups);
						try {
							statement.setExpression(expressionParser.parseExpression());
						} catch(SyntaxException e) {
							reportUnexpectedToken(tokens.current(), "Markup expression", "Markup+ Expression \";\"");
						}
						next(WaebricSymbol.SEMICOLON, "Markup expression closure ;", "Markup+ Expression \";\"");
						return statement;
					} else {
						reportUnexpectedToken(peek, "Markups statement", 
								"Markup+ { Markup, Expression, Embedding or Statement }");
					}
				}
			} else {
				reportMissingToken(tokens.current(), "Markups statement", 
						"Markup+ { Markup, Expression, Embedding or Statement }");
			}
		}
		
		return null;
	}
	
	/**
	 * Check if token is a non mark-up related statement.
	 * @param token
	 * @return
	 */
	public static boolean isMarkupFreeStatement(WaebricToken token) {
		if(token.getSort() == WaebricTokenSort.KEYWORD) {
			return token.getLexeme().equals(WaebricKeyword.IF) ||
				   token.getLexeme().equals(WaebricKeyword.CDATA) ||
				   token.getLexeme().equals(WaebricKeyword.COMMENT) ||
				   token.getLexeme().equals(WaebricKeyword.EACH) ||
				   token.getLexeme().equals(WaebricKeyword.LET) ||
				   token.getLexeme().equals(WaebricKeyword.YIELD);
		}
		
		return token.getLexeme().equals(WaebricSymbol.LCBRACKET);
	}
	
	public static boolean isEmbedding(WaebricToken token) {
		return token.getSort() == WaebricTokenSort.QUOTE && token.getLexeme().toString().matches("\\w*<\\w*>\\w*");
	}

	public static boolean isMarkup(WaebricToken token, Formals formals) {
		if(token.getSort() == WaebricTokenSort.IDCON) {
			// Check if token matches a specified variable
			return ! isVar(token, formals);
		}
		
		return false;
	}
	
	public static boolean isVar(WaebricToken token, Formals formals) {
		for(Var var: formals) {
			String name = var.getIdentifier().getLiteral().toString();
			if(token.getLexeme().equals(name)) { return true; }
		}
		
		return false;
	}
	
	/**
	 * @see Assignment
	 * @return Assignment
	 * @throws SyntaxException 
	 */
	public Assignment parseAssignment(Formals formals) throws SyntaxException {
		if(tokens.hasNext(1)) {
			if(isVar(tokens.peek(1), formals)) {
				return parseVarAssignment();
			} else if(tokens.peek(1).getSort() == WaebricTokenSort.IDCON) {
				return parseIdConAssignment(formals);
			} else {
				reportUnexpectedToken(tokens.peek(2), "Assignment", "Var \"=\" Expression or Identifier \"=\" Statement \"(\"");
				return null;
			}
		}
			
		reportMissingToken(tokens.current(), "Assignment", "Var \"=\" Expression or Identifier \"=\" Statement \"(\"");
		return null;
	}
	
	/**
	 * @see Assignment.VarAssignment
	 * @return Assignment
	 * @throws SyntaxException 
	 */
	public Assignment.VarAssignment parseVarAssignment() throws SyntaxException {
		Assignment.VarAssignment assignment = new Assignment.VarAssignment();
		
		assignment.setVar(expressionParser.parseVar());
		next(WaebricSymbol.EQUAL_SIGN, "assignment equals sign", "var \"=\"");
		assignment.setExpression(expressionParser.parseExpression());
		
		return assignment;
	}
	
	/**
	 * @see Assignment.IdConAssignment
	 * @return IdConAssignment
	 * @throws SyntaxException 
	 */
	public Assignment.IdConAssignment parseIdConAssignment(Formals formals) throws SyntaxException {
		next(WaebricTokenSort.IDCON, "Assignment identifier", "Identifier");
		
		Assignment.IdConAssignment assignment = new Assignment.IdConAssignment();
		assignment.setIdentifier(new IdCon(tokens.current().getLexeme().toString()));
		assignment.setFormals(parseFormals());
		
		next(WaebricSymbol.EQUAL_SIGN, "Identifier assignment \"=\"", "Formals \"=\" Statement");
		
		// Parse statement
		Statement subStatement = parseStatement(formals);
		assignment.setStatement(subStatement);
		
		return assignment;
	}
	
	/**
	 * @see Formals
	 * @param formals
	 * @throws SyntaxException 
	 */
	public Formals parseFormals() throws SyntaxException {
		Formals formals = new Formals();
		
		if(tokens.hasNext() && tokens.peek(1).getLexeme().equals(WaebricSymbol.LPARANTHESIS)) {
			tokens.next(); // Accept '(' and go to next symbol
			
			// Parse variables
			while(tokens.hasNext()) {
				if(tokens.peek(1).getLexeme().equals(WaebricSymbol.RPARANTHESIS)) {
					break; // End of formals found, break while
				}
				
				formals.add(expressionParser.parseVar());
				
				// While not end of formals, comma separator is expected
				if(tokens.hasNext() && ! tokens.peek(1).getLexeme().equals(WaebricSymbol.RPARANTHESIS)) {
					next(WaebricSymbol.COMMA, "arguments separator", "argument \",\" argument");
				}
			}
			
			// Expect right parenthesis
			next(WaebricSymbol.RPARANTHESIS, "formals opening parenthesis", "left parenthesis");
		}

		return formals;
	}

}