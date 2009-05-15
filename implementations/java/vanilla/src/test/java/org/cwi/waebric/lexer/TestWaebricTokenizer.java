package org.cwi.waebric.lexer;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.cwi.waebric.lexer.token.WaebricToken;
import org.cwi.waebric.lexer.token.WaebricTokenIdentifier;
import org.junit.Before;
import org.junit.Test;

public class TestWaebricTokenizer {

	private final String PROGRAM_HW_PATH = "src/test/waebric/helloworld.waebric";
	private final String PROGRAM_IS_PATH = "src/test/waebric/invalidsymbol.waebric";
	
	private WaebricLexer lexer;
	private WaebricTokenizer tokenizer;
	
	@Before
	public void setUp() throws Exception {
		lexer = new WaebricLexer();
		tokenizer = new WaebricTokenizer();
	}
	
	@Test
	public void testSeparateWords() throws IOException {
		String input = lexer.parseStream(new FileInputStream(PROGRAM_HW_PATH));
		String[] words = tokenizer.separateWords(input);
		assertNotNull(words);
		assertTrue(words.length > 0);
		assertTrue(words[0].equals("module"));
		assertTrue(words[words.length-1].equals("end"));
	}
	
	@Test
	public void testValidInput() {
		try {
			String input = lexer.parseStream(new FileInputStream(PROGRAM_HW_PATH));
			WaebricToken[] token = lexer.tokenizeStream(input);			
			assertNotNull(token);
			assertTrue(token.length > 0);
			assertTrue(token[0].getToken() == WaebricTokenIdentifier.MODULE);
			assertTrue(token[token.length-1].getToken() == WaebricTokenIdentifier.END);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testInvalidInput() {
		try {
			String data = lexer.parseStream(new FileInputStream(PROGRAM_IS_PATH));
			WaebricToken[] token = lexer.tokenizeStream(data);
			assertNull(token[4]);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}