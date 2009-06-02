package org.cwi.waebric.scanner;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

public class TestTokenizer {
	
	@Test
	public void testMultipleLineComments() throws IOException {
		StringReader reader = new StringReader("/* Multi line comments */");
		StreamTokenizer tokenizer = new StreamTokenizer(reader);
		
		int sort = tokenizer.nextToken();
		assertEquals(StreamTokenizer.COMMENT, sort);
	}
	
	@Test
	public void testSingleLineComments() throws IOException {
		StringReader reader = new StringReader("// Single line comments\n");
		StreamTokenizer tokenizer = new StreamTokenizer(reader);
		
		int sort = tokenizer.nextToken();
		assertEquals(StreamTokenizer.COMMENT, sort);
	}
	
	@Test
	public void testLayout() throws IOException {
		StringReader reader = new StringReader("id1\nid2\tid3");
		StreamTokenizer tokenizer = new StreamTokenizer(reader);
		
		int id1 = tokenizer.nextToken();
		assertEquals(StreamTokenizer.WORD, id1);
		assertEquals(1, tokenizer.getTokenLineNumber());
		assertEquals(1, tokenizer.getTokenCharacterNumber());
		
		tokenizer.nextToken();
		int id2 = tokenizer.nextToken();
		assertEquals(StreamTokenizer.WORD, id2);
		assertEquals(2, tokenizer.getTokenLineNumber());
		assertEquals(1, tokenizer.getTokenCharacterNumber());

		tokenizer.nextToken();
		int id3 = tokenizer.nextToken();
		assertEquals(StreamTokenizer.WORD, id3);
		assertEquals(2, tokenizer.getTokenLineNumber());
		assertEquals(9, tokenizer.getTokenCharacterNumber());
	}
	
	@Test
	public void testWord() throws IOException {
		StringReader reader = new StringReader("word");
		StreamTokenizer tokenizer = new StreamTokenizer(reader);
		
		int word = tokenizer.nextToken();
		assertEquals(StreamTokenizer.WORD, word);
		assertEquals("word", tokenizer.getStringValue());
		assertEquals(1, tokenizer.getTokenLineNumber());
		assertEquals(1, tokenizer.getTokenCharacterNumber());
	}
	
	@Test
	public void testNumber() throws IOException {
		StringReader reader = new StringReader("123");
		StreamTokenizer tokenizer = new StreamTokenizer(reader);
		
		int word = tokenizer.nextToken();
		assertEquals(StreamTokenizer.NUMBER, word);
		assertEquals(123, tokenizer.getIntegerValue());
		assertEquals(1, tokenizer.getTokenLineNumber());
		assertEquals(1, tokenizer.getTokenCharacterNumber());
	}
	
	@Test
	public void testCharacter() throws IOException {
		StringReader reader = new StringReader("@");
		StreamTokenizer tokenizer = new StreamTokenizer(reader);
		
		int word = tokenizer.nextToken();
		assertEquals(StreamTokenizer.CHARACTER, word);
		assertEquals('@', tokenizer.getCharacterValue());
		assertEquals(1, tokenizer.getTokenLineNumber());
		assertEquals(1, tokenizer.getTokenCharacterNumber());
	}
	
}