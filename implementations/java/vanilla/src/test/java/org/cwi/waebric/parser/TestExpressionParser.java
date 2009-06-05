package org.cwi.waebric.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.expression.KeyValuePair;
import org.cwi.waebric.parser.exception.SyntaxException;
import org.cwi.waebric.scanner.TestScanner;
import org.cwi.waebric.scanner.token.WaebricTokenIterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestExpressionParser {

	private ExpressionParser parser;
	
	private List<SyntaxException> exceptions;
	private WaebricTokenIterator iterator;
	
	@Before
	public void setUp() {
		exceptions = new ArrayList<SyntaxException>();
	}
	
	@After
	public void tearDown() {
		exceptions.clear();
		exceptions = null;
		parser = null;
		iterator = null;
	}
	
	@Test
	public void testParseExpression() {
		iterator = TestScanner.quickScan("variable1");
		parser = new ExpressionParser(iterator, exceptions);
		
		Expression expression = parser.parseExpression("expression", "test");
		
		// Assertions
		assertEquals(0, exceptions.size());
		assertEquals(Expression.VarExpression.class, expression.getClass());
	}
	
	@Test
	public void testVarExpression() {
		iterator = TestScanner.quickScan("variable1");
		parser = new ExpressionParser(iterator, exceptions);
		
		Expression.VarExpression expression = parser.parseVarExpression();
		assertEquals(0, exceptions.size());
		assertEquals("variable1", expression.getVar().getIdentifier().getLiteral().toString());
	}
	
	@Test
	public void testIdConExpression() {
		iterator = TestScanner.quickScan("variable1.identifier1");
		parser = new ExpressionParser(iterator, exceptions);
		
		Expression.IdConExpression expression = parser.parseIdConExpression();
		assertTrue(exceptions.size() == 0);
		assertEquals("identifier1", expression.getIdentifier().getLiteral().toString());
		assertEquals(Expression.VarExpression.class, expression.getExpression().getClass());
	}
	
	@Test
	public void testSymbolExpression() {
		iterator = TestScanner.quickScan("'abc");
		parser = new ExpressionParser(iterator, exceptions);
		
		Expression.SymbolExpression expression = parser.parseSymbolExpression();
		assertTrue(exceptions.size() == 0);
		assertEquals("abc", expression.getSymbol().getLiteral().toString());
	}
	
	@Test
	public void testNatExpression() {
		iterator = TestScanner.quickScan("123");
		parser = new ExpressionParser(iterator, exceptions);
		
		Expression.NatExpression expression = parser.parseNatExpression();
		assertTrue(exceptions.size() == 0);
		assertEquals(123, expression.getNatural().getLiteral().toInteger());
	}
	
	@Test
	public void testTextExpression() {
		iterator = TestScanner.quickScan("\"bla\"");
		parser = new ExpressionParser(iterator, exceptions);
		
		Expression.TextExpression expression = parser.parseTextExpression();
		assertTrue(exceptions.size() == 0);
		assertEquals("bla", expression.getText().getLiteral().toString());
	}
	
	@Test
	public void testExpressionCollection() {
		iterator = TestScanner.quickScan("[variable1,variable2]");
		parser = new ExpressionParser(iterator, exceptions);
		
		Expression.ListExpression expression = parser.parseListExpression();
		assertTrue(exceptions.size() == 0);
		assertTrue(expression.getElements().length == 2);
		assertTrue(expression.getElements()[0] instanceof Expression.VarExpression);
		assertTrue(expression.getElements()[1] instanceof Expression.VarExpression);
	}
	
	@Test
	public void testVarKeyValuePairCollection() {
		iterator = TestScanner.quickScan("{identifier1: variable1, identifier2: variable2}");
		parser = new ExpressionParser(iterator, exceptions);
		
		Expression.RecordExpression expression = parser.parseRecordExpression();
		assertTrue(exceptions.size() == 0);
		assertTrue(expression.getElements().length == 2);
		assertTrue(expression.getElements()[0] instanceof KeyValuePair);
		assertTrue(expression.getElements()[1] instanceof KeyValuePair);
	}
	
}