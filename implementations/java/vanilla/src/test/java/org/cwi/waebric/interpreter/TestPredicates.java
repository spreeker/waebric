package org.cwi.waebric.interpreter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.expression.KeyValuePair;
import org.cwi.waebric.parser.ast.statement.predicate.Predicate;
import org.cwi.waebric.parser.ast.statement.predicate.Type;
import org.jdom.Document;
import org.junit.Before;
import org.junit.Test;

public class TestPredicates {
	
	private PredicateEvaluator visitor;
	private WaebricEvaluator evaluator;

	@Before
	public void setUp() {
		evaluator = new WaebricEvaluator(new Document());
		this.visitor = new PredicateEvaluator(evaluator);
	}
	
	@Test
	public void testIs() {
		// Is-string predicate
		Predicate.Is validString = new Predicate.Is();
		validString.setExpression(new Expression.TextExpression("test"));
		validString.setType(new Type.StringType());
		assertTrue(visitor.visit(validString));
		
		Predicate.Is invalidString = new Predicate.Is();
		invalidString.setExpression(new Expression.NatExpression(1337));
		invalidString.setType(new Type.StringType());
		assertFalse(visitor.visit(invalidString));
		
		// Is-list predicate
		Predicate.Is validList = new Predicate.Is();
		validList.setExpression(new Expression.ListExpression());
		validList.setType(new Type.ListType());
		assertTrue(visitor.visit(validList));
		
		Predicate.Is invalidList = new Predicate.Is();
		invalidList.setExpression(new Expression.NatExpression(1337));
		invalidList.setType(new Type.ListType());
		assertFalse(visitor.visit(invalidList));
	
		// Is-record predicate
		Predicate.Is validRecord = new Predicate.Is();
		validRecord.setExpression(new Expression.RecordExpression());
		validRecord.setType(new Type.RecordType());
		assertTrue(visitor.visit(validRecord));
		
		Predicate.Is invalidRecord = new Predicate.Is();
		invalidRecord.setExpression(new Expression.NatExpression(1337));
		invalidRecord.setType(new Type.RecordType());
		assertFalse(visitor.visit(invalidRecord));
	}
	
	@Test
	public void testRegular() {
		// Field predicate, check if record element can be found
		Expression.RecordExpression recordExpr = new Expression.RecordExpression();
		KeyValuePair e = new KeyValuePair();
		e.setIdentifier(new IdCon("valid"));
		e.setExpression(new Expression.TextExpression("succes"));
		recordExpr.addKeyValuePair(e);
		
		Predicate.RegularPredicate validField = new Predicate.RegularPredicate();
		validField.setExpression(new Expression.Field(recordExpr, new IdCon("valid")));
		assertTrue(visitor.visit(validField));
		
		Predicate.RegularPredicate invalidField = new Predicate.RegularPredicate();
		invalidField.setExpression(new Expression.Field(recordExpr, new IdCon("invalid")));
		assertFalse(visitor.visit(invalidField));
		
		// Variable predicate, check if variable is defined
		evaluator.getEnvironment().defineVariable("valid", new Expression.TextExpression("success"));
		Predicate.RegularPredicate validVar = new Predicate.RegularPredicate();
		validVar.setExpression(new Expression.VarExpression(new IdCon("valid")));
		assertTrue(visitor.visit(validVar));
		
		Predicate.RegularPredicate invalidVar = new Predicate.RegularPredicate();
		invalidVar.setExpression(new Expression.VarExpression(new IdCon("invalid")));
		assertFalse(visitor.visit(invalidVar));
	}
	
	@Test
	public void testNot() {
		Predicate.Is validString = new Predicate.Is();
		validString.setExpression(new Expression.TextExpression("test"));
		validString.setType(new Type.StringType());

		Predicate.Is invalidString = new Predicate.Is();
		invalidString.setExpression(new Expression.NatExpression(1337));
		invalidString.setType(new Type.StringType());
		
		// Not predicate
		assertTrue(visitor.visit(new Predicate.Not(invalidString)));
		assertFalse(visitor.visit(new Predicate.Not(validString)));
	}
	
	@Test
	public void testAnd() {
		Predicate.Is validString = new Predicate.Is();
		validString.setExpression(new Expression.TextExpression("test"));
		validString.setType(new Type.StringType());

		Predicate.Is invalidString = new Predicate.Is();
		invalidString.setExpression(new Expression.NatExpression(1337));
		invalidString.setType(new Type.StringType());
		
		// And predicate
		assertTrue(visitor.visit(new Predicate.And(validString, validString)));
		assertFalse(visitor.visit(new Predicate.And(validString, invalidString)));
		assertFalse(visitor.visit(new Predicate.And(invalidString, validString)));
		assertFalse(visitor.visit(new Predicate.And(invalidString, invalidString)));
	}
	
	@Test
	public void testOr() {
		Predicate.Is validString = new Predicate.Is();
		validString.setExpression(new Expression.TextExpression("test"));
		validString.setType(new Type.StringType());

		Predicate.Is invalidString = new Predicate.Is();
		invalidString.setExpression(new Expression.NatExpression(1337));
		invalidString.setType(new Type.StringType());
		
		// Or predicate
		assertTrue(visitor.visit(new Predicate.Or(validString, validString)));
		assertTrue(visitor.visit(new Predicate.Or(validString, invalidString)));
		assertTrue(visitor.visit(new Predicate.Or(invalidString, validString)));
		assertFalse(visitor.visit(new Predicate.Or(invalidString, invalidString)));
	}

}
