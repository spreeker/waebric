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
import org.cwi.waebric.parser.ast.statement.Statement;
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
	public void testEach() {
		Statement.Each each = new Statement.Each();
		
		// List expression on which will be iterated
		Expression.ListExpression list = new Expression.ListExpression();
		list.addExpression(new Expression.TextExpression(new Text("test ")));
		list.addExpression(new Expression.TextExpression(new Text("has ")));
		list.addExpression(new Expression.TextExpression(new Text("succeeded")));
		each.setExpression(list);
		
		// Variable which is constructed per element
		IdCon var = new IdCon("myvar");
		each.setVar(var);
		
		// Statement which is executed for each element
		Statement.Echo echo = new Statement.Echo();
		echo.setExpression(new Expression.VarExpression(var));
		each.setStatement(echo);
		
		// Set root element
		Element current = new Element("test");
		visitor.setCurrent(current);
		
		visitor.visit(each); // Execute visit
		
		assertEquals("test has succeeded", current.getText());
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