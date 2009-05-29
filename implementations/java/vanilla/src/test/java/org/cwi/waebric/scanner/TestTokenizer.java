package org.cwi.waebric.scanner;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.cwi.waebric.WaebricKeyword;
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
		assertEquals(TokenSort.EOF, sort);
	}
	
	@Test
	public void testSingleLineComments() throws IOException {
		StringReader reader = new StringReader("// Single line comments\n");
		WaebricTokenizer tokenizer = new WaebricTokenizer(reader, exceptions);
		
		TokenSort sort = tokenizer.nextToken();
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
		assertEquals(1, tokenizer.getTokenLineNumber());
		assertEquals(1, tokenizer.getTokenCharacterNumber());
		
		assertEquals(TokenSort.EOF, tokenizer.nextToken());
	}
	
	@Test
	public void testSymbol() throws IOException {
		StringReader reader = new StringReader("\'abc '123 '@#! '");
		WaebricTokenizer tokenizer = new WaebricTokenizer(reader, exceptions);
		
		TokenSort textSymbol = tokenizer.nextToken();
		assertEquals(TokenSort.SYMBOLCON, textSymbol);
		assertEquals("abc", tokenizer.getStringValue());
		assertEquals(1, tokenizer.getTokenLineNumber());
		assertEquals(1, tokenizer.getTokenCharacterNumber());
		
		TokenSort numeralSymbol = tokenizer.nextToken();
		assertEquals(TokenSort.SYMBOLCON, numeralSymbol);
		assertEquals("123", tokenizer.getStringValue());
		assertEquals(1, tokenizer.getTokenLineNumber());
		assertEquals(6, tokenizer.getTokenCharacterNumber());	
		
		TokenSort asciiSymbol = tokenizer.nextToken();
		assertEquals(TokenSort.SYMBOLCON, asciiSymbol);
		assertEquals("@#!", tokenizer.getStringValue());
		assertEquals(1, tokenizer.getTokenLineNumber());
		assertEquals(11, tokenizer.getTokenCharacterNumber());
		
		TokenSort emptySymbol = tokenizer.nextToken();
		assertEquals(TokenSort.SYMBOLCON, emptySymbol);
		assertEquals("", tokenizer.getStringValue());
		assertEquals(1, tokenizer.getTokenLineNumber());
		assertEquals(16, tokenizer.getTokenCharacterNumber());
		
		assertEquals(TokenSort.EOF, tokenizer.nextToken());
	}
	
	@Test
	public void testSymbolCharacter() throws IOException {
		StringReader reader = new StringReader("@");
		WaebricTokenizer tokenizer = new WaebricTokenizer(reader, exceptions);
		
		TokenSort atSymbol = tokenizer.nextToken();
		assertEquals(TokenSort.SYMBOLCHAR, atSymbol);
		assertEquals(1, tokenizer.getTokenLineNumber());
		assertEquals(1, tokenizer.getTokenCharacterNumber());
		
		assertEquals(TokenSort.EOF, tokenizer.nextToken());
	}
	
	@Test
	public void testNumber() throws IOException {
		StringReader reader = new StringReader("00713379001");
		WaebricTokenizer tokenizer = new WaebricTokenizer(reader, exceptions);
		
		TokenSort atSymbol = tokenizer.nextToken();
		assertEquals(TokenSort.NATCON, atSymbol);
		assertEquals(713379001, tokenizer.getIntegerValue());
		assertEquals(1, tokenizer.getTokenLineNumber());
		assertEquals(1, tokenizer.getTokenCharacterNumber());
		
		assertEquals(TokenSort.EOF, tokenizer.nextToken());
	}
	
	@Test
	public void testIdentifier() throws IOException {
		StringReader reader = new StringReader("identifier1");
		WaebricTokenizer tokenizer = new WaebricTokenizer(reader, exceptions);
		
		TokenSort atSymbol = tokenizer.nextToken();
		assertEquals(TokenSort.IDCON, atSymbol);
		assertEquals("identifier1", tokenizer.getStringValue());
		assertEquals(1, tokenizer.getTokenLineNumber());
		assertEquals(1, tokenizer.getTokenCharacterNumber());
		
		assertEquals(TokenSort.EOF, tokenizer.nextToken());
	}
	
	@Test
	public void testKeyword() throws IOException {
		StringReader reader = new StringReader("module");
		WaebricTokenizer tokenizer = new WaebricTokenizer(reader, exceptions);
		
		TokenSort atSymbol = tokenizer.nextToken();
		assertEquals(TokenSort.KEYWORD, atSymbol);
		assertEquals(WaebricKeyword.MODULE, WaebricKeyword.valueOf(tokenizer.getStringValue()));
		assertEquals(1, tokenizer.getTokenLineNumber());
		assertEquals(1, tokenizer.getTokenCharacterNumber());
		
		assertEquals(TokenSort.EOF, tokenizer.nextToken());
	}
	
}
