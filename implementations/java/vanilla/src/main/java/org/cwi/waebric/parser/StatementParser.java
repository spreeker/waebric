package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.parser.ast.statements.Statement;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.token.TokenIterator;

/**
 * Statement parser
 * 
 * module languages/waebric/syntax/Statements
 * 
 * @author Jeroen van Schagen
 * @date 27-05-2009
 */
class StatementParser extends AbstractParser {

	public StatementParser(TokenIterator tokens, List<ParserException> exceptions) {
		super(tokens, exceptions);
	}
	
	public void parse(Statement statement) {
		
	}

}