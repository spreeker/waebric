package org.cwi.waebric.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.embedding.MidText;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.TestScanner;
import org.cwi.waebric.scanner.token.WaebricTokenIterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestEmbeddingParser {

	private EmbeddingParser parser;
	
	private List<ParserException> exceptions;
	private WaebricTokenIterator iterator;
	
	@Before
	public void setUp() {
		exceptions = new ArrayList<ParserException>();
	}
	
	@After
	public void tearDown() {
		exceptions.clear();
		exceptions = null;
		parser = null;
		iterator = null;
	}
	
	@Test
	public void testEmbedding() {
		iterator = TestScanner.quickScan("\"<123>\"");
		parser = new EmbeddingParser(iterator, exceptions);
		
		iterator = TestScanner.quickScan("\"left<func1() 123>right\"");
		parser = new EmbeddingParser(iterator, exceptions);
		// TODO: Create test assertions
	}
	
	@Test
	public void testEmbed() {
		// Simple embed without closure symbol
		iterator = TestScanner.quickScan("123");
		parser = new EmbeddingParser(iterator, exceptions);
		
		// Complicated embed with closure symbol <
		iterator = TestScanner.quickScan("func1(arg1) func2 123>");
		parser = new EmbeddingParser(iterator, exceptions);
		
		// Complicated embed without closure symbol <
		iterator = TestScanner.quickScan("func1(arg1) func2 123");
		parser = new EmbeddingParser(iterator, exceptions);
		// TODO: Create test assertions
	}
	
	@Test
	public void testPreText() {
//		iterator = TestScanner.quickScan("\"left<");
//		parser = new EmbeddingParser(iterator, exceptions);
		// TODO: Create test assertions
	}
	
	@Test
	public void testTextTail() {
//		iterator = TestScanner.quickScan(">right\"");
//		parser = new EmbeddingParser(iterator, exceptions);
//		
//		iterator = TestScanner.quickScan(">mid<123>");
//		parser = new EmbeddingParser(iterator, exceptions);
	}
	
	@Test
	public void testPostTest() {
		iterator = TestScanner.quickScan(">right\"");
		parser = new EmbeddingParser(iterator, exceptions);
		
		// TODO: Create test assertions
	}
	
	@Test
	public void testMidText() {
		iterator = TestScanner.quickScan(">mid<");
		parser = new EmbeddingParser(iterator, exceptions);
		
		MidText text = parser.parseMidText();
		assertTrue(exceptions.size() == 0);
		assertEquals("mid", text.getText().toString());
	}

}