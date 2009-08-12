package org.cwi.waebric.lexer.token;

import static org.junit.Assert.*;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.lexer.token.Token;
import org.cwi.waebric.lexer.token.WaebricTokenSort;
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
	
}