package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.module.FunctionDef;
import org.cwi.waebric.parser.exception.SyntaxException;
import org.cwi.waebric.scanner.token.WaebricTokenIterator;
import org.cwi.waebric.scanner.token.WaebricTokenSort;

/**
 * module languages/waebric/syntax/Functions
 * 
 * @author Jeroen van Schagen
 * @date 27-05-2009
 */
class FunctionParser extends AbstractParser {

	private final StatementParser statementParser;
	
	public FunctionParser(WaebricTokenIterator tokens, List<SyntaxException> exceptions) {
		super(tokens, exceptions);
		
		// Construct sub parser
		statementParser = new StatementParser(tokens, exceptions);
	}

	/**
	 * @see FunctionDef
	 * @return FunctionDef
	 * @throws SyntaxException 
	 */
	public FunctionDef parseFunctionDef() throws SyntaxException {
		FunctionDef def = new FunctionDef();
		
		// Parse identifier
		next(WaebricTokenSort.IDCON, "Function identifier", "Identifier"); 
		def.setIdentifier(new IdCon(tokens.current().getLexeme().toString()));
		
		// Parse formals
		def.setFormals(statementParser.parseFormals());
		
		// Parse statements
		while(tokens.hasNext() && ! tokens.peek(1).getLexeme().equals(WaebricKeyword.END)) {
			def.addStatement(statementParser.parseStatement());
		}
		
		next(WaebricKeyword.END, "Function closure \"end\"", "end");
		return def;
	}
	
}