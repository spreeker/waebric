package org.cwi.waebric.scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.cwi.waebric.TestUtilities;
import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.scanner.token.Token;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.cwi.waebric.scanner.token.WaebricTokenSort;
import org.cwi.waebric.scanner.token.Token.EmbeddingToken;
import org.junit.Test;

public class TestWaebricScanner {

	@Test
	public void testText() throws IOException {
		TokenIterator i = TestUtilities.quickScan("\"text\\\"\"");
		
		Token text = i.next();
		assertEquals(WaebricTokenSort.TEXT, text.getSort());
		assertEquals("text\\\"", text.getLexeme());
	}
	
	@Test
	public void testEmptyText() throws IOException {
		TokenIterator i = TestUtilities.quickScan("\"\"");
		
		Token text = i.next();
		assertEquals(WaebricTokenSort.TEXT, text.getSort());
		assertEquals("", text.getLexeme());
	}
	
	@Test
	public void testUnclosedText() throws IOException {
		StringReader reader = new StringReader("\"text");
		WaebricScanner scanner = new WaebricScanner(reader);
		List<LexicalException> e = scanner.tokenizeStream();
		assertEquals(LexicalException.UnclosedText.class, e.get(0).getClass());
	}
	
	@Test
	public void testRegularEmbed() throws IOException {
		TokenIterator i = TestUtilities.quickScan("\"pre<123>post\"");
		Token.EmbeddingToken embedding = (EmbeddingToken) i.next();
		assertEquals(WaebricTokenSort.EMBEDDING, embedding.getSort());
		
		Iterator<Token> content = embedding.iterator();
		assertEquals('"', content.next().getLexeme());
		assertEquals("pre", content.next().getLexeme());
		assertEquals('<', content.next().getLexeme());
		assertEquals(123, content.next().getLexeme());
		assertEquals('>', content.next().getLexeme());
		assertEquals("post", content.next().getLexeme());
		assertEquals('"', content.next().getLexeme());
		assertFalse(content.hasNext());
	}
	
	@Test
	public void testQuotedEmbed() throws IOException {
		TokenIterator i = TestUtilities.quickScan("\"pre<\"\\\">\">post\"");
		Token.EmbeddingToken embedding = (EmbeddingToken) i.next();
		assertEquals(WaebricTokenSort.EMBEDDING, embedding.getSort());
		
		Iterator<Token> content = embedding.iterator();
		assertEquals('"', content.next().getLexeme());
		assertEquals("pre", content.next().getLexeme());
		assertEquals('<', content.next().getLexeme());
		assertEquals("\\\">", content.next().getLexeme());
		assertEquals('>', content.next().getLexeme());
		assertEquals("post", content.next().getLexeme());
		assertEquals('"', content.next().getLexeme());
		assertFalse(content.hasNext());
	}
	
	@Test
	public void testComplicatedEmbed() throws IOException {
		TokenIterator i = TestUtilities.quickScan("\"&copy;2007 All Rights Reserved. Design by <a(href=\"http://www.freecsstemplates.org\") \"Free CSS Templates\">\"");
		Token.EmbeddingToken embedding = (EmbeddingToken) i.next();
		assertEquals(WaebricTokenSort.EMBEDDING, embedding.getSort());
		
		Iterator<Token> content = embedding.iterator();
		assertEquals('"', content.next().getLexeme());
		assertEquals("&copy;2007 All Rights Reserved. Design by ", content.next().getLexeme());
		assertEquals('<', content.next().getLexeme());
		assertEquals("a", content.next().getLexeme());
		assertEquals('(', content.next().getLexeme());
		assertEquals("href", content.next().getLexeme());
		assertEquals('=', content.next().getLexeme());
		assertEquals("http://www.freecsstemplates.org", content.next().getLexeme());
		assertEquals(')', content.next().getLexeme());
		assertEquals("Free CSS Templates", content.next().getLexeme());
		assertEquals('>', content.next().getLexeme());
		assertEquals('"', content.next().getLexeme());
		assertFalse(content.hasNext());
	}
	
	@Test
	public void testUnclosedEmbed() throws IOException {
		StringReader reader = new StringReader("\"pre<post");
		WaebricScanner scanner = new WaebricScanner(reader);
		List<LexicalException> e = scanner.tokenizeStream();
		assertEquals(LexicalException.UnclosedEmbedding.class, e.get(0).getClass());
	}
	
	@Test
	public void testIdCon() throws IOException {
		TokenIterator i = TestUtilities.quickScan("identifier1");
		Token identifier = i.next();
		assertEquals(WaebricTokenSort.IDCON, identifier.getSort());
		assertEquals("identifier1", identifier.getLexeme());
	}
	
	@Test
	public void testNatCon() throws IOException {
		TokenIterator i = TestUtilities.quickScan("1337");
		Token natural = i.next();
		assertEquals(WaebricTokenSort.NATCON, natural.getSort());
		assertEquals(1337, natural.getLexeme());
	}
	
	@Test
	public void testSymbolCon() throws IOException {
		TokenIterator i = TestUtilities.quickScan("'symbol");
		Token symbol = i.next();
		assertEquals(WaebricTokenSort.SYMBOLCON, symbol.getSort());
		assertEquals("symbol", symbol.getLexeme());
	}
	
	@Test
	public void testKeyword() throws IOException {
		TokenIterator i = TestUtilities.quickScan("module");
		Token keyword = i.next();
		assertEquals(WaebricTokenSort.KEYWORD, keyword.getSort());
		assertEquals(WaebricKeyword.MODULE, keyword.getLexeme());
	}
	
	@Test
	public void testCharacter() throws IOException {
		TokenIterator i = TestUtilities.quickScan("@");
		Token character = i.next();
		assertEquals(WaebricTokenSort.CHARACTER, character.getSort());
		assertEquals('@', character.getLexeme());
	}
	
	@Test
	public void testIsTextChar() {
		assertTrue(WaebricScanner.isText("&#123;program"));
		assertFalse(WaebricScanner.isText("&#;program"));
		
		assertTrue(WaebricScanner.isText("&#xaBc123;program"));
		assertFalse(WaebricScanner.isText("&#xg;program"));
		
		assertTrue(WaebricScanner.isText("&amp;volume="));
		assertTrue(WaebricScanner.isText("&;program"));
		assertTrue(WaebricScanner.isText("&.;program"));
		assertFalse(WaebricScanner.isText("&"));
		
		assertTrue(WaebricScanner.isText("\\\""));
		assertTrue(WaebricScanner.isText("\\&"));
		assertFalse(WaebricScanner.isText("&"));
		assertFalse(WaebricScanner.isText("\""));
	}
	
}