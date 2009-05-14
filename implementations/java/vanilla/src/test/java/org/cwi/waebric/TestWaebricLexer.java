package org.cwi.waebric;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestWaebricLexer {
	
	final String PROGRAM_PATH = "src/test/waebric/helloworld.waebric";
	
	private WaebricLexer lexer;
	private InputStream is;
	
	@Before
	public void setUp() throws FileNotFoundException {
		lexer = new WaebricLexer();
		is = new FileInputStream(PROGRAM_PATH);
	}
	
	@After
	public void tearDown() throws IOException {
		is.close();
	}

	@Test
	public void testParseStream() {
		try {
			String input = lexer.parseStream(is);
			assertNotNull(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSeparateWords() {
		try {
			String input = lexer.parseStream(is);
			String[] data = lexer.separateWords(input);
			assertNotNull(data);
			assertTrue(data.length > 0);
			assertTrue(data[0].equals("module"));
			assertTrue(data[data.length-1].equals("end"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testTokenizeStream() {
		try {
			Object[] data = lexer.tokenizeStream(is);
			assertNotNull(data);
			assertTrue(data.length > 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}