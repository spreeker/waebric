package org.cwi.waebric.lexer.token;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.lexer.token.Token;
import org.cwi.waebric.lexer.token.TokenIterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestTokenIterator {

	private TokenIterator iterator;
	
	@Before
	public void setUp() throws IOException {
		ArrayList<Token> tokens = new ArrayList<Token>();
		tokens.add(new Token.NaturalToken(123, 1, 2)); // Number
		tokens.add(new Token.IdentifierToken("identifier1", 3, 4)); // Identifier
		tokens.add(new Token.KeywordToken(WaebricKeyword.DEF, 5, 6)); // Keyword
		tokens.add(new Token.TextToken("text", 7, 8)); // Quote
		tokens.add(new Token.CharacterToken('@', 9, 10)); // Character
		tokens.add(new Token.SymbolToken("symbol1", 11, 12)); // Symbol
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
	
	@Test
	public void testAdd() {
		Token newbie = new Token.TextToken("test", 1, 2);
		iterator.add(newbie);
		assertEquals(newbie, iterator.next());
	}
	
	@Test
	public void testAddAll() {
		Token newbie = new Token.TextToken("test", 1, 2);
		ArrayList<Token> list = new ArrayList<Token>();
		list.add(newbie);
		iterator.addAll(list);
		assertEquals(newbie, iterator.next());
	}
	
}
