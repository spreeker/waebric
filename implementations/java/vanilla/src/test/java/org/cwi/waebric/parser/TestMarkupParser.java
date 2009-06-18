package org.cwi.waebric.parser;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.markup.Argument;
import org.cwi.waebric.parser.ast.markup.Arguments;
import org.cwi.waebric.parser.ast.markup.Attribute;
import org.cwi.waebric.parser.ast.markup.Attributes;
import org.cwi.waebric.parser.ast.markup.Designator;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.ast.markup.Argument.Attr;
import org.cwi.waebric.parser.ast.markup.Argument.RegularArgument;
import org.cwi.waebric.parser.ast.markup.Markup.Call;
import org.cwi.waebric.TestUtilities;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestMarkupParser {

	private MarkupParser parser;
	
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
	public void testMarkup() throws SyntaxException {
		// Regular mark-up
		iterator = TestUtilities.quickScan("myfunction @99,#myattribute,@99%12");
		parser = new MarkupParser(iterator, exceptions);
		
		Markup markup = parser.parseMarkup();
		assertNotNull(markup.getDesignator());
		
		// Mark-up with arguments
		iterator = TestUtilities.quickScan("myfunction (12,\"text!\")");
		parser = new MarkupParser(iterator, exceptions);
		
		Markup.Call markupa = (Call) parser.parseMarkup();
		assertNotNull(markupa.getDesignator());
		assertEquals(2, markupa.getArguments().size());
		
		iterator = TestUtilities.quickScan("myfunction @99,#myattribute,@99%12 (12,\"text!\")");
		parser = new MarkupParser(iterator, exceptions);
		
		Markup.Call markupaa = (Call) parser.parseMarkup();
		assertNotNull(markupaa.getDesignator());
		assertEquals(2, markupaa.getArguments().size());
		assertEquals(2, markupaa.getArguments().size());
	}
	
	@Test
	public void testDesignator() throws SyntaxException {
		iterator = TestUtilities.quickScan("myfunction @99,#myattribute,@99%12");
		parser = new MarkupParser(iterator, exceptions);
		
		Designator designator = parser.parseDesignator();
		assertEquals("myfunction", designator.getIdentifier().getToken().getLexeme().toString());
		assertEquals(3, designator.getAttributes().size());
	}
	
	@Test
	public void testAttributes() throws SyntaxException {
		iterator = TestUtilities.quickScan("@99,#myattribute,@99%12");
		parser = new MarkupParser(iterator, exceptions);
		
		Attributes attributes = parser.parseAttributes();
		assertEquals(3, attributes.size());
		assertEquals(Attribute.WidthAttribute.class, attributes.get(0).getClass());
		assertEquals(Attribute.IdAttribute.class, attributes.get(1).getClass());
		assertEquals(Attribute.WidthHeightAttribute.class, attributes.get(2).getClass());
	}
	
	@Test
	public void testAttribute() throws SyntaxException {
		// Identifier attribute
		iterator = TestUtilities.quickScan("#myattribute");
		parser = new MarkupParser(iterator, exceptions);
		
		Attribute.IdAttribute attributei = (Attribute.IdAttribute) parser.parseAttribute();
		assertEquals("myattribute", attributei.getIdentifier().getToken().getLexeme().toString());
		
		// Regular natural attribute
		iterator = TestUtilities.quickScan("@99");
		parser = new MarkupParser(iterator, exceptions);
		
		Attribute.WidthAttribute attributen = (Attribute.WidthAttribute) parser.parseAttribute();
		assertEquals(99, attributen.getWidth().getLiteral().toInteger());
		
		// Double natural attribute
		iterator = TestUtilities.quickScan("@99%12");
		parser = new MarkupParser(iterator, exceptions);
		
		Attribute.WidthHeightAttribute attributedn = (Attribute.WidthHeightAttribute) parser.parseAttribute();
		assertEquals(99, attributedn.getWidth().getLiteral().toInteger());
		assertEquals(12, attributedn.getHeight().getLiteral().toInteger());
	}
	
	@Test
	public void testArguments() throws SyntaxException {
		iterator = TestUtilities.quickScan("(var1=argument1,argument2)");
		parser = new MarkupParser(iterator, exceptions);
		
		Arguments arguments = parser.parseArguments();
		assertEquals(Argument.Attr.class, arguments.get(0).getClass());
		assertEquals(Argument.RegularArgument.class, arguments.get(1).getClass());
	}
	
	@Test
	public void testArgument() throws SyntaxException {
		// Variable argument
		iterator = TestUtilities.quickScan("var1=12");
		parser = new MarkupParser(iterator, exceptions);
		
		Argument.Attr argumentv = (Attr) parser.parseArgument();
		assertEquals("var1", argumentv.getIdentifier().getToken().getLexeme().toString());
		assertEquals(Expression.NatExpression.class, argumentv.getExpression().getClass());
		
		// Plain argument
		iterator = TestUtilities.quickScan("12");
		parser = new MarkupParser(iterator, exceptions);
		
		Argument.RegularArgument argument = (RegularArgument) parser.parseArgument();
		assertEquals(Expression.NatExpression.class, argument.getExpression().getClass());
	}

}