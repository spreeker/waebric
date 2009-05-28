package org.cwi.waebric.parser;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.expressions.Expression;
import org.cwi.waebric.parser.ast.predicates.Predicate;
import org.cwi.waebric.parser.ast.predicates.Type;
import org.cwi.waebric.parser.exception.MissingTokenException;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.parser.exception.UnexpectedTokenException;
import org.cwi.waebric.scanner.TestScanner;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestPredicateParser {
	
	private PredicateParser parser;
	
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
	public void testPredicateWithoutType() {
		// Correct predicate
		iterator = TestScanner.quickScan("123");
		parser = new PredicateParser(iterator, exceptions);
		
		Predicate predicate = parser.parsePredicate("expression", "expression");
		assertEquals(0, exceptions.size()); // Error free
		assertEquals(Predicate.PredicateWithoutType.class, predicate.getClass()); // Correct type
		assertEquals(Expression.NatExpression.class, predicate.getExpression().getClass()); // Correct expression
	}
	
	@Test
	public void testPredicateWithType() {
		// Correct predicate
		iterator = TestScanner.quickScan("bla.string?");
		parser = new PredicateParser(iterator, exceptions);
		
		Predicate predicate = parser.parsePredicate("expression", "expression \".\" type \"?\"");
		assertEquals(0, exceptions.size()); // Error free
		assertEquals(Predicate.PredicateWithType.class, predicate.getClass()); // Correct type
		assertEquals(Expression.VarExpression.class, predicate.getExpression().getClass()); // Correct expression
		assertEquals("string", ((Predicate.PredicateWithType) predicate).getType().toString()); // Correct type
	}
	
	@Test
	public void testPredicateIncorrectExpression() {
		// TODO
	}
	
	@Test
	public void testPredicateIncorrectType() {
		// TODO
	}
	
	
	@Test
	public void testPredicateMissingEnd() {
		// TODO
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