package org.cwi.waebric.lexer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestWaebricTokenizer {

	final String PROGRAM_PATH = "src/test/waebric/helloworld.waebric";
	
	private WaebricTokenizer tokenizer;
	private String data;
	
	@Before
	public void setUp() throws Exception {
		tokenizer = new WaebricTokenizer();
		data = new WaebricLexer().parseStream(new FileInputStream(PROGRAM_PATH));
	}

	@After
	public void tearDown() throws Exception {
		tokenizer = null;
		data = null;
	}
	
	@Test
	public void testTokenizeInput() {
		
	}
	
	@Test
	public void testSeparateWords() {
		String[] words = tokenizer.separateWords(data);
		assertNotNull(words);
		assertTrue(words.length > 0);
		assertTrue(words[0].equals("module"));
		assertTrue(words[words.length-1].equals("end"));
	}

}