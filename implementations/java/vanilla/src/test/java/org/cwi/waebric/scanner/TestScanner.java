package org.cwi.waebric.scanner;

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
		WaebricScanner scanner = new WaebricScanner(reader);
		
		try {
			List<ScannerException> exceptions = scanner.tokenizeStream();
			assertNotNull(exceptions);
			assertTrue(exceptions.size() == 0);
		} catch(IOException e) {
			fail(e.getMessage());
		}
		
		return scanner.iterator();
	}
	
	@Test
	public void testScanIdentifier() {
		iterator = quickScan("identifier1 html module1.identifier identifier2");
		while(iterator.hasNext()) {
			current = iterator.next();
			assertTrue(current.getSort().equals(TokenSort.IDENTIFIER));
		}
	}
	
	@Test
	public void testScanNumber() {
		iterator = quickScan("1 2 3 99 9999 123.456 0 -1 -99");
		while(iterator.hasNext()) {
			current = iterator.next();
			assertTrue(current.getSort().equals(TokenSort.NUMBER));
		}
	}
	
	@Test
	public void testScanTest() {
		iterator = quickScan("\"text1\" \"text2\" \"text3\"");
		while(iterator.hasNext()) {
			current = iterator.next();
			assertTrue(current.getSort().equals(TokenSort.TEXT));
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
		iterator = quickScan("! @ # $ % ^ & * ( ) { } [ ] , < > ? /");
		while(iterator.hasNext()) {
			current = iterator.next();
			assertTrue(current.getSort().equals(TokenSort.SYMBOL));
		}
		
		// Symbols as separator
		iterator = quickScan("@attribute");
		current = iterator.next(); // Dot symbol
		assertTrue(current.getLexeme().equals('@'));
		assertTrue(current.getSort().equals(TokenSort.SYMBOL));
		current = iterator.next(); // Identifier
		assertTrue(current.getLexeme().equals("attribute"));
		assertTrue(current.getSort().equals(TokenSort.IDENTIFIER));
	}
	
	@Test
	public void testIsKeyword() {
		assertTrue(WaebricScanner.isKeyword("module"));
		assertFalse(WaebricScanner.isKeyword("rofl"));
	}

	@Test
	public void testIsIdentifier() {
		assertTrue(WaebricScanner.isIdentifier("identifier"));
		assertTrue(WaebricScanner.isIdentifier("identifier1"));
		assertTrue(WaebricScanner.isIdentifier("html"));
		assertFalse(WaebricScanner.isIdentifier("identifier@"));
		assertFalse(WaebricScanner.isIdentifier("1identifier"));
		assertFalse(WaebricScanner.isIdentifier("@identifier"));
		assertFalse(WaebricScanner.isIdentifier(" identifier"));
		assertFalse(WaebricScanner.isIdentifier(""));
		assertFalse(WaebricScanner.isIdentifier(null));
	}
	
	@Test
	public void testIsSymbol() {
		for(char c = ' '; c <= '~'; c++) {
			assertTrue(WaebricScanner.isSymbol("" + c));
		}
		
		assertFalse(WaebricScanner.isSymbol("aa"));
		assertFalse(WaebricScanner.isSymbol("11"));
		assertFalse(WaebricScanner.isSymbol(""));
		assertFalse(WaebricScanner.isSymbol(null));
	}
	
	@Test
	public void testIsLetter() {
		for(char c = 'a'; c <= 'z'; c++) {
			assertTrue(WaebricScanner.isLetter(c));
		}
		
		for(char c = 'A'; c <= 'Z'; c++) {
			assertTrue(WaebricScanner.isLetter(c));
		}

		assertFalse(WaebricScanner.isLetter(' '));
		assertFalse(WaebricScanner.isLetter('@'));
		assertFalse(WaebricScanner.isLetter('\n'));
		assertFalse(WaebricScanner.isLetter('1'));
	}
	
	@Test
	public void testIsNumber() {
		for(char c = '0'; c <= '9'; c++) {
			assertTrue(WaebricScanner.isDigit(c));
		}
		
		assertFalse(WaebricScanner.isDigit('a'));
		assertFalse(WaebricScanner.isDigit('z'));
		assertFalse(WaebricScanner.isDigit(' '));
		assertFalse(WaebricScanner.isDigit('@'));
		assertFalse(WaebricScanner.isDigit('\n'));
	}
	
}