package org.cwi.waebric.scanner.token;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;

import org.cwi.waebric.WaebricKeyword;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestTokenIterator {

	private TokenIterator iterator;
	
	@Before
	public void setUp() throws IOException {
		ArrayList<Token> tokens = new ArrayList<Token>();
		tokens.add(new Token(123, TokenSort.NATCON, 1, 2)); // Number
		tokens.add(new Token("identifier1", TokenSort.IDCON, 3, 4)); // Identifier
		tokens.add(new Token(WaebricKeyword.DEF, TokenSort.KEYWORD, 5, 6)); // Keyword
		tokens.add(new Token("text", TokenSort.STRCON, 7, 8)); // String
		tokens.add(new Token('@', TokenSort.SYMBOLCHAR, 9, 10)); // Character
		tokens.add(new Token("symbol1", TokenSort.SYMBOLCON, 11, 12)); // Symbol
		iterator = new TokenIterator(tokens);
	}
	
	@After
	public void tearDown() {
		iterator = null;
	}
	
	@Test
	public void testPeek() {
		iterator.next(); // Go to first token
		assertEquals("identifier1", iterator.peek(1).getLexeme());
		assertEquals(WaebricKeyword.DEF, iterator.peek(2).getLexeme());
		assertEquals("text", iterator.peek(3).getLexeme());
	}
	
	@Test
	public void testNextAndHasNext() {
		Token current = iterator.next();
		
		assertTrue(iterator.hasNext(0));
		assertTrue(iterator.hasNext(1));
		assertTrue(iterator.hasNext(5));
		assertFalse(iterator.hasNext(6));
		assertFalse(iterator.hasNext(-1));

		while(iterator.hasNext()) {
			assertNotNull(current);
			current = iterator.next();
		}
		
		assertTrue(current.getLexeme().equals("symbol1"));
	}
	
	@Test
	public void testRemove() {
		iterator.next(); // Start token
		iterator.next(); // Token to be removed
		Token peek = iterator.peek(1);
		iterator.remove();
		Token current = iterator.next();
		
		assertTrue(peek.equals(current));
	}
	
}
