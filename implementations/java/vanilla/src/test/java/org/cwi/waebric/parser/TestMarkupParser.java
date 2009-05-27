package org.cwi.waebric.parser;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.expressions.Expression;
import org.cwi.waebric.parser.ast.expressions.Var;
import org.cwi.waebric.parser.ast.markup.Argument;
import org.cwi.waebric.parser.ast.markup.Arguments;
import org.cwi.waebric.parser.ast.markup.Attribute;
import org.cwi.waebric.parser.ast.markup.Attributes;
import org.cwi.waebric.parser.ast.markup.Designator;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.ast.markup.Attribute.AttributeDoubleNatCon;
import org.cwi.waebric.parser.ast.markup.Attribute.AttributeIdCon;
import org.cwi.waebric.parser.ast.markup.Attribute.AttributeNatCon;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.TestScanner;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestMarkupParser {

	private MarkupParser parser;
	
	private List<ParserException> exceptions;
	private TokenIterator iterator;
	
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
		// Mark up with arguments
		iterator = TestScanner.quickScan("myfunction @99,#myattribute,@99%12 (12,\"text!\")");
		parser = new MarkupParser(iterator, exceptions);
		
		Markup.MarkupWithArguments markupa = new Markup.MarkupWithArguments();
		parser.parse(markupa);
		
		assertEquals(0, exceptions.size());
		assertNotNull(markupa.getDesignator());
		assertEquals(2, markupa.getArguments().getElementCount());
		
		// Mark up without arguments
		iterator = TestScanner.quickScan("myfunction @99,#myattribute,@99%12");
		parser = new MarkupParser(iterator, exceptions);
		
		Markup.MarkupWithoutArguments markup = new Markup.MarkupWithoutArguments();
		parser.parse(markup);
		
		assertEquals(0, exceptions.size());
		assertNotNull(markup.getDesignator());
	}
	
	@Test
	public void testDesignator() {
		iterator = TestScanner.quickScan("myfunction @99,#myattribute,@99%12");
		parser = new MarkupParser(iterator, exceptions);
		
		Designator designator = new Designator();
		parser.parse(designator);
		
		// Assertions
		assertEquals(0, exceptions.size());
		assertEquals("myfunction", designator.getIdentifier().toString());
		assertEquals(3, designator.getAttributes().size());
	}
	
	@Test
	public void testAttributes() {
		iterator = TestScanner.quickScan("@99,#myattribute,@99%12");
		parser = new MarkupParser(iterator, exceptions);
		
		Attributes attributes = new Attributes();
		parser.parse(attributes);
		
		// Assertions
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
		
		AttributeIdCon attributei = new Attribute.AttributeIdCon('#');
		parser.parse(attributei);
		
		assertEquals(0, exceptions.size());
		assertEquals("myattribute", attributei.getIdentifier().toString());
		
		// Regular natural attribute
		iterator = TestScanner.quickScan("@99");
		parser = new MarkupParser(iterator, exceptions);
		
		AttributeNatCon attributen = new Attribute.AttributeNatCon();
		parser.parse(attributen);
		
		assertEquals(0, exceptions.size());
		assertEquals(99, attributen.getNumber().getIdentifier().getLiteral());
		
		// Double natural attribute
		iterator = TestScanner.quickScan("@99%12");
		parser = new MarkupParser(iterator, exceptions);
		
		AttributeDoubleNatCon attributedn = new Attribute.AttributeDoubleNatCon();
		parser.parse(attributedn);
		
		assertEquals(0, exceptions.size());
		assertEquals(99, attributedn.getNumber().getIdentifier().getLiteral());
		assertEquals(12, attributedn.getSecondNumber().getIdentifier().getLiteral());
	}
	
	@Test
	public void testArguments() {
		iterator = TestScanner.quickScan("(var1=argument1,argument2)");
		parser = new MarkupParser(iterator, exceptions);
		
		Arguments arguments = new Arguments();
		parser.parse(arguments);
		
		assertEquals(0, exceptions.size());
		assertEquals(Argument.ArgumentWithVar.class, arguments.getElement(0).getClass());
		assertEquals(Argument.ArgumentWithoutVar.class, arguments.getElement(1).getClass());
	}
	
	@Test
	public void testArgument() {
		// Variable argument
		iterator = TestScanner.quickScan("var1=12");
		parser = new MarkupParser(iterator, exceptions);
		
		Argument.ArgumentWithVar argumentv = new Argument.ArgumentWithVar();
		parser.parse(argumentv);
		
		assertEquals(0, exceptions.size());
		assertEquals("var1", argumentv.getVar().getIdentifier().toString());
		assertEquals(Expression.NatExpression.class, argumentv.getExpression().getClass());
		
		// Plain argument
		iterator = TestScanner.quickScan("12");
		parser = new MarkupParser(iterator, exceptions);
		
		Argument.ArgumentWithoutVar argument = new Argument.ArgumentWithoutVar();
		parser.parse(argument);
		
		assertEquals(0, exceptions.size());
		assertEquals(Expression.NatExpression.class, argument.getExpression().getClass());
	}
	
	@Test
	public void testVar() {
		iterator = TestScanner.quickScan("var1");
		parser = new MarkupParser(iterator, exceptions);
		
		Var var = new Var();
		parser.parse(var);
		
		// Assertions
		assertEquals(0, exceptions.size());
		assertEquals("var1", var.getIdentifier().toString());
	}

}