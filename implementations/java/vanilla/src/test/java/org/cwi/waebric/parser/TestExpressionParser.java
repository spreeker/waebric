package org.cwi.waebric.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.TestUtilities;
import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.expression.KeyValuePair;
import org.cwi.waebric.parser.ast.expression.Expression.Field;
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
	public void testParseExpression() throws SyntaxException, IOException {
		iterator = TestUtilities.quickScan("variable1");
		parser = new ExpressionParser(iterator, exceptions);
		
		Expression expression = parser.parseExpression();
		assertEquals(Expression.VarExpression.class, expression.getClass());
	}
	
	@Test
	public void testCat() throws SyntaxException, IOException {
		iterator = TestUtilities.quickScan("\"http://www.cwi.nl/?site=\" + var + \",user=\" + my.field");
		parser = new ExpressionParser(iterator, exceptions);
		
		// Class type is checked in cast
		Expression.CatExpression expression = (Expression.CatExpression) parser.parseExpression();
		assertEquals(Expression.TextExpression.class, expression.getLeft().getClass());
		assertEquals(Expression.CatExpression.class, expression.getRight().getClass());
	}
	
	@Test
	public void testVarExpression() throws SyntaxException, IOException {
		iterator = TestUtilities.quickScan("variable1");
		parser = new ExpressionParser(iterator, exceptions);
		
		Expression.VarExpression expression = parser.parseVarExpression();
		assertEquals("variable1", expression.getId().getToken().getLexeme().toString());
	}
	
	@Test
	public void testFieldExpression() throws SyntaxException, IOException {
		iterator = TestUtilities.quickScan("exp1.identifier2.identifier");
		parser = new ExpressionParser(iterator, exceptions);
		
		Expression.Field expression = (Field) parser.parseExpression();
		assertEquals("identifier", expression.getIdentifier().getToken().getLexeme().toString());
		assertEquals(Expression.Field.class, expression.getExpression().getClass());
		
		Expression.Field sub = (Field) expression.getExpression();
		assertEquals("identifier2", sub.getIdentifier().getToken().getLexeme().toString());
		assertEquals(Expression.VarExpression.class, sub.getExpression().getClass());
	}
	
	@Test
	public void testSymbolExpression() throws SyntaxException, IOException {
		iterator = TestUtilities.quickScan("'abc");
		parser = new ExpressionParser(iterator, exceptions);
		
		Expression.SymbolExpression expression = parser.parseSymbolExpression();
		assertEquals("abc", expression.getSymbol().getName().toString());
	}
	
	@Test
	public void testNatExpression() throws SyntaxException, IOException {
		iterator = TestUtilities.quickScan("123");
		parser = new ExpressionParser(iterator, exceptions);
		
		Expression.NatExpression expression = parser.parseNatExpression();
		assertEquals(123, expression.getNatural().getValue());
	}
	
	@Test
	public void testTextExpression() throws SyntaxException, IOException {
		iterator = TestUtilities.quickScan("\"bla\"");
		parser = new ExpressionParser(iterator, exceptions);
		
		Expression.TextExpression expression = parser.parseTextExpression();
		assertEquals("bla", expression.getText().getLiteral().toString());
	}
	
	@Test
	public void testExpressionCollection() throws SyntaxException, IOException {
		iterator = TestUtilities.quickScan("[variable1,variable2]");
		parser = new ExpressionParser(iterator, exceptions);
		
		Expression.ListExpression expression = parser.parseListExpression();
		assertTrue(expression.getExpressions().size() == 2);
		assertTrue(expression.getExpressions().get(0) instanceof Expression.VarExpression);
		assertTrue(expression.getExpressions().get(1) instanceof Expression.VarExpression);
	}
	
	@Test
	public void testVarKeyValuePairCollection() throws SyntaxException, IOException {
		iterator = TestUtilities.quickScan("{identifier1: variable1, identifier2: variable2}");
		parser = new ExpressionParser(iterator, exceptions);
		
		Expression.RecordExpression expression = parser.parseRecordExpression();
		assertTrue(expression.getPairs().size() == 2);
		assertTrue(expression.getPairs().get(0) instanceof KeyValuePair);
		assertTrue(expression.getPairs().get(1) instanceof KeyValuePair);
	}
	
}