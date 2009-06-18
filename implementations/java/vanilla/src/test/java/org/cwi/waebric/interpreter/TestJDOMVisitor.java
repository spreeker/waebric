package org.cwi.waebric.interpreter;

import static org.junit.Assert.assertEquals;

import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.expression.Text;
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
		Element current = new Element("home");
		visitor.setCurrent(current);
		visitor.visit(each); // Execute visit
		
		assertEquals("test has succeeded", current.getText());
	}

}