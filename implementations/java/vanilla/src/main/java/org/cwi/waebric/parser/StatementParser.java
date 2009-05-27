package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.expressions.Var;
import org.cwi.waebric.parser.ast.statements.Formals;
import org.cwi.waebric.parser.ast.statements.Statement;
import org.cwi.waebric.parser.exception.ParserException;
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
	 * 
	 * @param statement
	 */
	public void parse(Statement statement) {
		
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