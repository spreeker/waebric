package org.cwi.waebric.parser;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.expressions.Expression;
import org.cwi.waebric.parser.ast.expressions.KeyValuePair;
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
		iterator = TestScanner.quickScan("variable1");
		parser = new ExpressionParser(iterator, exceptions);
		
		Expression.VarExpression expression = new Expression.VarExpression();
		parser.visit(expression);
		
		// Assertions
		assertTrue(exceptions.size() == 0);
		assertTrue(expression.getVar().equals("variable1"));
	}
	
	@Test
	public void testExpressionWithIdCon() {
		iterator = TestScanner.quickScan("variable1.identifier1");
		parser = new ExpressionParser(iterator, exceptions);
		
		Expression.ExpressionWithIdCon expression = new Expression.ExpressionWithIdCon();
		parser.visit(expression);
		
		// Assertions
		assertTrue(exceptions.size() == 0);
		assertTrue(expression.getIdentifier().equals("identifier1"));
		assertTrue(expression.getExpression() instanceof Expression.VarExpression);
	}
	
	@Test
	public void testExpressionCollection() {
		iterator = TestScanner.quickScan("[variable1,variable2]");
		parser = new ExpressionParser(iterator, exceptions);
		
		Expression.ExpressionCollection expression = new Expression.ExpressionCollection();
		parser.visit(expression);
		
		// Assertions
		assertTrue(exceptions.size() == 0);
		assertTrue(expression.getElements().length == 2);
		assertTrue(expression.getElements()[0] instanceof Expression.VarExpression);
		assertTrue(expression.getElements()[1] instanceof Expression.VarExpression);
	}
	
	@Test
	public void testVarKeyValuePairCollection() {
		iterator = TestScanner.quickScan("{identifier1: variable1, identifier2: variable2}");
		parser = new ExpressionParser(iterator, exceptions);
		
		Expression.KeyValuePairCollection expression = new Expression.KeyValuePairCollection();
		parser.visit(expression);
		
		// Assertions
		assertTrue(exceptions.size() == 0);
		assertTrue(expression.getElements().length == 2);
		assertTrue(expression.getElements()[0] instanceof KeyValuePair);
		assertTrue(expression.getElements()[1] instanceof KeyValuePair);
	}
	
}