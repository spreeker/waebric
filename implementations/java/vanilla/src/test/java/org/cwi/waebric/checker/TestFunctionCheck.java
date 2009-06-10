package org.cwi.waebric.checker;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.TestUtilities;
import org.cwi.waebric.parser.ast.AbstractSyntaxTree;
import org.cwi.waebric.parser.ast.module.function.FunctionDef;
import org.junit.After;
import org.junit.Test;

public class TestFunctionCheck {

	private List<SemanticException> exceptions;
	private FunctionCheck check;
	
	public TestFunctionCheck() {
		exceptions = new ArrayList<SemanticException>();
		check = new FunctionCheck();
	}
	
	@After
	public void tearDown() {
		exceptions.clear();
	}
	
	@Test
	public void testGetFunctionDefinition() throws FileNotFoundException {
		AbstractSyntaxTree ast = TestUtilities.quickParse("src/test/waebric/mod/mymodule.wae");
		new ModuleCheck().checkAST(ast, exceptions); // Cache related modules
		List<FunctionDef> defs = check.getFunctionDefinitions(ast.getRoot().get(0), exceptions);
		assertEquals(0, exceptions.size()); // No faults
		assertEquals(2, defs.size()); // One definition found (test)
		assertEquals("main", defs.get(0).getIdentifier().getLiteral().toString());
		assertEquals("test", defs.get(1).getIdentifier().getLiteral().toString());
	}
	
	@Test
	public void testDuplicateFunction() throws FileNotFoundException {
		AbstractSyntaxTree ast = TestUtilities.quickParse("src/test/waebric/func/duplicate.wae");
		check.checkAST(ast, exceptions); // Perform function check
		assertEquals(2, exceptions.size());
		assertEquals(FunctionCheck.DuplicateFunctionDefinition.class, exceptions.get(0).getClass());
		assertEquals(FunctionCheck.DuplicateFunctionDefinition.class, exceptions.get(1).getClass());
	}
	
	@Test
	public void testArityMismatch() throws FileNotFoundException {
		AbstractSyntaxTree ast = TestUtilities.quickParse("src/test/waebric/func/aritymm.wae");
		check.checkAST(ast, exceptions); // Perform function check
		assertEquals(2, exceptions.size());
		assertEquals(FunctionCheck.ArityMismatchException.class, exceptions.get(0).getClass());
		assertEquals(FunctionCheck.ArityMismatchException.class, exceptions.get(1).getClass());
	}
	
	@Test
	public void testUndefinedFunction() throws FileNotFoundException {
		AbstractSyntaxTree ast = TestUtilities.quickParse("src/test/waebric/func/undeffunc.wae");
		check.checkAST(ast, exceptions); // Perform function check
		assertEquals(2, exceptions.size());
		assertEquals(FunctionCheck.UndefinedFunctionException.class, exceptions.get(0).getClass());
		assertEquals(FunctionCheck.UndefinedFunctionException.class, exceptions.get(1).getClass());
	}
	
}
