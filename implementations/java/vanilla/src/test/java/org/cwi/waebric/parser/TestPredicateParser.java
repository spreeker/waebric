package org.cwi.waebric.parser;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.expressions.Expression;
import org.cwi.waebric.parser.ast.statement.predicate.Predicate;
import org.cwi.waebric.parser.ast.statement.predicate.Type;
import org.cwi.waebric.parser.ast.statement.predicate.Predicate.AndPredicate;
import org.cwi.waebric.parser.ast.statement.predicate.Predicate.ExpressionPredicate;
import org.cwi.waebric.parser.ast.statement.predicate.Predicate.ExpressionTypePredicate;
import org.cwi.waebric.parser.ast.statement.predicate.Predicate.NotPredicate;
import org.cwi.waebric.parser.ast.statement.predicate.Predicate.OrPredicate;
import org.cwi.waebric.parser.exception.MissingTokenException;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.parser.exception.UnexpectedTokenException;
import org.cwi.waebric.scanner.TestScanner;
import org.cwi.waebric.scanner.token.WaebricTokenIterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestPredicateParser {
	
	private PredicateParser parser;
	
	private List<ParserException> exceptions;
	private WaebricTokenIterator iterator;
	
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
	public void testPredicateWithoutType() {
		iterator = TestScanner.quickScan("123");
		parser = new PredicateParser(iterator, exceptions);
		
		Predicate.ExpressionPredicate predicate = (ExpressionPredicate) parser.parsePredicate();
		assertEquals(0, exceptions.size()); // Error free
		assertEquals(Predicate.ExpressionPredicate.class, predicate.getClass()); // Correct type
		assertEquals(Expression.NatExpression.class, predicate.getExpression().getClass());
	}
	
	@Test
	public void testPredicateWithType() {
		iterator = TestScanner.quickScan("123.string?");
		parser = new PredicateParser(iterator, exceptions);
		
		Predicate.ExpressionTypePredicate predicate = (ExpressionTypePredicate) parser.parsePredicate();
		assertEquals(0, exceptions.size()); // Error free
		assertEquals(Predicate.ExpressionTypePredicate.class, predicate.getClass()); // Correct type
		assertEquals("string", predicate.getType().toString()); // Correct type
		assertEquals(Expression.NatExpression.class, predicate.getExpression().getClass());
	}
	
	@Test
	public void testNotPredicate() {
		iterator = TestScanner.quickScan("!123");
		parser = new PredicateParser(iterator, exceptions);
		
		Predicate.NotPredicate predicate = (NotPredicate) parser.parsePredicate();
		assertEquals(0, exceptions.size()); // Error free
		assertEquals(Predicate.ExpressionPredicate.class, predicate.getPredicate().getClass());
	}
	
	@Test
	public void testAndPredicate() {
		iterator = TestScanner.quickScan("123&&123.string?");
		parser = new PredicateParser(iterator, exceptions);
		
		Predicate.AndPredicate predicate = (AndPredicate) parser.parsePredicate();
		assertEquals(0, exceptions.size()); // Error free
		assertEquals(Predicate.ExpressionPredicate.class, predicate.getLeft().getClass());
		assertEquals(Predicate.ExpressionTypePredicate.class, predicate.getRight().getClass());
	}
	
	
	@Test
	public void testOrPredicate() {
		iterator = TestScanner.quickScan("123||123.string?");
		parser = new PredicateParser(iterator, exceptions);
		
		Predicate.OrPredicate predicate = (OrPredicate) parser.parsePredicate();
		assertEquals(0, exceptions.size()); // Error free
		assertEquals(Predicate.ExpressionPredicate.class, predicate.getLeft().getClass());
		assertEquals(Predicate.ExpressionTypePredicate.class, predicate.getRight().getClass());
	}
	
	@Test
	public void testCorrectType() {
		// Correct type
		iterator = TestScanner.quickScan("string");
		parser = new PredicateParser(iterator, exceptions);
		
		Type type = parser.parseType();
		assertEquals(0, exceptions.size()); // Error free
		assertEquals("string", type.getType().toString()); // Correct literal stored
	}
	
	@Test
	public void testUnknownType() {
		// Incorrect type: non-existing
		iterator = TestScanner.quickScan("unknown");
		parser = new PredicateParser(iterator, exceptions);
		
		parser.parseType();
		assertEquals(1, exceptions.size()); // Exception thrown
		assertEquals(UnexpectedTokenException.class, exceptions.get(0).getClass()); // Correct exception throw
	}
	
	@Test
	public void testEmptyType() {
		// Incorrect type: empty
		iterator = TestScanner.quickScan("");
		parser = new PredicateParser(iterator, exceptions);
		
		parser.parseType();
		assertEquals(1, exceptions.size()); // Exception thrown
		assertEquals(MissingTokenException.class, exceptions.get(0).getClass()); // Correct exception thrown
	}
	
}