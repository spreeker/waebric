package org.cwi.waebric.scanner.token;

import static org.junit.Assert.*;

import org.cwi.waebric.WaebricKeyword;
import org.junit.Test;

public class TestToken {

	@Test
	public void testConstructorAndAccessors() {
		WaebricToken token = new WaebricToken(WaebricKeyword.MODULE, WaebricTokenSort.KEYWORD, 123, 321);
		assertNotNull(token);
		assertTrue(token.getLexeme() == WaebricKeyword.MODULE);
		assertTrue(token.getSort() == WaebricTokenSort.KEYWORD);
		assertTrue(token.getLine() == 123);
	}
	
	@Test
	public void testEquals() {
		WaebricToken module = new WaebricToken(WaebricKeyword.MODULE, WaebricTokenSort.KEYWORD, 123, 321);
		WaebricToken similar = new WaebricToken(WaebricKeyword.MODULE, WaebricTokenSort.KEYWORD, 123, 321);
		WaebricToken difflit = new WaebricToken(WaebricKeyword.END, WaebricTokenSort.KEYWORD, 123, 321);
		WaebricToken diffsort = new WaebricToken(WaebricKeyword.MODULE, WaebricTokenSort.IDCON, 123, 321);
		WaebricToken diffline = new WaebricToken(WaebricKeyword.MODULE, WaebricTokenSort.KEYWORD, 124, 321);
		WaebricToken diffchar = new WaebricToken(WaebricKeyword.MODULE, WaebricTokenSort.IDCON, 123, 1337);
		
		assertTrue(module.equals(module));
		assertTrue(module.equals(similar));
		assertFalse(module.equals(difflit));
		assertFalse(module.equals(diffsort));
		assertFalse(module.equals(diffline));
		assertFalse(module.equals(diffchar));
		assertFalse(module.equals(null));
	}
	
}