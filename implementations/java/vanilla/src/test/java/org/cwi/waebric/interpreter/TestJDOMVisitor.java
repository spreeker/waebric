package org.cwi.waebric.interpreter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.expression.Text;
import org.cwi.waebric.parser.ast.markup.Argument;
import org.cwi.waebric.parser.ast.markup.Arguments;
import org.cwi.waebric.parser.ast.markup.Designator;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.ast.module.function.Formals;
import org.cwi.waebric.parser.ast.module.function.FunctionDef;
import org.jdom.Document;
import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;

public class TestJDOMVisitor {
	
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
		Markup.Tag markup = new Markup.Tag();
		markup.setDesignator(new Designator(new IdCon("head")));
		visitor.setCurrent(parent);
		visitor.visit(markup);
		assertEquals("head", visitor.getCurrent().getName());
		assertTrue(parent.getContent().contains(visitor.getCurrent()));
	}
	
	@Test
	public void testCall() {
		FunctionDef function = new FunctionDef();
		function.setIdentifier(new IdCon("callme"));
		
		Formals.RegularFormal formals = new Formals.RegularFormal();
		formals.addIdentifier(new IdCon("var1"));
		formals.addIdentifier(new IdCon("var2"));
		formals.addIdentifier(new IdCon("var3"));
		
		function.setFormals(formals);
		visitor.addFunctionDef("callme", function);

		Arguments arguments = new Arguments();
		Argument arg1 = new Argument.RegularArgument();
		Expression expr1 = new Expression.TextExpression(new Text("one"));
		arg1.setExpression(expr1); arguments.add(arg1);
		Argument arg2 = new Argument.RegularArgument();
		Expression expr2 = new Expression.TextExpression(new Text("two"));
		arg2.setExpression(expr2); arguments.add(arg2);
		Argument arg3 = new Argument.RegularArgument();
		Expression expr3 = new Expression.TextExpression(new Text("three"));
		arg3.setExpression(expr3); arguments.add(arg3);

		Markup.Call call = new Markup.Call(arguments);
		call.setDesignator(new Designator(new IdCon("callme")));
		
		visitor.visit(call);
		
		assertEquals(expr1, visitor.getVariable("var1"));
		assertEquals(expr2, visitor.getVariable("var2"));
		assertEquals(expr3, visitor.getVariable("var3"));
	}

}