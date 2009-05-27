package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.expressions.Var;
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
	
	public StatementParser(TokenIterator tokens, List<ParserException> exceptions) {
		super(tokens, exceptions);
		
		// Construct sub parser
		expressionParser = new ExpressionParser(tokens, exceptions);
	}
	
	/**
	 * Recognise and construct statement sort based on look-ahead information.
	 * 
	 * @param previous Previous token
	 * @param expected Expected syntax
	 * @return Statement
	 * 
	 * TODO
	 */
	public Statement parseStatement() {
		if(! tokens.hasNext()) { return null; }
		
		Statement statement = null;
		Token peek = tokens.peek(1); // Determine statement type based on look-ahead
		if(peek.getLexeme().equals(WaebricKeyword.IF)) {
			statement = new Statement.IfStatement();
			parse((Statement.IfStatement) statement);
		} else if(peek.getLexeme().equals(WaebricKeyword.EACH)) {
			statement = new Statement.EachStatement();
			parse((Statement.EachStatement) statement);
		} else if(peek.getLexeme().equals(WaebricKeyword.LET)) {
			statement = new Statement.LetStatement();
			parse((Statement.LetStatement) statement);
		} else if(peek.getLexeme().equals(WaebricSymbol.LCBRACKET)) {
			statement = new Statement.StatementCollection();
			parse((Statement.StatementCollection) statement);
		} else if(peek.getLexeme().equals(WaebricKeyword.COMMENT)) {
			statement = new Statement.CommentStatement();
			parse((Statement.CommentStatement) statement);
		} else if(peek.getLexeme().equals(WaebricKeyword.ECHO)) {
			Token echoPeek = tokens.peek(2);
			if(echoPeek.getSort().equals(TokenSort.TEXT)) {
				// Embedding start with text
				statement = new Statement.EchoEmbeddingStatement();
				parse((Statement.EchoEmbeddingStatement) statement);
			} else {
				// Only remaining echo alternative uses expressions
				statement = new Statement.EchoExpressionStatement();
				parse((Statement.EchoExpressionStatement) statement);
			}
		} else if(peek.getLexeme().equals(WaebricKeyword.CDATA)) {
			statement = new Statement.CDataStatement();
			parse((Statement.CDataStatement) statement);
		} else if(peek.getLexeme().equals(WaebricKeyword.YIELD)) {
			statement = new Statement.YieldStatement();
			parse((Statement.YieldStatement) statement);
		} 
		
		return statement;
	}
	
	public void parse(Statement.IfStatement statement) {
		next("if keyword", "\"if\" \"(\"", WaebricKeyword.IF);
		next("predicate opening", "\"if\" \"(\" predicate", WaebricSymbol.LPARANTHESIS);
		
		// TODO: Parse predicate
		
		next("predicate closure", "\"(\" predicate \")\"", WaebricSymbol.RPARANTHESIS);
		
		// Parse sub statement
		Statement subStatement = parseStatement();
		statement.setStatement(subStatement);
	}
	
	public void parse(Statement.EachStatement statement) {
		
	}
	
	public void parse(Statement.LetStatement statement) {
		
	}
	
	public void parse(Statement.StatementCollection statement) {
		
	}
	
	public void parse(Statement.CommentStatement statement) {
		
	}
	
	public void parse(Statement.EchoEmbeddingStatement statement) {
		
	}
	
	public void parse(Statement.EchoExpressionStatement statement) {
		
	}
	
	public void parse(Statement.CDataStatement statement) {
		
	}
	
	public void parse(Statement.YieldStatement statement) {
		
	}
	
	/**
	 * 
	 * @param formals
	 */
	public void parse(Formals formals) {
		// Expect left parenthesis
		next("formals opening parenthesis", "left parenthesis", WaebricSymbol.LPARANTHESIS);
		
		while(tokens.hasNext()) {
			if(tokens.peek(1).getLexeme().equals(WaebricSymbol.RPARANTHESIS)) {
				break; // End of formals found, break while
			}
			
			// Parse variable
			Var var = new Var();
			parse(var);
			formals.addVar(var);
			
			// While not end of formals, comma separator is expected
			if(tokens.hasNext() && ! tokens.peek(1).getLexeme().equals(WaebricSymbol.RPARANTHESIS)) {
				next("arguments separator", "argument \",\" argument", WaebricSymbol.COMMA);
			}
		}
		
		// Expect right parenthesis
		next("formals opening parenthesis", "left parenthesis", WaebricSymbol.RPARANTHESIS);
	}

	/**
	 * 
	 * @param var
	 */
	public void parse(Var var) {
		expressionParser.parse(var);
	}

}