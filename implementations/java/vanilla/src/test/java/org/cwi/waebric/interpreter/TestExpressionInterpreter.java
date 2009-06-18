package org.cwi.waebric.interpreter;

import org.jdom.Document;
import org.junit.Before;

public class TestExpressionInterpreter {
	
	private JDOMVisitor visitor;
	private Document document;

	@Before
	public void setUp() {
		this.document = new Document();
		this.visitor = new JDOMVisitor(document);
	}
	
}