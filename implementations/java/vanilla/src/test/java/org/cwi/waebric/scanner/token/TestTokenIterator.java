package org.cwi.waebric.scanner.token;

import static org.junit.Assert.*;

import java.io.FileReader;
import java.io.IOException;

import org.cwi.waebric.scanner.WaebricScanner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestTokenIterator {

	private final String PROGRAM_PATH = "src/test/waebric/simpleexample.wae";
	private TokenIterator iterator;
	
	@Before
	public void setUp() throws IOException {
		FileReader reader = new FileReader(PROGRAM_PATH);
		WaebricScanner scanner = new WaebricScanner(reader);
		scanner.tokenizeStream();
		iterator = scanner.iterator();	
	}
	
	@After
	public void tearDown() {
		iterator = null;
	}
	
	@Test
	public void testPeek() {
		assertTrue(iterator.peek(1).getLexeme().equals(WaebricKeyword.MODULE));
		assertTrue(iterator.peek(2).getLexeme().equals("homepage"));
		assertTrue(iterator.peek(3).getLexeme().equals(WaebricKeyword.SITE));
	}
	
	@Test
	public void testNextAndHasNext() {
		Token current = iterator.next();
		
		assertTrue(iterator.hasNext(0));
		assertTrue(iterator.hasNext(1));
		assertTrue(iterator.hasNext(26));
		assertFalse(iterator.hasNext(27));
		assertFalse(iterator.hasNext(-1));

		while(iterator.hasNext()) {
			assertNotNull(current);
			current = iterator.next();
		}
		
		assertTrue(current.getLexeme().equals(WaebricKeyword.END));
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
