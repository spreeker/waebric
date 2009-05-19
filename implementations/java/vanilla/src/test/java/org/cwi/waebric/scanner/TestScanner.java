package org.cwi.waebric.scanner;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.cwi.waebric.scanner.token.Token;
import org.cwi.waebric.scanner.token.TokenSort;
import org.cwi.waebric.scanner.token.WaebricKeyword;
import org.junit.Test;

public class TestScanner {

	private final String PROGRAM_PATH = "src/test/waebric/helloworld.waebric";

	@Test
	public void testScanner() {
		try {
			FileReader reader = new FileReader(PROGRAM_PATH);
			WaebricScanner scanner = new WaebricScanner(reader);
			scanner.tokenizeStream();
			
			// Retrieve tokens
			Iterator<Token> tokens = scanner.iterator();
			assertNotNull(tokens);
			assertTrue(tokens.hasNext());
			
			// Assert Waebric keywords
			assertTrue(scanner.getToken(0).getSort().equals(TokenSort.KEYWORD));
			assertTrue(scanner.getToken(0).getLexeme().equals(WaebricKeyword.MODULE));
			assertTrue(scanner.getToken(scanner.getSize()-1).getSort().equals(TokenSort.KEYWORD));
			assertTrue(scanner.getToken(scanner.getSize()-1).getLexeme().equals(WaebricKeyword.END));
			
			// Assert HTML keywords
			assertTrue(scanner.getToken(4).getSort().equals(TokenSort.IDCON));
			assertTrue(scanner.getToken(4).getLexeme().equals("html"));
			
			// Assert identifiers
			assertTrue(scanner.getToken(1).getSort().equals(TokenSort.IDCON));
			assertTrue(scanner.getToken(1).getLexeme().equals("test"));
			
			// Assert symbols
			assertTrue(scanner.getToken(5).getSort().equals(TokenSort.SYMBOL));
			assertTrue(scanner.getToken(5).getLexeme().equals('{'));

			// Assert text
			assertTrue(scanner.getToken(8).getSort().equals(TokenSort.TEXT));
			assertTrue(scanner.getToken(8).getLexeme().equals("Hello world"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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