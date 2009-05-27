package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.functions.FunctionDef;
import org.cwi.waebric.parser.ast.statements.Formals;
import org.cwi.waebric.parser.ast.statements.Statement;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.cwi.waebric.scanner.token.TokenSort;

/**
 * module languages/waebric/syntax/Functions
 * @author schagen
 *
 */
class FunctionParser extends AbstractParser {

	private final StatementParser statementParser;
	
	public FunctionParser(TokenIterator tokens, List<ParserException> exceptions) {
		super(tokens, exceptions);
		
		// Construct sub parser
		statementParser = new StatementParser(tokens, exceptions);
	}

	/**
	 * 
	 * @param def
	 */
	public void parse(FunctionDef def) {
		if(next("function identifier", "identifier", TokenSort.IDENTIFIER)) {
			// Parse function definition identifier
			IdCon identifier = new IdCon(current.getLexeme().toString());
			def.setIdentifier(identifier);
		}
		
		// TODO: Optional formals
		// TODO: 0..* Statements
		
		// Parse function definition end
		next("function end", "end", WaebricKeyword.END);
	}
	
	/**
	 * 
	 * @param formals
	 */
	public void parse(Formals formals) {
		// TODO: LOLOL
	}
	
	/**
	 * 
	 * @param statement
	 */
	public void parse(Statement statement) {
		statementParser.parse(statement);
	}

}
