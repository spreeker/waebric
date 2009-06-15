package org.cwi.waebric.interpreter;

import static org.junit.Assert.assertTrue;

import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.markup.Designator;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;

public class TestJDOMVisitor {
	
	private final JDOMVisitor visitor;
	
	public TestJDOMVisitor() {
		this.visitor = new JDOMVisitor();
	}
	
	@Before
	public void setUp() {}
	
	@Test
	public void testTag() {
		Element parent = new Element("html");
		Markup.Tag markup = new Markup.Tag();
		markup.setDesignator(new Designator(new IdCon("head")));
		Object[] args = new Object[] { parent };
		visitor.visit(markup, args);
		assertTrue(parent.getContent().contains(args[0]));
	}

}