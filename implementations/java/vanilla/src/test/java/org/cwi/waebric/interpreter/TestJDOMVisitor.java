package org.cwi.waebric.interpreter;

import static org.junit.Assert.assertEquals;

import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.module.function.FunctionDef;
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

	@Test
	public void testGetAddFunction() {
		assertEquals(null, visitor.getFunction("myfunc"));
		FunctionDef myfunc = new FunctionDef();
		myfunc.setIdentifier(new IdCon("myfunc"));
		visitor.addFunctionDef(myfunc);
		assertEquals(myfunc, visitor.getFunction("myfunc"));
	}
	
	@Test
	public void testGetAddVar() {
		assertEquals(null, visitor.getVariable("myvar"));
		Expression myvar = new Expression.TextExpression("test");
		visitor.addVariable("myvar", myvar);
		assertEquals(myvar, visitor.getVariable("myvar"));
	}

}