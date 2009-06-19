package org.cwi.waebric.scanner;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.scanner.token.Token;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.cwi.waebric.scanner.token.WaebricTokenSort;
import org.junit.Test;

public class TestWaebricScanner {

	@Test
	public void testText() throws IOException {
		StringReader reader = new StringReader("\"text\"");
		WaebricScanner scanner = new WaebricScanner(reader);
		TokenIterator i = scanner.tokenizeStream();
		
		Token text = i.next();
		assertEquals(WaebricTokenSort.TEXT, text.getSort());
		assertEquals("text", text.getLexeme());
	}
	
	@Test
	public void testInvalidText() throws IOException {
		StringReader reader = new StringReader("\"text");
		WaebricScanner scanner = new WaebricScanner(reader);
		TokenIterator i = scanner.tokenizeStream();
		
		Token quote = i.next();
		assertEquals(WaebricTokenSort.CHARACTER, quote.getSort());
		assertEquals('"', quote.getLexeme());
		
		Token text = i.next();
		assertEquals(WaebricTokenSort.IDCON, text.getSort());
		assertEquals("text", text.getLexeme());
	}
	
	@Test
	public void testRegularEmbed() throws IOException {
		StringReader reader = new StringReader("\"pre<123>post\"");
		WaebricScanner scanner = new WaebricScanner(reader);
		TokenIterator i = scanner.tokenizeStream();
		
		Token embedding = i.next();
		assertEquals(WaebricTokenSort.EMBEDDING, embedding.getSort());
		assertEquals("pre<123>post", embedding.getLexeme());
	}
	
	@Test
	public void testQuotedEmbed() throws IOException {
		StringReader reader = new StringReader("\"pre<\">\">post\"");
		WaebricScanner scanner = new WaebricScanner(reader);
		TokenIterator i = scanner.tokenizeStream();
		
		Token embedding = i.next();
		assertEquals(WaebricTokenSort.EMBEDDING, embedding.getSort());
		assertEquals("pre<\">\">post", embedding.getLexeme());
	}
	
	@Test
	public void testIdCon() throws IOException {
		StringReader reader = new StringReader("identifier1");
		WaebricScanner scanner = new WaebricScanner(reader);
		TokenIterator i = scanner.tokenizeStream();
		
		Token identifier = i.next();
		assertEquals(WaebricTokenSort.IDCON, identifier.getSort());
		assertEquals("identifier1", identifier.getLexeme());
	}
	
	@Test
	public void testNatCon() throws IOException {
		StringReader reader = new StringReader("1337");
		WaebricScanner scanner = new WaebricScanner(reader);
		TokenIterator i = scanner.tokenizeStream();
		
		Token natural = i.next();
		assertEquals(WaebricTokenSort.NATCON, natural.getSort());
		assertEquals(1337, natural.getLexeme());
	}
	
	@Test
	public void testSymbolCon() throws IOException {
		StringReader reader = new StringReader("'symbol");
		WaebricScanner scanner = new WaebricScanner(reader);
		TokenIterator i = scanner.tokenizeStream();
		
		Token symbol = i.next();
		assertEquals(WaebricTokenSort.SYMBOLCON, symbol.getSort());
		assertEquals("symbol", symbol.getLexeme());
	}
	
	@Test
	public void testKeyword() throws IOException {
		StringReader reader = new StringReader("module");
		WaebricScanner scanner = new WaebricScanner(reader);
		TokenIterator i = scanner.tokenizeStream();
		
		Token keyword = i.next();
		assertEquals(WaebricTokenSort.KEYWORD, keyword.getSort());
		assertEquals(WaebricKeyword.MODULE, keyword.getLexeme());
	}
	
	@Test
	public void testCharacter() throws IOException {
		StringReader reader = new StringReader("@");
		WaebricScanner scanner = new WaebricScanner(reader);
		TokenIterator i = scanner.tokenizeStream();
		
		Token character = i.next();
		assertEquals(WaebricTokenSort.CHARACTER, character.getSort());
		assertEquals('@', character.getLexeme());
	}
	
}