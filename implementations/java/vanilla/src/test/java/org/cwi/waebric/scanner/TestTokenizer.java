package org.cwi.waebric.scanner;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.cwi.waebric.scanner.exception.ScannerException;
import org.cwi.waebric.scanner.token.TokenSort;
import org.junit.Before;
import org.junit.Test;

public class TestTokenizer {

	private ArrayList<ScannerException> exceptions = new ArrayList<ScannerException>();
	
	@Before
	public void setUp() {
		exceptions.clear();
	}
	
	@Test
	public void testMultipleLineComments() throws IOException {
		StringReader reader = new StringReader("/* Multi line comments */");
		WaebricTokenizer tokenizer = new WaebricTokenizer(reader, exceptions);
		
		TokenSort sort = tokenizer.nextToken();
		// Comments is not processed, thus it reads an empty program
		assertEquals(TokenSort.EOF, sort);
	}
	
	@Test
	public void testSingleLineComments() throws IOException {
		StringReader reader = new StringReader("// Single line comments\n");
		WaebricTokenizer tokenizer = new WaebricTokenizer(reader, exceptions);
		
		TokenSort sort = tokenizer.nextToken();
		// Comments is not processed, thus it reads an empty program
		assertEquals(TokenSort.EOF, sort);
	}
	
	@Test
	public void testLayout() throws IOException {
		StringReader reader = new StringReader("id1\nid2\tid3");
		WaebricTokenizer tokenizer = new WaebricTokenizer(reader, exceptions);
		
		TokenSort id1 = tokenizer.nextToken();
		assertEquals(TokenSort.IDCON, id1);
		assertEquals(1, tokenizer.getTokenLineNumber());
		assertEquals(1, tokenizer.getTokenCharacterNumber());
		
		TokenSort id2 = tokenizer.nextToken();
		assertEquals(TokenSort.IDCON, id2);
		assertEquals(2, tokenizer.getTokenLineNumber());
		assertEquals(1, tokenizer.getTokenCharacterNumber());

		TokenSort id3 = tokenizer.nextToken();
		assertEquals(TokenSort.IDCON, id3);
		assertEquals(2, tokenizer.getTokenLineNumber());
		assertEquals(9, tokenizer.getTokenCharacterNumber());
		
		assertEquals(TokenSort.EOF, tokenizer.nextToken());
	}
	
	@Test
	public void testString() throws IOException {
		StringReader reader = new StringReader("\"text\"");
		WaebricTokenizer tokenizer = new WaebricTokenizer(reader, exceptions);
		
		TokenSort sort = tokenizer.nextToken();
		assertEquals(TokenSort.STRCON, sort);
		assertEquals(1, tokenizer.getLineNumber());
		assertEquals(6, tokenizer.getCharacterNumber());
		assertEquals(TokenSort.EOF, tokenizer.nextToken());
	}
	
	@Test
	public void testSymbol() throws IOException {
		StringReader reader = new StringReader("\'abc '123 '@#! '");
		WaebricTokenizer tokenizer = new WaebricTokenizer(reader, exceptions);
		
		TokenSort textSymbol = tokenizer.nextToken();
		assertEquals(TokenSort.SYMBOLCON, textSymbol);
		assertEquals("abc", tokenizer.getStringValue());
		assertEquals(1, tokenizer.getLineNumber());
		assertEquals(5, tokenizer.getCharacterNumber());
		
		TokenSort numeralSymbol = tokenizer.nextToken();
		assertEquals(TokenSort.SYMBOLCON, numeralSymbol);
		assertEquals("123", tokenizer.getStringValue());
		assertEquals(1, tokenizer.getLineNumber());
		assertEquals(10, tokenizer.getCharacterNumber());	
		
		TokenSort asciiSymbol = tokenizer.nextToken();
		assertEquals(TokenSort.SYMBOLCON, asciiSymbol);
		assertEquals("@#!", tokenizer.getStringValue());
		assertEquals(1, tokenizer.getLineNumber());
		assertEquals(15, tokenizer.getCharacterNumber());
		
		TokenSort emptySymbol = tokenizer.nextToken();
		assertEquals(TokenSort.SYMBOLCHAR, emptySymbol);
		assertEquals('\'', tokenizer.getCharacterValue());
		assertEquals(1, tokenizer.getLineNumber());
		assertEquals(16, tokenizer.getCharacterNumber());
		
		assertEquals(TokenSort.EOF, tokenizer.nextToken());
	}
	
	@Test
	public void testSymbolCharacter() throws IOException {
		
	}
	
	@Test
	public void testNumber() throws IOException {
		
	}
	
	@Test
	public void testIdentifier() throws IOException {
		
	}
	
	@Test
	public void testKeyword() throws IOException {
		
	}
	
}
