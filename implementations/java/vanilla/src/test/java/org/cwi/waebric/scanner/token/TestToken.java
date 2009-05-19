package org.cwi.waebric.scanner.token;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestToken {

	@Test
	public void testConstructorAndAccessors() {
		Token token = new Token(WaebricLiteral.MODULE, TokenSort.KEYWORD, 123);
		assertNotNull(token);
		assertTrue(token.getLexeme() == WaebricLiteral.MODULE);
		assertTrue(token.getSort() == TokenSort.KEYWORD);
		assertTrue(token.getLine() == 123);
	}
	
	@Test
	public void testEquals() {
		Token module = new Token(WaebricLiteral.MODULE, TokenSort.KEYWORD, 123);
		Token similar = new Token(WaebricLiteral.MODULE, TokenSort.KEYWORD, 123);
		Token difflit = new Token(WaebricLiteral.END, TokenSort.KEYWORD, 123);
		Token diffsort = new Token(WaebricLiteral.MODULE, TokenSort.IDCON, 123);
		Token diffline = new Token(WaebricLiteral.MODULE, TokenSort.KEYWORD, 124);
		
		assertTrue(module.equals(module));
		assertTrue(module.equals(similar));
		assertFalse(module.equals(difflit));
		assertFalse(module.equals(diffsort));
		assertFalse(module.equals(diffline));
		assertFalse(module.equals(null));
	}
	
}