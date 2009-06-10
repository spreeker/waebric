package org.cwi.waebric.scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.cwi.waebric.TestUtilities;
import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.scanner.token.WaebricToken;
import org.cwi.waebric.scanner.token.WaebricTokenIterator;
import org.cwi.waebric.scanner.token.WaebricTokenSort;
import org.junit.After;
import org.junit.Test;

public class TestScanner {

	private WaebricTokenIterator iterator;
	private WaebricToken current;
	
	@After
	public void tearDown() {
		// Clean global attribute, so tests cannot affect each other
		iterator = null;
		current = null;
	}
	
	@Test
	public void testScanNumber() {
		iterator = TestUtilities.quickScan("1 2 3 99 9999 123 456");
		while(iterator.hasNext()) {
			current = iterator.next();
			assertEquals(current.getLexeme().toString(), WaebricTokenSort.NATCON, current.getSort());
		}
	}
	
	@Test
	public void testScanIdentifier() {
		iterator = TestUtilities.quickScan("identifier1abc html identifier2");
		while(iterator.hasNext()) {
			current = iterator.next();
			assertTrue(current.getSort().equals(WaebricTokenSort.IDCON));
		}
	}
	
	@Test
	public void testScanKeyword() {
		iterator = TestUtilities.quickScan("module site import def end");
		while(iterator.hasNext()) {
			current = iterator.next();
			assertTrue(current.getSort().equals(WaebricTokenSort.KEYWORD));
			assertTrue(current.getLexeme() instanceof WaebricKeyword);
		}
	}
	
	@Test
	public void testScanCharacter() {
		iterator = TestUtilities.quickScan("! @ # $ % ^ & * ( ) { } [ ] , < > ? / .");
		while(iterator.hasNext()) {
			current = iterator.next();
			assertTrue(current.getSort().equals(WaebricTokenSort.CHARACTER));
		}
		
		// Symbols as separator
		iterator = TestUtilities.quickScan("@attribute");
		
		current = iterator.next(); // Dot symbol
		assertTrue(current.getLexeme().equals('@'));
		assertTrue(current.getSort().equals(WaebricTokenSort.CHARACTER));
		
		current = iterator.next(); // Identifier
		assertTrue(current.getLexeme().equals("attribute"));
		assertTrue(current.getSort().equals(WaebricTokenSort.IDCON));
	}

	@Test
	public void testScanQuote() {
		iterator = TestUtilities.quickScan("\"text\" \"123\"");
		assertEquals("text", iterator.next().getLexeme());
		assertEquals("123", iterator.next().getLexeme());
		
		iterator = TestUtilities.quickScan("\"text 123 '@@");
		assertEquals('"', iterator.next().getLexeme());
	}
	
	@Test
	public void testScanSymbol() {
		iterator = TestUtilities.quickScan("'abc '123 '@@@ 'abc123@@@");
		assertEquals("abc", iterator.next().getLexeme());
		assertEquals("123", iterator.next().getLexeme());
		assertEquals("@@@", iterator.next().getLexeme());
		assertEquals("abc123@@@", iterator.next().getLexeme());
	}
	
	@Test
	public void testIsStringChars() {
		assertTrue(WaebricScanner.isStringChars("Hello")); // Regular word
		assertTrue(WaebricScanner.isStringChars("@")); // Symbol
		assertTrue(WaebricScanner.isStringChars("")); // Empty
		assertTrue(WaebricScanner.isStringChars("\\n")); // New-line
		assertTrue(WaebricScanner.isStringChars("\\t")); // Tab
		assertTrue(WaebricScanner.isStringChars("\\\"")); // Quote
		assertTrue(WaebricScanner.isStringChars("\\\\")); // Back
		assertFalse(WaebricScanner.isStringChars("\n")); // Regular new-line
		assertFalse(WaebricScanner.isStringChars("\t")); // Regular tab
		assertFalse(WaebricScanner.isStringChars("\"")); // Regular quote
		assertFalse(WaebricScanner.isStringChars("\\")); // Regular back
	}

	@Test
	public void testIsTextChars() {
		assertTrue(WaebricScanner.isTextChars("Hello there")); // Sentence
		assertTrue(WaebricScanner.isTextChars("Hello")); // Word
		assertTrue(WaebricScanner.isTextChars("@")); // Symbol
		assertTrue(WaebricScanner.isTextChars("\n")); // Layout
		assertTrue(WaebricScanner.isTextChars("")); // Empty
		assertTrue(WaebricScanner.isTextChars("\\&")); // &
		assertTrue(WaebricScanner.isTextChars("\\\"")); // "
		assertTrue(WaebricScanner.isTextChars("\\")); // Back
		assertFalse(WaebricScanner.isTextChars("&")); // Regular &
		assertFalse(WaebricScanner.isTextChars("\"")); // Regular "
		assertFalse(WaebricScanner.isTextChars("Hi!<")); // Text by <
	}
	
	@Test
	public void testIsSymbolChars() {
		assertTrue(WaebricScanner.isSymbolChars("abc")); // Word
		assertTrue(WaebricScanner.isSymbolChars("123")); // Number
		assertTrue(WaebricScanner.isSymbolChars("@@@")); // Symbols
		assertTrue(WaebricScanner.isSymbolChars("abc123@@@")); // All
		assertFalse(WaebricScanner.isSymbolChars(" ")); // Layout
		assertFalse(WaebricScanner.isSymbolChars("\n")); // Layout
		assertFalse(WaebricScanner.isSymbolChars("\t")); // Layout
	}
	
}