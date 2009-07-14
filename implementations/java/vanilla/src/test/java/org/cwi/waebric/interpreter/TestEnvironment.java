package org.cwi.waebric.interpreter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.module.function.FunctionDef;
import org.junit.Before;
import org.junit.Test;

public class TestEnvironment {
	
	private Environment env;
	
	@Before
	public void setUp() throws Exception {
		env = new Environment();
	}

	@Test
	public void testStoreGetVariable() {
		Expression expr = new Expression.TextExpression("test");
		env.defineVariable("var", expr);
		assertEquals(expr, env.getVariable("var"));
	}
	
	@Test
	public void testStoreGetFunction() {
		FunctionDef func = new FunctionDef();
		func.setIdentifier(new IdCon("func"));
		env.defineFunction(func);
		assertEquals(func, env.getFunction("func"));
	}
	
	@Test
	public void testStoreGetFunctions() {
		FunctionDef func1 = new FunctionDef();
		func1.setIdentifier(new IdCon("func1"));
		FunctionDef func2 = new FunctionDef();
		func2.setIdentifier(new IdCon("func2"));
		ArrayList<FunctionDef> funcs = new ArrayList<FunctionDef>();
		funcs.add(func1); funcs.add(func2);
		env.defineFunctions(funcs);
		assertEquals(func1, env.getFunction("func1"));
		assertEquals(func2, env.getFunction("func2"));
	}
	
	@Test
	public void testContainsVariable() {
		Expression expr1 = new Expression.TextExpression("expr1");
		env.defineVariable("var1", expr1);
		
		assertTrue(env.isDefinedVariable("var1"));
		assertFalse(env.isDefinedVariable("var2"));
		
		// Create nested environment
		Environment curr = new Environment(env);
		Expression expr2 = new Expression.TextExpression("expr2");
		curr.defineVariable("var2", expr2);
		
		assertTrue(curr.isDefinedVariable("var1"));
		assertTrue(curr.isDefinedVariable("var2"));
	}
	
	@Test
	public void testGetNestedVariable() {
		Expression expr1 = new Expression.TextExpression("expr1");
		env.defineVariable("var1", expr1);
		
		assertEquals(expr1, env.getVariable("var1"));
		
		// Create nested environment
		Environment curr = new Environment(env);
		Expression expr2 = new Expression.TextExpression("expr2");
		curr.defineVariable("var2", expr2);
		Expression expr3 = new Expression.TextExpression("overwritten");
		curr.defineVariable("var1", expr3);
		
		assertEquals(expr3, curr.getVariable("var1"));
		assertEquals(expr2, curr.getVariable("var2"));
	}
	
	@Test
	public void testContainsFunction() {
		FunctionDef func1 = new FunctionDef();
		func1.setIdentifier(new IdCon("func1"));
		env.defineFunction(func1);
		
		assertTrue(env.isDefinedFunction("func1"));
		assertFalse(env.isDefinedFunction("func2"));
		
		// Create nested environment
		Environment curr = new Environment(env);
		FunctionDef func2 = new FunctionDef();
		func2.setIdentifier(new IdCon("func2"));
		curr.defineFunction(func2);
		
		assertTrue(curr.isDefinedFunction("func1"));
		assertTrue(curr.isDefinedFunction("func2"));
	}
	
	@Test
	public void testGetNestedFunction() {
		FunctionDef func1 = new FunctionDef();
		func1.setIdentifier(new IdCon("func1"));
		env.defineFunction(func1);
		
		assertEquals(func1, env.getFunction("func1"));
		
		// Create nested environment
		Environment curr = new Environment(env);
		FunctionDef func2 = new FunctionDef();
		func2.setIdentifier(new IdCon("func2"));
		curr.defineFunction(func2);
		FunctionDef func3 = new FunctionDef();
		func3.setIdentifier(new IdCon("func1")); // Overwrite function
		curr.defineFunction(func3);
		
		assertEquals(func3, curr.getFunction("func1"));
		assertEquals(func2, curr.getFunction("func2"));
		
		assertEquals(func1, env.getFunction("func1"));
	}
	
	@Test
	public void testClone() {
		Environment curr = new Environment(env);
		Environment clone = curr.clone();
		assertEquals(curr.getParent(), clone.getParent());
		assertEquals(curr.getFunctionNames().size(), clone.getFunctionNames().size());
		assertEquals(curr.getVariableNames().size(), clone.getVariableNames().size());
	}

}