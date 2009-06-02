package org.cwi.waebric.scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.cwi.waebric.scanner.exception.ScannerException;
import org.cwi.waebric.scanner.token.Token;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.cwi.waebric.scanner.token.TokenSort;
import org.junit.After;
import org.junit.Test;

public class TestScanner {

	private TokenIterator iterator;
	private Token current;
	
	@After
	public void tearDown() {
		// Clean global attribute, so tests cannot affect each other
		iterator = null;
		current = null;
	}
	
	/**
	 * Quickly perform scan based on raw string data,
	 * use this method to make tests smaller and easier
	 * to understand.
	 * 
	 * During the scan multiple assertions are done,
	 * assuring that zero exceptions are caught.
	 * 
	 * @param data
	 * @return iterator
	 * @throws IOException
	 */
	public static TokenIterator quickScan(String data) {
		Reader reader = new StringReader(data);
		try {
			WaebricScanner scanner = new WaebricScanner(reader);
			List<ScannerException> exceptions = scanner.tokenizeStream();
			assertNotNull(exceptions);
			assertTrue(exceptions.size() == 0);
			return scanner.iterator();
		} catch(IOException e) {
			fail(e.getMessage());
			return null;
		}
	}
	
	@Test
	public void testScanIdentifier() {
		iterator = quickScan("identifier1 html identifier2");
		while(iterator.hasNext()) {
			current = iterator.next();
			assertTrue(current.getSort().equals(TokenSort.IDCON));
		}
	}
	
	@Test
	public void testScanNumber() {
		iterator = quickScan("1 2 3 99 9999 123 456");
		while(iterator.hasNext()) {
			current = iterator.next();
			assertEquals(current.getLexeme().toString(), TokenSort.NATCON, current.getSort());
		}
	}
	
	@Test
	public void testScanKeyword() {
		iterator = quickScan("module site import def end");
		while(iterator.hasNext()) {
			current = iterator.next();
			assertTrue(current.getSort().equals(TokenSort.KEYWORD));
		}
	}
	
	@Test
	public void testScanSymbol() {
		iterator = quickScan("! @ # $ % ^ & * ( ) { } [ ] , < > ? / .");
		while(iterator.hasNext()) {
			current = iterator.next();
			assertTrue(current.getSort().equals(TokenSort.SYMBOLCHAR));
		}
		
		// Symbols as separator
		iterator = quickScan("@attribute");
		current = iterator.next(); // Dot symbol
		assertTrue(current.getLexeme().equals('@'));
		assertTrue(current.getSort().equals(TokenSort.SYMBOLCHAR));
		current = iterator.next(); // Identifier
		assertTrue(current.getLexeme().equals("attribute"));
		assertTrue(current.getSort().equals(TokenSort.IDCON));
	}
	
	@Test
	public void testString() {
		iterator = quickScan("\"String, using a\\nnew line!\"");
		current = iterator.next();
		assertEquals(TokenSort.STRCON, current.getSort());
		assertEquals("String, using a\\nnew line!", current.getLexeme().toString());
		assertFalse(iterator.hasNext());
	}
	
	@Test
	public void testIsString() {
		assertTrue(WaebricScanner.isString("Hello")); // Regular word
		assertTrue(WaebricScanner.isString("@")); // Symbol
		assertTrue(WaebricScanner.isString("")); // Empty
		assertTrue(WaebricScanner.isString("\\n")); // New-line
		assertTrue(WaebricScanner.isString("\\t")); // Tab
		assertTrue(WaebricScanner.isString("\\\"")); // Quote
		assertTrue(WaebricScanner.isString("\\\\")); // Back
		assertFalse(WaebricScanner.isString("\n")); // Regular new-line
		assertFalse(WaebricScanner.isString("\t")); // Regular tab
		assertFalse(WaebricScanner.isString("\"")); // Regular quote
		assertFalse(WaebricScanner.isString("\\")); // Regular back
	}

	@Test
	public void testScanText() {
		iterator = quickScan("\"Text, using a \nnew line!\"<");
		current = iterator.next();
		assertEquals(TokenSort.TEXT, current.getSort());
		assertEquals("Text, using a \nnew line!", current.getLexeme().toString());
		
		Token symbol = iterator.next();
		assertEquals(TokenSort.SYMBOLCHAR, symbol.getSort());
		assertEquals("<", symbol.getLexeme().toString());
	}
	
	@Test
	public void testIsText() {
		assertTrue(WaebricScanner.isText("Hello there")); // Sentence
		assertTrue(WaebricScanner.isText("Hello")); // Word
		assertTrue(WaebricScanner.isText("@")); // Symbol
		assertTrue(WaebricScanner.isText("\n")); // Layout
		assertTrue(WaebricScanner.isText("")); // Empty
		assertTrue(WaebricScanner.isText("\\&")); // &
		assertTrue(WaebricScanner.isText("\\\"")); // "
		assertTrue(WaebricScanner.isText("\\")); // Back
		assertFalse(WaebricScanner.isText("&")); // Regular &
		assertFalse(WaebricScanner.isText("\"")); // Regular "
		assertFalse(WaebricScanner.isText("Hi!<")); // Text by <
	}
	
	@Test
	public void testTextDelegate() {
		iterator = quickScan("\"no string\nno text<\"");
		while(iterator.hasNext()) { current = iterator.next(); }
		
		// Last token
		assertEquals(TokenSort.SYMBOLCHAR, current.getSort());
		assertEquals("\"", current.getLexeme().toString());
		assertEquals(2, current.getLine());
		assertEquals(9, current.getCharacter());		
	}
	
}