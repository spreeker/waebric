package org.cwi.waebric.interpreter;

import static org.junit.Assert.assertEquals;

import org.jdom.Document;
import org.junit.Before;
import org.junit.Test;

/**
 * Random boring get-set tests
 * @author Jeroen van Schagen
 * 19-06-2009
 */
public class TestJDOMVisitor {
	
	private Document document;
	private JDOMVisitor visitor;

	@Before
	public void setUp() {
		document = new Document();
		visitor = new JDOMVisitor(document);
	}

	@Test
	public void testGetDocument() {
		assertEquals(document, visitor.getDocument());
	}

}