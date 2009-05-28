package org.cwi.waebric.parser;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

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
		
	}
	
	@Test
	public void testPredicateWithType() {
		
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