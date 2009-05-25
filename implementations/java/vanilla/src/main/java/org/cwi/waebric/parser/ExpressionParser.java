package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.parser.ast.expressions.Expression;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.token.TokenIterator;

/**
 * module languages/waebric/syntax/Expressions
 * @author schagen
 *
 */
public class ExpressionParser extends AbstractParser {

	public ExpressionParser(TokenIterator tokens, List<ParserException> exceptions) {
		super(tokens, exceptions);
	}
	
	public void visit(Expression expression) {
		
	}

}
