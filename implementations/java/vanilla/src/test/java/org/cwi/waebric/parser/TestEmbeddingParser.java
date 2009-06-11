package org.cwi.waebric.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.TestUtilities;
import org.cwi.waebric.parser.ast.StringLiteral;
import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.ast.statement.embedding.Embed;
import org.cwi.waebric.parser.ast.statement.embedding.Embedding;
import org.cwi.waebric.parser.ast.statement.embedding.MidText;
import org.cwi.waebric.parser.ast.statement.embedding.PostText;
import org.cwi.waebric.parser.ast.statement.embedding.PreText;
import org.cwi.waebric.parser.ast.statement.embedding.TextTail;
import org.cwi.waebric.parser.ast.statement.embedding.Embed.ExpressionEmbed;
import org.cwi.waebric.parser.ast.statement.embedding.Embed.MarkupEmbed;
import org.cwi.waebric.parser.ast.statement.embedding.TextTail.MidTail;
import org.cwi.waebric.parser.ast.statement.embedding.TextTail.PostTail;
import org.cwi.waebric.parser.exception.SyntaxException;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestEmbeddingParser {

	private EmbeddingParser parser;
	
	private List<SyntaxException> exceptions;
	private TokenIterator iterator;
	
	@Before
	public void setUp() {
		exceptions = new ArrayList<SyntaxException>();
	}
	
	@After
	public void tearDown() {
		exceptions.clear();
		exceptions = null;
		parser = null;
		iterator = null;
	}
	
	@Test
	public void testEmbedding() throws SyntaxException {
		iterator = TestUtilities.quickScan("\"<123>\"");
		parser = new EmbeddingParser(iterator, exceptions);
		
		Embedding simple = parser.parseEmbedding();
		assertEquals("", simple.getPre().getText().toString());
		assertEquals(0, simple.getEmbed().getMarkupCount());
		assertEquals(TextTail.PostTail.class, simple.getTail().getClass());
		
		iterator = TestUtilities.quickScan("\"left<func1() 123>right\"");
		parser = new EmbeddingParser(iterator, exceptions);
		
		Embedding extended = parser.parseEmbedding();
		assertEquals("left", extended.getPre().getText().toString());
		assertEquals(1, extended.getEmbed().getMarkupCount());
		assertEquals(Markup.Call.class, extended.getEmbed().getMarkup(0).getClass());
		assertEquals(TextTail.PostTail.class, extended.getTail().getClass());
	}
	
	@Test
	public void testEmbed() throws SyntaxException {
		// Expression only embed
		iterator = TestUtilities.quickScan("123");
		parser = new EmbeddingParser(iterator, exceptions);
		
		Embed.ExpressionEmbed simple = (ExpressionEmbed) parser.parseEmbed();
		assertEquals(Expression.NatExpression.class, simple.getExpression().getClass());
		assertEquals(0, simple.getMarkupCount());
		
		// Embed with single mark-up
		iterator = TestUtilities.quickScan("func1 123>");
		parser = new EmbeddingParser(iterator, exceptions);
		
		Embed.ExpressionEmbed diff = (ExpressionEmbed) parser.parseEmbed();
		assertEquals(Expression.NatExpression.class, diff.getExpression().getClass());
		assertEquals(1, diff.getMarkupCount());
		
		// Embed with multiple mark-up
		iterator = TestUtilities.quickScan("func1(arg1) func2 123>");
		parser = new EmbeddingParser(iterator, exceptions);
		
		Embed.ExpressionEmbed diff2 = (ExpressionEmbed) parser.parseEmbed();
		assertEquals(Expression.NatExpression.class, diff2.getExpression().getClass());
		assertEquals(2, diff2.getMarkupCount());
		assertEquals(Markup.Call.class, diff2.getMarkup(0).getClass());
		
		// Embed Markup* Markup
		iterator = TestUtilities.quickScan("func1(arg1) func2 func3>");
		parser = new EmbeddingParser(iterator, exceptions);
		Embed.MarkupEmbed markupemb = (MarkupEmbed) parser.parseEmbed();
		assertEquals(2, markupemb.getMarkupCount());
		assertEquals(Markup.Call.class, markupemb.getMarkup(0).getClass());
	}
	
	@Test
	public void testPreText() throws SyntaxException {
		iterator = TestUtilities.quickScan("\"left<");
		parser = new EmbeddingParser(iterator, exceptions);
		
		PreText text = parser.parsePreText();
		assertEquals("left", text.getText().toString());
	}
	
	@Test
	public void testTextTail() throws SyntaxException {
		iterator = TestUtilities.quickScan(">right\"");
		parser = new EmbeddingParser(iterator, exceptions);
		
		TextTail.PostTail post = (PostTail) parser.parseTextTail();
		assertEquals("right", post.getPost().getText().toString());
		
		iterator = TestUtilities.quickScan(">mid<123>\"");
		parser = new EmbeddingParser(iterator, exceptions);
		
		TextTail.MidTail mid = (MidTail) parser.parseTextTail();
		assertEquals("mid", mid.getMid().getText().toString());
		assertEquals(TextTail.PostTail.class, mid.getTail().getClass());
	}
	
	@Test
	public void testPostTest() throws SyntaxException {
		iterator = TestUtilities.quickScan(">right\"");
		parser = new EmbeddingParser(iterator, exceptions);
		
		PostText text = parser.parsePostText();
		assertTrue(exceptions.size() == 0);
		assertEquals("right", text.getText().toString());
	}
	
	@Test
	public void testMidText() throws SyntaxException {
		iterator = TestUtilities.quickScan(">mid<");
		parser = new EmbeddingParser(iterator, exceptions);
		
		MidText text = parser.parseMidText();
		assertTrue(exceptions.size() == 0);
		assertEquals("mid", text.getText().toString());
	}
	
	@Test
	public void testTextChars() {
		iterator = TestUtilities.quickScan("left<");
		parser = new EmbeddingParser(iterator, exceptions);
		
		StringLiteral text = parser.parseTextChars();
		assertTrue(exceptions.size() == 0);
		assertEquals("left", text.toString());
	}
	
	@Test
	public void testCaveat() {
		// Mark-up
		parser = new EmbeddingParser(TestUtilities.quickScan("p;"), exceptions);
		assertFalse(parser.isMarkup(1));
		
		// Mark-up, variable
		parser = new EmbeddingParser(TestUtilities.quickScan("p p;"), exceptions);
		assertTrue(parser.isMarkup(1));
		assertFalse(parser.isMarkup(2));
		
		// Mark-up, mark-up
		parser = new EmbeddingParser(TestUtilities.quickScan("p p();"), exceptions);
		assertTrue(parser.isMarkup(1));
		assertTrue(parser.isMarkup(2));
		
		// Markup, mark-up, natural
		parser = new EmbeddingParser(TestUtilities.quickScan("p p 123;"), exceptions);
		assertTrue(parser.isMarkup(1));
		assertTrue(parser.isMarkup(2));
		assertFalse(parser.isMarkup(3));
	}

}