package org.cwi.waebric.parser;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.expressions.Expression;
import org.cwi.waebric.parser.ast.markup.Designator;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.TestScanner;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestExpressionParser {

	private ExpressionParser parser;
	
	private List<ParserException> exceptions;
	private TokenIterator iterator;
	
	@Before
	public void setUp() {
		exceptions = new ArrayList<ParserException>();
	}
	
	@After
	public void tearDown() {
		exceptions.clear();
		exceptions = null;
		parser = null;
		iterator = null;
	}
	
	@Test
	public void testVarExpression() {
		iterator = TestScanner.quickScan("designator1 @99,#myattribute,@99%12");
		parser = new ExpressionParser(iterator, exceptions);
		
		Expression expression = new Expression.VarExpression();
		parser.visit(expression);
		
		// Assertions
		assertTrue(exceptions.size() == 0);
	}
	
}