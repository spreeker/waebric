package org.cwi.waebric.scanner.token;

import static org.junit.Assert.*;

import org.cwi.waebric.WaebricKeyword;
import org.junit.Test;

public class TestToken {

	@Test
	public void testConstructorAndAccessors() {
		Token token = new Token.KeywordToken(WaebricKeyword.MODULE, 123, 321);
		assertNotNull(token);
		assertTrue(token.getLexeme() == WaebricKeyword.MODULE);
		assertTrue(token.getSort() == WaebricTokenSort.KEYWORD);
		assertTrue(token.getLine() == 123);
	}
	
	@Test
	public void testEquals() {
		Token module = new Token.KeywordToken(WaebricKeyword.MODULE, 123, 321);
		Token similar = new Token.KeywordToken(WaebricKeyword.MODULE, 123, 321);
		Token difflit = new Token.KeywordToken(WaebricKeyword.END, 123, 321);
		Token diffsort = new Token.KeywordToken(WaebricKeyword.MODULE, 123, 321);
		Token diffline = new Token.KeywordToken(WaebricKeyword.MODULE, 124, 321);
		Token diffchar = new Token.KeywordToken(WaebricKeyword.MODULE, 123, 1337);
		
		assertTrue(module.equals(module));
		assertTrue(module.equals(similar));
		assertFalse(module.equals(difflit));
		assertFalse(module.equals(diffsort));
		assertFalse(module.equals(diffline));
		assertFalse(module.equals(diffchar));
		assertFalse(module.equals(null));
	}
	
}