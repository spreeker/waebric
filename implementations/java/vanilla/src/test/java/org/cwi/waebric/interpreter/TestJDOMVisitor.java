package org.cwi.waebric.interpreter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.markup.Designator;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.jdom.Document;
import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;

public class TestJDOMVisitor {
	
	private final JDOMVisitor visitor;
	private final Document document;
	
	public TestJDOMVisitor() {
		this.document = new Document();
		this.visitor = new JDOMVisitor(document);
	}
	
	@Before
	public void setUp() {}
	
	@Test
	public void testTag() {
		Element parent = new Element("html");
		Markup.Tag markup = new Markup.Tag();
		markup.setDesignator(new Designator(new IdCon("head")));
		visitor.setCurrent(parent);
		visitor.visit(markup);
		assertEquals("head", visitor.getCurrent().getName());
		assertTrue(parent.getContent().contains(visitor.getCurrent()));
	}

}