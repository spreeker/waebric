package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.expressions.Expression;
import org.cwi.waebric.parser.ast.expressions.Var;
import org.cwi.waebric.parser.ast.markup.Argument;
import org.cwi.waebric.parser.ast.markup.Arguments;
import org.cwi.waebric.parser.ast.markup.Attribute;
import org.cwi.waebric.parser.ast.markup.Attributes;
import org.cwi.waebric.parser.ast.markup.Designator;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.ast.markup.Markup.MarkupWithArguments;
import org.cwi.waebric.parser.ast.statements.Statement;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.token.TokenIterator;

public class MarkupParser extends AbstractParser {

	private final ExpressionParser expressionParser;
	private final StatementParser statementParser;
	
	public MarkupParser(TokenIterator tokens, List<ParserException> exceptions) {
		super(tokens, exceptions);
		
		// Initialise sub parsers
		expressionParser = new ExpressionParser(tokens, exceptions);
		statementParser = new StatementParser(tokens, exceptions);
	}
	
	public void visit(Markup markup) {
		if(markup == null) { // Initialise mark up when null
			if(tokens.hasNext(2) && tokens.peek(2).getLexeme().
					equals("" + WaebricSymbol.LPARANTHESIS)) {
				markup = new Markup.MarkupWithArguments();
			} else {
				markup = new Markup.MarkupWithoutArguments();
			}
		}
		
		Designator designator = new Designator();
		visit(designator);
		markup.setDesignator(designator);
		
		if(markup instanceof MarkupWithArguments) {
			visit(markup.getArguments());
		}
	}
	
	public void visit(Designator designator) {
		
	}
	
	public void visit(Attributes attributes) {
		
	}
	
	public static boolean isAttribute(String lexeme) {
		return lexeme.matches("(#\\w+)|(\\.\\w+)|(\\$\\w+)|(:\\w+)|(@\\d+%\\d+)|(@\\d+)");
	}
	
	public void visit(Attribute attribute) {
		
	}
	
	public void visit(Arguments arguments) {
		
	}
	
	public void visit(Argument argument) {
		
	}
	
	public void visit(Var var) {
		
	}
	
	/**
	 * @see org.cwi.waebric.parser.ExpressionParser
	 * @param expression
	 */
	public void visit(Expression expression) {
		expressionParser.visit(expression);
	}
	
	/**
	 * @see org.cwi.waebric.parser.StatementParser
	 * @param statement
	 */
	public void visit(Statement statement) {
		statementParser.visit(statement);
	}

}