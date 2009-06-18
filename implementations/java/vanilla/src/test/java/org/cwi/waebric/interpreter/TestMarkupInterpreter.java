package org.cwi.waebric.interpreter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.basic.NatCon;
import org.cwi.waebric.parser.ast.markup.Attribute;
import org.cwi.waebric.parser.ast.markup.Attributes;
import org.cwi.waebric.parser.ast.markup.Designator;
import org.cwi.waebric.parser.ast.markup.Markup;

import org.jdom.Document;
import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;

public class TestMarkupInterpreter {
	
	private JDOMVisitor visitor;
	private Document document;

	@Before
	public void setUp() {
		this.document = new Document();
		this.visitor = new JDOMVisitor(document);
	}

	@Test
	public void testTag() {
		Element parent = new Element("html");
		visitor.setCurrent(parent);
		
		Markup.Tag markup = new Markup.Tag();
		markup.setDesignator(new Designator(new IdCon("head")));
		visitor.visit(markup);
		
		assertEquals("head", visitor.getCurrent().getName());
		assertTrue(parent.getContent().contains(visitor.getCurrent()));
	}
	
	@Test
	public void testAttributes() {
		Element test = new Element("test");
		visitor.setCurrent(test);
		
		Attributes attributes = new Attributes();
		Attribute.ClassAttribute catt = new Attribute.ClassAttribute();
		catt.setIdentifier(new IdCon("myclass"));
		attributes.add(catt);
		Attribute.IdAttribute iatt = new Attribute.IdAttribute();
		iatt.setIdentifier(new IdCon("myid"));
		attributes.add(iatt);
		Attribute.TypeAttribute tatt = new Attribute.TypeAttribute();
		tatt.setIdentifier(new IdCon("mytype"));
		attributes.add(tatt);
		Attribute.NameAttribute natt = new Attribute.NameAttribute();
		natt.setIdentifier(new IdCon("myname"));
		attributes.add(natt);
		Attribute.WidthAttribute watt = new Attribute.WidthAttribute();
		watt.setWidth(new NatCon(100));
		attributes.add(watt);
		Attribute.WidthHeightAttribute whatt = new Attribute.WidthHeightAttribute();
		whatt.setWidth(new NatCon(10));
		whatt.setHeight(new NatCon(99));
		attributes.add(whatt);
		
		visitor.visit(attributes);
		assertEquals("myclass", test.getAttribute("class").getValue());
		assertEquals("myid", test.getAttribute("id").getValue());
		assertEquals("mytype", test.getAttribute("type").getValue());
		assertEquals("myname", test.getAttribute("name").getValue());
		assertEquals("10", test.getAttribute("width").getValue());
		assertEquals("99", test.getAttribute("height").getValue());
	}
	
}