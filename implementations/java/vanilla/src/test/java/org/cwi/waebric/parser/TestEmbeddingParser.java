package org.cwi.waebric.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.StringLiteral;
import org.cwi.waebric.parser.ast.embedding.Embed;
import org.cwi.waebric.parser.ast.embedding.Embedding;
import org.cwi.waebric.parser.ast.embedding.MidText;
import org.cwi.waebric.parser.ast.embedding.PostText;
import org.cwi.waebric.parser.ast.embedding.PreText;
import org.cwi.waebric.parser.ast.embedding.TextTail;
import org.cwi.waebric.parser.ast.embedding.Embed.ExpressionEmbed;
import org.cwi.waebric.parser.ast.embedding.Embed.MarkupEmbed;
import org.cwi.waebric.parser.ast.embedding.TextTail.MidTail;
import org.cwi.waebric.parser.ast.embedding.TextTail.PostTail;
import org.cwi.waebric.parser.ast.expressions.Expression;
import org.cwi.waebric.parser.ast.markup.Markup;
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
		
		Embedding simple = parser.parseEmbedding();
		assertTrue(exceptions.size() == 0);
		assertEquals("", simple.getPre().getText().toString());
		assertEquals(0, simple.getEmbed().getMarkupCount());
		assertEquals(TextTail.PostTail.class, simple.getTail().getClass());
		
		iterator = TestScanner.quickScan("\"left<func1() 123>right\"");
		parser = new EmbeddingParser(iterator, exceptions);
		
		Embedding extended = parser.parseEmbedding();
		assertTrue(exceptions.size() == 0);
		assertEquals("left", extended.getPre().getText().toString());
		assertEquals(1, extended.getEmbed().getMarkupCount());
		assertEquals(Markup.MarkupWithArguments.class, extended.getEmbed().getMarkup(0).getClass());
		assertEquals(TextTail.PostTail.class, extended.getTail().getClass());
	}
	
	@Test
	public void testEmbed() {
		// Expression only embed
		iterator = TestScanner.quickScan("123");
		parser = new EmbeddingParser(iterator, exceptions);
		
		Embed.ExpressionEmbed simple = (ExpressionEmbed) parser.parseEmbed();
		assertTrue(exceptions.size() == 0);
		assertEquals(Expression.NatExpression.class, simple.getExpression().getClass());
		assertEquals(0, simple.getMarkupCount());
		
		// Embed with single mark-up
		iterator = TestScanner.quickScan("func1 123>");
		parser = new EmbeddingParser(iterator, exceptions);
		
		Embed.ExpressionEmbed diff = (ExpressionEmbed) parser.parseEmbed();
		assertTrue(exceptions.size() == 0);
		assertEquals(Expression.NatExpression.class, diff.getExpression().getClass());
		assertEquals(1, diff.getMarkupCount());
		assertEquals(Markup.MarkupWithoutArguments.class, diff.getMarkup(0).getClass());
		
		// Embed with multiple mark-up
		iterator = TestScanner.quickScan("func1(arg1) func2 123>");
		parser = new EmbeddingParser(iterator, exceptions);
		
		Embed.ExpressionEmbed diff2 = (ExpressionEmbed) parser.parseEmbed();
		assertTrue(exceptions.size() == 0);
		assertEquals(Expression.NatExpression.class, diff2.getExpression().getClass());
		assertEquals(2, diff2.getMarkupCount());
		assertEquals(Markup.MarkupWithArguments.class, diff2.getMarkup(0).getClass());
		assertEquals(Markup.MarkupWithoutArguments.class, diff2.getMarkup(1).getClass());
		
		// Embed Markup* Markup
		iterator = TestScanner.quickScan("func1(arg1) func2 func3>");
		parser = new EmbeddingParser(iterator, exceptions);
		Embed.MarkupEmbed markupemb = (MarkupEmbed) parser.parseEmbed();
		assertTrue(exceptions.size() == 0);
		assertEquals(Markup.MarkupWithoutArguments.class, markupemb.getMarkup().getClass());
		assertEquals(2, diff2.getMarkupCount());
		assertEquals(Markup.MarkupWithArguments.class, diff2.getMarkup(0).getClass());
		assertEquals(Markup.MarkupWithoutArguments.class, diff2.getMarkup(1).getClass());
	}
	
	@Test
	public void testPreText() {
		iterator = TestScanner.quickScan("\"left<");
		parser = new EmbeddingParser(iterator, exceptions);
		
		PreText text = parser.parsePreText();
		assertTrue(exceptions.size() == 0);
		assertEquals("left", text.getText().toString());
	}
	
	@Test
	public void testTextTail() {
		iterator = TestScanner.quickScan(">right\"");
		parser = new EmbeddingParser(iterator, exceptions);
		
		TextTail.PostTail post = (PostTail) parser.parseTextTail();
		assertTrue(exceptions.size() == 0);
		assertEquals("right", post.getPost().getText().toString());
		
		iterator = TestScanner.quickScan(">mid<123>\"");
		parser = new EmbeddingParser(iterator, exceptions);
		
		TextTail.MidTail mid = (MidTail) parser.parseTextTail();
		assertTrue(exceptions.size() == 0);
		assertEquals("mid", mid.getMid().getText().toString());
		assertEquals(TextTail.PostTail.class, mid.getTail().getClass());
	}
	
	@Test
	public void testPostTest() {
		iterator = TestScanner.quickScan(">right\"");
		parser = new EmbeddingParser(iterator, exceptions);
		
		PostText text = parser.parsePostText();
		assertTrue(exceptions.size() == 0);
		assertEquals("right", text.getText().toString());
	}
	
	@Test
	public void testMidText() {
		iterator = TestScanner.quickScan(">mid<");
		parser = new EmbeddingParser(iterator, exceptions);
		
		MidText text = parser.parseMidText();
		assertTrue(exceptions.size() == 0);
		assertEquals("mid", text.getText().toString());
	}
	
	@Test
	public void testTextChars() {
		iterator = TestScanner.quickScan("left<");
		parser = new EmbeddingParser(iterator, exceptions);
		
		StringLiteral text = parser.parseTextChars();
		assertTrue(exceptions.size() == 0);
		assertEquals("left", text.toString());
	}

}