package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.parser.ast.functions.FunctionDef;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.token.TokenIterator;

public class FunctionParser extends AbstractParser {

	public FunctionParser(TokenIterator tokens, List<ParserException> exceptions) {
		super(tokens, exceptions);
	}

	public void visit(FunctionDef def) {
		// TODO Auto-generated method stub
		
	}

}
