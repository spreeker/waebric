package org.cwi.waebric.parser;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.expression.Var;
import org.cwi.waebric.parser.ast.markup.Argument;
import org.cwi.waebric.parser.ast.markup.Arguments;
import org.cwi.waebric.parser.ast.markup.Attribute;
import org.cwi.waebric.parser.ast.markup.Attributes;
import org.cwi.waebric.parser.ast.markup.Designator;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.ast.markup.Argument.ArgumentWithVar;
import org.cwi.waebric.parser.ast.markup.Argument.ArgumentWithoutVar;
import org.cwi.waebric.parser.ast.markup.Attribute.AttributeDoubleNatCon;
import org.cwi.waebric.parser.ast.markup.Attribute.AttributeIdCon;
import org.cwi.waebric.parser.ast.markup.Attribute.AttributeNatCon;
import org.cwi.waebric.parser.ast.markup.Markup.MarkupWithArguments;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.TestScanner;
import org.cwi.waebric.scanner.token.WaebricTokenIterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestMarkupParser {

	private MarkupParser parser;
	
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
	public void testMarkup() {
		// Regular mark-up
		iterator = TestScanner.quickScan("myfunction @99,#myattribute,@99%12");
		parser = new MarkupParser(iterator, exceptions);
		
		Markup markup = parser.parseMarkup();
		assertEquals(0, exceptions.size());
		assertNotNull(markup.getDesignator());
		
		// Mark-up with arguments
		iterator = TestScanner.quickScan("myfunction @99,#myattribute,@99%12 (12,\"text!\")");
		parser = new MarkupParser(iterator, exceptions);
		
		Markup.MarkupWithArguments markupa = (MarkupWithArguments) parser.parseMarkup();
		assertEquals(0, exceptions.size());
		assertNotNull(markupa.getDesignator());
		assertEquals(2, markupa.getArguments().size());
	}
	
	@Test
	public void testDesignator() {
		iterator = TestScanner.quickScan("myfunction @99,#myattribute,@99%12");
		parser = new MarkupParser(iterator, exceptions);
		
		Designator designator = parser.parseDesignator();
		assertEquals(0, exceptions.size());
		assertEquals("myfunction", designator.getIdentifier().getLiteral().toString());
		assertEquals(3, designator.getAttributes().size());
	}
	
	@Test
	public void testAttributes() {
		iterator = TestScanner.quickScan("@99,#myattribute,@99%12");
		parser = new MarkupParser(iterator, exceptions);
		
		Attributes attributes = parser.parseAttributes();
		assertEquals(0, exceptions.size());
		assertEquals(3, attributes.size());
		assertEquals(Attribute.AttributeNatCon.class, attributes.get(0).getClass());
		assertEquals(Attribute.AttributeIdCon.class, attributes.get(1).getClass());
		assertEquals(Attribute.AttributeDoubleNatCon.class, attributes.get(2).getClass());
	}
	
	@Test
	public void testAttribute() {
		// Identifier attribute
		iterator = TestScanner.quickScan("#myattribute");
		parser = new MarkupParser(iterator, exceptions);
		
		Attribute.AttributeIdCon attributei = (AttributeIdCon) parser.parseAttribute();
		assertEquals(0, exceptions.size());
		assertEquals("myattribute", attributei.getIdentifier().getLiteral().toString());
		
		// Regular natural attribute
		iterator = TestScanner.quickScan("@99");
		parser = new MarkupParser(iterator, exceptions);
		
		Attribute.AttributeNatCon attributen = (AttributeNatCon) parser.parseAttribute();
		assertEquals(0, exceptions.size());
		assertEquals(99, attributen.getNumber().getLiteral().toInteger());
		
		// Double natural attribute
		iterator = TestScanner.quickScan("@99%12");
		parser = new MarkupParser(iterator, exceptions);
		
		Attribute.AttributeDoubleNatCon attributedn = (AttributeDoubleNatCon) parser.parseAttribute();
		assertEquals(0, exceptions.size());
		assertEquals(99, attributedn.getNumber().getLiteral().toInteger());
		assertEquals(12, attributedn.getSecondNumber().getLiteral().toInteger());
	}
	
	@Test
	public void testArguments() {
		iterator = TestScanner.quickScan("(var1=argument1,argument2)");
		parser = new MarkupParser(iterator, exceptions);
		
		Arguments arguments = parser.parseArguments();
		assertEquals(0, exceptions.size());
		assertEquals(Argument.ArgumentWithVar.class, arguments.get(0).getClass());
		assertEquals(Argument.ArgumentWithoutVar.class, arguments.get(1).getClass());
	}
	
	@Test
	public void testArgument() {
		// Variable argument
		iterator = TestScanner.quickScan("var1=12");
		parser = new MarkupParser(iterator, exceptions);
		
		Argument.ArgumentWithVar argumentv = (ArgumentWithVar) parser.parseArgument();
		assertEquals(0, exceptions.size());
		assertEquals("var1", argumentv.getVar().getIdentifier().getLiteral().toString());
		assertEquals(Expression.NatExpression.class, argumentv.getExpression().getClass());
		
		// Plain argument
		iterator = TestScanner.quickScan("12");
		parser = new MarkupParser(iterator, exceptions);
		
		Argument.ArgumentWithoutVar argument = (ArgumentWithoutVar) parser.parseArgument();
		assertEquals(0, exceptions.size());
		assertEquals(Expression.NatExpression.class, argument.getExpression().getClass());
	}
	
	@Test
	public void testVar() {
		iterator = TestScanner.quickScan("var1");
		parser = new MarkupParser(iterator, exceptions);
		
		Var var = parser.parseVar("", "");
		assertEquals(0, exceptions.size());
		assertEquals("var1", var.getIdentifier().getLiteral().toString());
	}

}