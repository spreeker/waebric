package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.lexer.token.Token;
import org.cwi.waebric.lexer.token.TokenIterator;
import org.cwi.waebric.lexer.token.WaebricTokenSort;
import org.cwi.waebric.parser.ast.AbstractSyntaxNodeList;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.basic.StrCon;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.ast.module.function.Formals;
import org.cwi.waebric.parser.ast.statement.Assignment;
import org.cwi.waebric.parser.ast.statement.Statement;
import org.cwi.waebric.parser.ast.statement.Statement.MarkupsEmbedding;
import org.cwi.waebric.parser.ast.statement.Statement.MarkupsExpression;
import org.cwi.waebric.parser.ast.statement.Statement.MarkupsStatement;
import org.cwi.waebric.parser.ast.statement.predicate.Predicate;

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
	public StatementParser(TokenIterator tokens, List<SyntaxException> exceptions) {
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
	public Statement parseStatement() throws SyntaxException {
		if(tokens.hasNext()) {
			Token peek = tokens.peek(1); // Retrieve first token of statement
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
				if(tokens.hasNext(2) && tokens.peek(2).getSort() == WaebricTokenSort.EMBEDDING) {
					// Embedding echo production is followed by a text
					return parseEchoEmbeddingStatement();
				} else {
					// Embedding echo production is followed by an expression
					return parseEchoExpressionStatement();
				}
			} else if(peek.getLexeme().equals(WaebricKeyword.CDATA)) {
				// CData statements start with a cdata keyword
				return parseCDataStatement();
			} else if(peek.getLexeme().equals(WaebricKeyword.YIELD)) {
				// Yield statements start with a yield keyword
				return parseYieldStatement();
			} else if(peek.getSort().equals(WaebricTokenSort.IDCON)) {
				// Mark-up statements always begin with an identifier
				return parseMarkupStatements();
			} else {
				reportUnexpectedToken(tokens.current(), "statement", 
						"\"if\", \"each\", \"let\", \"{\", \"comment\", " +
						"\"echo\", \"cdata\", \"yield\" or Markup");
			}
		}

		return null;
	}
	
	/**
	 * @see Statement.If
	 * @return IfStatement
	 * @throws SyntaxException 
	 */
	public Statement.If parseIfStatement() throws SyntaxException {
		Statement.If statement; 
		
		next(WaebricKeyword.IF, "if keyword", "\"if\" \"(\"");

		// Parse "(" Predicate ")
		next(WaebricSymbol.LPARANTHESIS, "Predicate opening \"(\"", "\"if\" \"(\" Predicate");
		Predicate predicate = predicateParser.parsePredicate();
		next(WaebricSymbol.RPARANTHESIS, "Predicate closure \")\"", "\"(\" Predicate \")\"");

		// Parse "true" sub-statement
		Statement trueStatement = parseStatement();
		
		// Determine statement type by checking for "else" keyword
		if(tokens.hasNext() && tokens.peek(1).getLexeme().equals(WaebricKeyword.ELSE)) {
			tokens.next(); // Accept and skip "else" keyword
			Statement elseStatement = parseStatement(); // Parse "false" sub-statement
			statement = new Statement.IfElse(predicate, trueStatement, elseStatement);
		} else {
			statement = new Statement.If(predicate, trueStatement);
		}
		
		return statement;
	}
	
	/**
	 * @see Statement.Each
	 * @return EachStatement
	 * @throws SyntaxException 
	 */
	public Statement.Each parseEachStatement() throws SyntaxException {
		next(WaebricKeyword.EACH, "Each keyword", "\"each\"");
		
		Statement.Each statement = new Statement.Each();

		// Parse "(" Var ":" Expression ")"
		next(WaebricSymbol.LPARANTHESIS, "Each opening", "\"each\" \"(\" Var");
		next(WaebricTokenSort.IDCON, "Variable", "var \":\" Expression");
		statement.setVar(new IdCon(tokens.current()));
		next(WaebricSymbol.COLON, "Each colon separator", "var \":\" Expression");
		
		statement.setExpression(expressionParser.parseExpression());
		next(WaebricSymbol.RPARANTHESIS, "Each closure", "Expression \")\" Statement");
		
		// Parse sub-statement
		statement.setStatement(parseStatement());
		return statement;
	}
	
	/**
	 * @see Statement.Let
	 * @return LetStatement
	 * @throws SyntaxException 
	 */
	public Statement.Let parseLetStatement() throws SyntaxException {
		next(WaebricKeyword.LET, "Let keyword", "\"let\"");

		Statement.Let statement = new Statement.Let();
			
		// Parse assignments
		do {
			statement.addAssignment(parseAssignment());
		} while(tokens.hasNext() && ! tokens.peek(1).getLexeme().equals(WaebricKeyword.IN));

		next(WaebricKeyword.IN, "Let-in keyword", "Assignment+ \"in\" Statement*");
		
		// Parse sub-statements
		while(tokens.hasNext() && ! tokens.peek(1).getLexeme().equals(WaebricKeyword.END)) {
			statement.addStatement(parseStatement());
		}
		
		next(WaebricKeyword.END, "Let-end keyword", "\"in\" Statement* \"end\"");
		return statement;
	}
	
	/**
	 * @see Statement.Block
	 * @return StatementCollection
	 * @throws SyntaxException 
	 */
	public Statement.Block parseStatementCollection() throws SyntaxException {
		next(WaebricSymbol.LCBRACKET, "Statement collection opening", "\"{\" Statement*");
		
		Statement.Block statement = new Statement.Block();
		while(tokens.hasNext() && ! tokens.peek(1).getLexeme().equals(WaebricSymbol.RCBRACKET)) {
			statement.addStatement(parseStatement());
		}
		
		next(WaebricSymbol.RCBRACKET, "Statement collection closure", "statement* \"}\"");
		return statement;
	}
	
	/**
	 * @see Statement.Comment
	 * @return CommentStatement
	 * @throws SyntaxException 
	 */
	public Statement.Comment parseCommentStatement() throws SyntaxException {
		Statement.Comment statement = new Statement.Comment();
		next(WaebricKeyword.COMMENT, "Comment keyword", "\"comment\"");
		next(WaebricTokenSort.STRING, "Comments text", "\"comments\" String");
		StrCon comment = new StrCon(tokens.current().getLexeme().toString());
		statement.setComment(comment);
		next(WaebricSymbol.SEMICOLON, "Comment closure \";\"", "\"comment\" StrCon \";\"");
		return statement;
	}
	
	/**
	 * @see Statement.EchoEmbedding
	 * @return EchoEmbedding
	 * @throws SyntaxException 
	 */
	public Statement.EchoEmbedding parseEchoEmbeddingStatement() throws SyntaxException {
		next(WaebricKeyword.ECHO, "Echo keyword", "\"echo\"");
	
		Statement.EchoEmbedding statement = new Statement.EchoEmbedding();
		try {
			statement.setEmbedding(embeddingParser.parseEmbedding());
		} catch(SyntaxException e) {
			reportUnexpectedToken(tokens.current(), "Embedding echo", "\"echo\" Embedding");
		}
		
		next(WaebricSymbol.SEMICOLON, "Echo closure", "\"echo\" Embedding \";\"");
		return statement;
	}

	/**
	 * @see Statement.Echo
	 * @return EchoExpressionStatement
	 * @throws SyntaxException 
	 */
	public Statement.Echo parseEchoExpressionStatement() throws SyntaxException {
		Statement.Echo statement = new Statement.Echo();
		next(WaebricKeyword.ECHO, "Echo keyword", "\"echo\"");
		statement.setExpression(expressionParser.parseExpression());
		next(WaebricSymbol.SEMICOLON, "Echo closure \";\"", "\"echo\" expression \";\"");
		return statement;
	}
	
	/**
	 * @see Statement.CDatadiv.span-3.prepend-1 
	 * @return CData collection
	 * @throws SyntaxException 
	 */
	public Statement.CData parseCDataStatement() throws SyntaxException {
		Statement.CData statement = new Statement.CData();
		next(WaebricKeyword.CDATA, "Cdata keyword", "\"cdata\"");
		statement.setExpression(expressionParser.parseExpression());
		next(WaebricSymbol.SEMICOLON, "Cdata closure", "\"echo\" expression \";\"");
		return statement;
	}
	
	/**
	 * @see Statement.Yield
	 * @return YieldStatement
	 * @throws SyntaxException 
	 */
	public Statement.Yield parseYieldStatement() throws SyntaxException {
		Statement.Yield statement = new Statement.Yield();
		next(WaebricKeyword.YIELD, "yield keyword", "\"yield\"");
		next(WaebricSymbol.SEMICOLON, "yield closure", "\"yield\" \";\"");
		return statement;
	}

	/**
	 * @see Statement.AbstractMarkupStatement
	 * @return MarkupsStatement
	 * @throws SyntaxException 
	 */
	public Statement parseMarkupStatements() throws SyntaxException {
		Markup markup = markupParser.parseMarkup(); // Retrieve (first) mark-up
		
		if(tokens.hasNext() && tokens.peek(1).getLexeme().equals(WaebricSymbol.SEMICOLON)) {
			// Markup ";"
			Statement.MarkupStatement statement = new Statement.MarkupStatement();
			statement.setMarkup(markup);
			tokens.next(); // Accept ; and jump to next token
			return statement;
		} else {
			// Retrieve remaining mark-up tokens (Markup+)
			AbstractSyntaxNodeList<Markup> markups = new AbstractSyntaxNodeList<Markup>();
			markups.add(markup);
			while(isMarkup(1, false)) {
				markups.add(markupParser.parseMarkup());
			}
			
			if(tokens.hasNext()) {
				// Determine mark-ups statement type
				Token peek = tokens.peek(1);
				if(peek.getLexeme().equals(WaebricSymbol.SEMICOLON)) {
					// Markup+ Markup ";"
					Markup end = markups.remove(markups.size()-1);
					Statement.MarkupsMarkup statement = new Statement.MarkupsMarkup(markups);
					statement.setMarkup(end);
					next(WaebricSymbol.SEMICOLON, "Markup markup closure ;", "Markup+ Markup \";\"");
					return statement;
				} else if(peek.getSort() == WaebricTokenSort.EMBEDDING) {
					MarkupsEmbedding statement = new MarkupsEmbedding(markups);
					try {
						statement.setEmbedding(embeddingParser.parseEmbedding());
					} catch(Exception e) {
						reportUnexpectedToken(tokens.current(), "Embedding statement", "Markup+ Embedding");
					}
					next(WaebricSymbol.SEMICOLON, "Markup embedding closure ;", "Markup+ Embedding \";\"");
					return statement;
				} else {
					if(StatementParser.isMarkupFreeStatement(peek)) {
						// Markup+ Statement ";"
						MarkupsStatement statement = new MarkupsStatement(markups);
						statement.setStatement(parseStatement());
						return statement;
					} else if(ExpressionParser.isExpression(peek)) {
						// Markup+ Expression
						MarkupsExpression statement = new MarkupsExpression(markups);
						try {
							statement.setExpression(expressionParser.parseExpression());
						} catch(SyntaxException e) {
							reportUnexpectedToken(tokens.current(), "Markup expression", "Markup+ Expression \";\"");
						}
						next(WaebricSymbol.SEMICOLON, "Markup expression closure ;", "Markup+ Expression \";\"");
						return statement;
					} else {
						reportUnexpectedToken(peek, "Markups statement", 
								"Markup+ { Markup, Expression, Embedding or Statement } \";\"");
					}
				}
			} else {
				reportMissingToken(tokens.current(), "Markups statement", 
						"Markup+ { Markup, Expression, Embedding or Statement } \";\"");
			}
		}
		
		return null;
	}
	
	/**
	 * Check if next token is mark-up.
	 * @param k Tokens look-ahead position in iterator
	 * @param first First in mark-up chain?
	 * @return Mark-up?
	 */
	public boolean isMarkup(int k, boolean first) {
		if(tokens.hasNext(k) && tokens.peek(k).getSort() == WaebricTokenSort.IDCON) {
			while(tokens.hasNext(k+1)) { // Absorb attributes
				Token peek = tokens.peek(k+1);
				if(MarkupParser.isAttribute(peek)) {
					if(tokens.hasNext(k+2)) { k+=2; } // Skip attribute symbol and value
					else { break; } // Invalid token space left
				} else {
					break; // No attribute detected
				}
			}
			
			if(tokens.hasNext(k+1) && tokens.peek(k+1).getLexeme().equals(WaebricSymbol.LPARANTHESIS)) {
				return true; // Parenthesis determines a call and is thus a mark-up
			} else if(tokens.hasNext(k+1) && tokens.peek(k+1).getLexeme().equals(WaebricSymbol.SEMICOLON)) {
				return first; // Semicolon marks the end of a mark-up chain, the last element is a variable unless it is alone
			} return true; // Valid mark-up tag
		} else {
			return false; // Invalid token type
		}
	}

	/**
	 * Check if token is a mark-up free statement.
	 * @param token
	 * @return Statement?
	 */
	public static boolean isMarkupFreeStatement(Token token) {
		if(token.getSort() == WaebricTokenSort.KEYWORD) {
			return token.getLexeme().equals(WaebricKeyword.IF) 
				|| token.getLexeme().equals(WaebricKeyword.CDATA)
				|| token.getLexeme().equals(WaebricKeyword.COMMENT)
				|| token.getLexeme().equals(WaebricKeyword.EACH)
				|| token.getLexeme().equals(WaebricKeyword.LET)
				|| token.getLexeme().equals(WaebricKeyword.YIELD)
				|| token.getLexeme().equals(WaebricKeyword.ECHO);
		}
		
		return token.getLexeme().equals(WaebricSymbol.LCBRACKET);
	}

	/**
	 * @see Assignment
	 * @return Assignment
	 * @throws SyntaxException 
	 */
	public Assignment parseAssignment() throws SyntaxException {
		if(tokens.hasNext(2) && tokens.peek(2).getLexeme().equals(WaebricSymbol.LPARANTHESIS)) {
			return parseFuncAssignment();
		} else {
			return parseVarAssignment();
		}
	}
	
	/**
	 * @see Assignment.VarBind
	 * @return Assignment
	 * @throws SyntaxException 
	 */
	public Assignment.VarBind parseVarAssignment() throws SyntaxException {
		Assignment.VarBind assignment = new Assignment.VarBind();
		next(WaebricTokenSort.IDCON, "Variable binding identifier", "IdCon \"=\"");
		assignment.setIdentifier(new IdCon(tokens.current()));
		next(WaebricSymbol.EQUAL_SIGN, "Variable binding \"=\"", "IdCon \"=\"");
		assignment.setExpression(expressionParser.parseExpression());
		next(WaebricSymbol.SEMICOLON, "Var binding closure", "IdCon \"=\" Expression \";\"");
		return assignment;
	}
	
	/**
	 * @see Assignment.FuncBind
	 * @return IdConAssignment
	 * @throws SyntaxException 
	 */
	public Assignment.FuncBind parseFuncAssignment() throws SyntaxException {
		Assignment.FuncBind assignment = new Assignment.FuncBind();
		
		// Parse identifier
		next(WaebricTokenSort.IDCON, "Assignment identifier", "Identifier");
		assignment.setIdentifier(new IdCon(tokens.current()));
		
		// Parse "(" { IdCon "," }* ")" "="
		next(WaebricSymbol.LPARANTHESIS, "Function binding opening parenthesis", "\"(\" IdCon");
		while(tokens.hasNext()) {
			if(tokens.peek(1).getLexeme().equals(WaebricSymbol.RPARANTHESIS)) {
				break; // End of formals found, break while
			}
			
			next(WaebricTokenSort.IDCON, "Function binding identifier", "\"(\" { Identifier, \",\" }* \")\"");
			assignment.addVariable(new IdCon(tokens.current()));
			
			// While not end of formals, comma separator is expected
			if(tokens.hasNext() && ! tokens.peek(1).getLexeme().equals(WaebricSymbol.RPARANTHESIS)) {
				next(WaebricSymbol.COMMA, "Formals separator", "Identifier \",\" Identifier");
			}
		}
		next(WaebricSymbol.RPARANTHESIS, "Function binding closing parenthesis", "IdCon \")\"");
		next(WaebricSymbol.EQUAL_SIGN, "Identifier assignment \"=\"", "Formals \"=\" Statement");
		
		// Parse sub-statement
		assignment.setStatement(parseStatement());		
		return assignment;
	}
	
	/**
	 * @see Formals
	 * @param formals
	 * @throws SyntaxException 
	 */
	public Formals parseFormals() throws SyntaxException {
		if(tokens.hasNext() && tokens.peek(1).getLexeme().equals(WaebricSymbol.LPARANTHESIS)) {
			Formals.RegularFormal formals = new Formals.RegularFormal();
			tokens.next(); // Accept '(' and go to next symbol

			// Parse variables
			while(tokens.hasNext()) {
				if(tokens.peek(1).getLexeme().equals(WaebricSymbol.RPARANTHESIS)) {
					break; // End of formals found, break while
				}
				
				next(WaebricTokenSort.IDCON, "Formals identifier", "\"(\" { Identifier, \",\" }* \")\"");
				formals.addIdentifier(new IdCon(tokens.current()));
				
				// While not end of formals, comma separator is expected
				if(tokens.hasNext() && ! tokens.peek(1).getLexeme().equals(WaebricSymbol.RPARANTHESIS)) {
					next(WaebricSymbol.COMMA, "Formals separator", "Identifier \",\" Identifier");
				}
			}
			
			// Expect right parenthesis
			next(WaebricSymbol.RPARANTHESIS, "Formals closing parenthesis", "\")\"");
			return formals;
		} else {
			return new Formals.EmptyFormal();
		}
	}

}