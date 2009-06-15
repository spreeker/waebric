package org.cwi.waebric.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.TestUtilities;
import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.expression.KeyValuePair;
import org.cwi.waebric.parser.ast.expression.Expression.Field;
import org.cwi.waebric.parser.exception.SyntaxException;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestExpressionParser {

	private ExpressionParser parser;
	
	private List<SyntaxException> exceptions;
	private TokenIterator iterator;
	
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
	public void testParseExpression() throws SyntaxException {
		iterator = TestUtilities.quickScan("variable1");
		parser = new ExpressionParser(iterator, exceptions);
		
		Expression expression = parser.parseExpression();
		assertEquals(Expression.VarExpression.class, expression.getClass());
	}
	
	@Test
	public void testCat() throws SyntaxException {
		iterator = TestUtilities.quickScan("var+my.field");
		parser = new ExpressionParser(iterator, exceptions);
		
		// Class type is checked in cast
		Expression.CatExpression expression = (Expression.CatExpression) parser.parseExpression();
		assertEquals(Expression.VarExpression.class, expression.getLeft().getClass());
		assertEquals(Expression.Field.class, expression.getRight().getClass());
	}
	
	@Test
	public void testVarExpression() throws SyntaxException {
		iterator = TestUtilities.quickScan("variable1");
		parser = new ExpressionParser(iterator, exceptions);
		
		Expression.VarExpression expression = parser.parseVarExpression();
		assertEquals("variable1", expression.getVar().getToken().getLexeme().toString());
	}
	
	@Test
	public void testIdConExpression() throws SyntaxException {
		iterator = TestUtilities.quickScan("variable1.identifier1");
		parser = new ExpressionParser(iterator, exceptions);
		
		Expression.Field expression = (Field) parser.parseExpression();
		assertEquals("identifier1", expression.getIdentifier().getToken().getLexeme().toString());
		assertEquals(Expression.VarExpression.class, expression.getExpression().getClass());
	}
	
	@Test
	public void testSymbolExpression() throws SyntaxException {
		iterator = TestUtilities.quickScan("'abc");
		parser = new ExpressionParser(iterator, exceptions);
		
		Expression.SymbolExpression expression = parser.parseSymbolExpression();
		assertEquals("abc", expression.getSymbol().getLiteral().toString());
	}
	
	@Test
	public void testNatExpression() throws SyntaxException {
		iterator = TestUtilities.quickScan("123");
		parser = new ExpressionParser(iterator, exceptions);
		
		Expression.NatExpression expression = parser.parseNatExpression();
		assertEquals(123, expression.getNatural().getLiteral().toInteger());
	}
	
	@Test
	public void testTextExpression() throws SyntaxException {
		iterator = TestUtilities.quickScan("\"bla\"");
		parser = new ExpressionParser(iterator, exceptions);
		
		Expression.TextExpression expression = parser.parseTextExpression();
		assertEquals("bla", expression.getText().getLiteral().toString());
	}
	
	@Test
	public void testExpressionCollection() throws SyntaxException {
		iterator = TestUtilities.quickScan("[variable1,variable2]");
		parser = new ExpressionParser(iterator, exceptions);
		
		Expression.ListExpression expression = parser.parseListExpression();
		assertTrue(expression.getExpressions().size() == 2);
		assertTrue(expression.getExpressions().get(0) instanceof Expression.VarExpression);
		assertTrue(expression.getExpressions().get(1) instanceof Expression.VarExpression);
	}
	
	@Test
	public void testVarKeyValuePairCollection() throws SyntaxException {
		iterator = TestUtilities.quickScan("{identifier1: variable1, identifier2: variable2}");
		parser = new ExpressionParser(iterator, exceptions);
		
		Expression.RecordExpression expression = parser.parseRecordExpression();
		assertTrue(expression.getPairs().size() == 2);
		assertTrue(expression.getPairs().get(0) instanceof KeyValuePair);
		assertTrue(expression.getPairs().get(1) instanceof KeyValuePair);
	}
	
}