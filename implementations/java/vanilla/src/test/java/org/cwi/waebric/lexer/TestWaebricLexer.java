package org.cwi.waebric.lexer;

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

}