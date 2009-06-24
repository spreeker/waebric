package org.cwi.waebric.parser;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.statement.predicate.Predicate;
import org.cwi.waebric.parser.ast.statement.predicate.Type;
import org.cwi.waebric.parser.ast.statement.predicate.Predicate.And;
import org.cwi.waebric.parser.ast.statement.predicate.Predicate.RegularPredicate;
import org.cwi.waebric.parser.ast.statement.predicate.Predicate.Is;
import org.cwi.waebric.parser.ast.statement.predicate.Predicate.Not;
import org.cwi.waebric.parser.ast.statement.predicate.Predicate.Or;
import org.cwi.waebric.TestUtilities;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestPredicateParser {
	
	private PredicateParser parser;
	
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
	public void testPredicateWithoutType() throws SyntaxException, IOException {
		iterator = TestUtilities.quickScan("123");
		parser = new PredicateParser(iterator, exceptions);
		
		Predicate.RegularPredicate predicate = (RegularPredicate) parser.parsePredicate();
		assertEquals(Predicate.RegularPredicate.class, predicate.getClass()); // Correct type
		assertEquals(Expression.NatExpression.class, predicate.getExpression().getClass());
	}
	
	@Test
	public void testPredicateWithType() throws SyntaxException, IOException {
		iterator = TestUtilities.quickScan("123.string?");
		parser = new PredicateParser(iterator, exceptions);
		
		Predicate.Is predicate = (Is) parser.parsePredicate();
		assertEquals(Predicate.Is.class, predicate.getClass()); // Correct type
		assertEquals(Type.StringType.class, predicate.getType().getClass()); // Correct type
		assertEquals(Expression.NatExpression.class, predicate.getExpression().getClass());
	}
	
	@Test
	public void testNotPredicate() throws SyntaxException, IOException {
		iterator = TestUtilities.quickScan("!123");
		parser = new PredicateParser(iterator, exceptions);
		
		Predicate.Not predicate = (Not) parser.parsePredicate();
		assertEquals(Predicate.RegularPredicate.class, predicate.getPredicate().getClass());
	}
	
	@Test
	public void testAndPredicate() throws SyntaxException, IOException {
		iterator = TestUtilities.quickScan("123&&123.string?");
		parser = new PredicateParser(iterator, exceptions);
		
		Predicate.And predicate = (And) parser.parsePredicate();
		assertEquals(Predicate.RegularPredicate.class, predicate.getLeft().getClass());
		assertEquals(Predicate.Is.class, predicate.getRight().getClass());
	}
	
	
	@Test
	public void testOrPredicate() throws SyntaxException, IOException {
		iterator = TestUtilities.quickScan("123||123.string?");
		parser = new PredicateParser(iterator, exceptions);
		
		Predicate.Or predicate = (Or) parser.parsePredicate();
		assertEquals(Predicate.RegularPredicate.class, predicate.getLeft().getClass());
		assertEquals(Predicate.Is.class, predicate.getRight().getClass());
	}
	
	@Test
	public void testCorrectType() throws SyntaxException, IOException {
		// Correct type
		iterator = TestUtilities.quickScan("string");
		parser = new PredicateParser(iterator, exceptions);
		
		Type type = parser.parseType();
		assertEquals(Type.StringType.class, type.getClass()); // Correct literal stored
	}
	
	@Test
	public void testUnknownType() throws IOException {
		// Incorrect type: non-existing
		iterator = TestUtilities.quickScan("unknown");
		parser = new PredicateParser(iterator, exceptions);
		
		try {
			parser.parseType();
		} catch(Exception e) {
			assertEquals(UnexpectedTokenException.class, e.getClass()); // Correct exception throw
		}
	}
	
	@Test
	public void testEmptyType() throws IOException {
		// Incorrect type: empty
		iterator = TestUtilities.quickScan("");
		parser = new PredicateParser(iterator, exceptions);
		
		try {
			parser.parseType();
		} catch(Exception e) {
			assertEquals(MissingTokenException.class, e.getClass()); // Correct exception throw
		}
	}
	
}