package org.cwi.waebric.interpreter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.expression.KeyValuePair;
import org.cwi.waebric.parser.ast.expression.Text;
import org.cwi.waebric.parser.ast.statement.Statement;
import org.cwi.waebric.parser.ast.statement.predicate.Predicate;
import org.cwi.waebric.parser.ast.statement.predicate.Type;
import org.jdom.Document;
import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;

public class TestStatements {
	
	private JDOMVisitor visitor;
	private Document document;

	@Before
	public void setUp() {
		this.document = new Document();
		this.visitor = new JDOMVisitor(document);
	}
	
	@Test
	public void testIf() { // Program: if(var) echo var;
		Expression text = new Expression.TextExpression("success");
		
		// Create a true predicate
		Expression.VarExpression var = new Expression.VarExpression(new IdCon("var"));
		visitor.addVariable("var", text);
		Predicate truePredicate = new Predicate.RegularPredicate(var);

		// Create sub-statement
		Statement.Echo successEcho = new Statement.Echo();
		successEcho.setExpression(text);
		
		// Create if statement
		Statement.If ifStm = new Statement.If();
		ifStm.setPredicate(truePredicate);
		ifStm.setTrueStatement(successEcho);
		
		Element placeholder = new Element("placeholder");
		visitor.setCurrent(placeholder);
		ifStm.accept(visitor);
		
		assertEquals("success", placeholder.getText());
	}
	
	@Test
	public void testIfElse() {
		// Create a true and false predicate
		Expression.VarExpression var = new Expression.VarExpression(new IdCon("var"));
		visitor.addVariable("var", new Expression.ListExpression());
		Predicate truePredicate = new Predicate.RegularPredicate(var);
		Predicate falsePredicate = new Predicate.Not(truePredicate);

		// Create sub-statement
		Statement.Echo successEcho = new Statement.Echo(new Expression.TextExpression("success"));
		Statement.Echo failEcho = new Statement.Echo(new Expression.TextExpression("fail"));
		
		// Create true and false if-else statement
		Statement.IfElse ifFlow = new Statement.IfElse(truePredicate, successEcho, failEcho);
		Statement.IfElse elseFlow = new Statement.IfElse(falsePredicate, successEcho, failEcho);
		
		Element placeholder = new Element("placeholder");
		visitor.setCurrent(placeholder);
		ifFlow.accept(visitor);
		elseFlow.accept(visitor);
		
		assertEquals("successfail", placeholder.getText());
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
	
	@Test
	public void testEvaluatePredicate() {
		// Is-string predicate
		Predicate.Is validString = new Predicate.Is();
		validString.setExpression(new Expression.TextExpression("test"));
		validString.setType(new Type.StringType());
		assertTrue(visitor.evaluatePredicate(validString));
		
		Predicate.Is invalidString = new Predicate.Is();
		invalidString.setExpression(new Expression.NatExpression(1337));
		invalidString.setType(new Type.StringType());
		assertFalse(visitor.evaluatePredicate(invalidString));
		
		// Is-list predicate
		Predicate.Is validList = new Predicate.Is();
		validList.setExpression(new Expression.ListExpression());
		validList.setType(new Type.ListType());
		assertTrue(visitor.evaluatePredicate(validList));
		
		Predicate.Is invalidList = new Predicate.Is();
		invalidList.setExpression(new Expression.NatExpression(1337));
		invalidList.setType(new Type.ListType());
		assertFalse(visitor.evaluatePredicate(invalidList));
	
		// Is-record predicate
		Predicate.Is validRecord = new Predicate.Is();
		validRecord.setExpression(new Expression.RecordExpression());
		validRecord.setType(new Type.RecordType());
		assertTrue(visitor.evaluatePredicate(validRecord));
		
		Predicate.Is invalidRecord = new Predicate.Is();
		invalidRecord.setExpression(new Expression.NatExpression(1337));
		invalidRecord.setType(new Type.RecordType());
		assertFalse(visitor.evaluatePredicate(invalidRecord));
		
		// Field predicate, check if record element can be found
		Expression.RecordExpression recordExpr = new Expression.RecordExpression();
		KeyValuePair e = new KeyValuePair();
		e.setIdentifier(new IdCon("valid"));
		e.setExpression(new Expression.TextExpression("succes"));
		recordExpr.addKeyValuePair(e);
		
		Predicate.RegularPredicate validField = new Predicate.RegularPredicate();
		validField.setExpression(new Expression.Field(recordExpr, new IdCon("valid")));
		assertTrue(visitor.evaluatePredicate(validField));
		
		Predicate.RegularPredicate invalidField = new Predicate.RegularPredicate();
		invalidField.setExpression(new Expression.Field(recordExpr, new IdCon("invalid")));
		assertFalse(visitor.evaluatePredicate(invalidField));
		
		// Variable predicate, check if variable is defined
		visitor.addVariable("valid", new Expression.TextExpression("success"));
		Predicate.RegularPredicate validVar = new Predicate.RegularPredicate();
		validVar.setExpression(new Expression.VarExpression(new IdCon("valid")));
		assertTrue(visitor.evaluatePredicate(validVar));
		
		Predicate.RegularPredicate invalidVar = new Predicate.RegularPredicate();
		invalidVar.setExpression(new Expression.VarExpression(new IdCon("invalid")));
		assertFalse(visitor.evaluatePredicate(invalidVar));

		// And predicate
		assertTrue(visitor.evaluatePredicate(new Predicate.And(validString, validString))); // true && true = true
		assertFalse(visitor.evaluatePredicate(new Predicate.And(validString, invalidString))); // true && false = false
		assertFalse(visitor.evaluatePredicate(new Predicate.And(invalidString, validString))); // false && true = false
		assertFalse(visitor.evaluatePredicate(new Predicate.And(invalidString, invalidString))); // false && false = false
		
		// Or predicate
		assertTrue(visitor.evaluatePredicate(new Predicate.Or(validString, validString))); // true || true = true
		assertTrue(visitor.evaluatePredicate(new Predicate.Or(validString, invalidString))); // true || false = true
		assertTrue(visitor.evaluatePredicate(new Predicate.Or(invalidString, validString))); // false || true = true
		assertFalse(visitor.evaluatePredicate(new Predicate.Or(invalidString, invalidString))); // false || false = false
		
		// Not predicate
		assertTrue(visitor.evaluatePredicate(new Predicate.Not(invalidString))); // ! false = true
		assertFalse(visitor.evaluatePredicate(new Predicate.Not(validString))); // ! true = false
	}
	
}