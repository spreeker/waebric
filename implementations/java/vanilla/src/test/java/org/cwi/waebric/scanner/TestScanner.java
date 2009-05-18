package org.cwi.waebric.scanner;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.cwi.waebric.scanner.token.Token;
import org.cwi.waebric.scanner.token.TokenSort;
import org.cwi.waebric.scanner.token.WaebricLiteral;
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
			List<Token> tokens = scanner.getTokens();
			assertNotNull(tokens);
			assertTrue(tokens.size() > 0);
			
			// Assert literals
			assertTrue(tokens.get(0).getSort().equals(TokenSort.LITERAL));
			assertTrue(tokens.get(0).getLexeme().equals(WaebricLiteral.MODULE));
			assertTrue(tokens.get(tokens.size()-1).getSort().equals(TokenSort.LITERAL));
			assertTrue(tokens.get(tokens.size()-1).getLexeme().equals(WaebricLiteral.END));
			
			// Assert identifiers
			assertTrue(tokens.get(1).getSort().equals(TokenSort.IDCON));
			assertTrue(tokens.get(1).getLexeme().equals("test"));

			// Assert text
			assertTrue(tokens.get(8).getSort().equals(TokenSort.TEXT));
			assertTrue(tokens.get(8).getLexeme().equals("Hello world"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testIsLiteral() {
		assertTrue(WaebricScanner.isLiteral("module"));
		assertFalse(WaebricScanner.isLiteral("rofl"));
	}
	
	@Test
	public void testIsIdentifier() {
		assertTrue(WaebricScanner.isIdentifier("identifier"));
		assertTrue(WaebricScanner.isIdentifier("identifier1"));
		assertFalse(WaebricScanner.isIdentifier("1identifier"));
		assertFalse(WaebricScanner.isIdentifier("@identifier"));
		assertFalse(WaebricScanner.isIdentifier(" identifier"));
		assertFalse(WaebricScanner.isIdentifier(""));
		assertFalse(WaebricScanner.isIdentifier(null));
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
