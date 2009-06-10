package org.cwi.waebric.checker;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.TestUtilities;
import org.cwi.waebric.parser.ast.module.Modules;
import org.cwi.waebric.parser.ast.module.function.FunctionDef;
import org.junit.After;
import org.junit.Test;

public class TestFunctionCheck {

	private FunctionCheck check;
	private WaebricChecker checker;
	private List<SemanticException> exceptions;
	
	public TestFunctionCheck() {
		exceptions = new ArrayList<SemanticException>();
		checker = new WaebricChecker();
		check = new FunctionCheck(checker);
	}
	
	@After
	public void tearDown() {
		exceptions.clear();
	}
	
	@Test
	public void testGetFunctionDefinition() throws FileNotFoundException {
		Modules modules = TestUtilities.quickParse("src/test/waebric/mod/mymodule.wae");
		new ModuleCheck(checker).checkAST(modules, exceptions); // Cache related modules
		List<FunctionDef> defs = check.getFunctionDefinitions(modules.get(0), exceptions);
		assertEquals(0, exceptions.size()); // No faults
		assertEquals(2, defs.size()); // One definition found (test)
		assertEquals("main", defs.get(0).getIdentifier().getLiteral().toString());
		assertEquals("test", defs.get(1).getIdentifier().getLiteral().toString());
	}
	
	@Test
	public void testDuplicateFunction() throws FileNotFoundException {
		Modules modules = TestUtilities.quickParse("src/test/waebric/func/duplicate.wae");
		new ModuleCheck(checker).checkAST(modules, exceptions); // Cache related modules
		check.checkAST(modules, exceptions); // Perform function check
		assertEquals(2, exceptions.size()); // No faults
		assertEquals(FunctionCheck.DuplicateFunctionDefinition.class, exceptions.get(0).getClass());
		assertEquals(FunctionCheck.DuplicateFunctionDefinition.class, exceptions.get(1).getClass());
	}
	
	@Test
	public void testArityMismatch() {
		
	}
	
	@Test
	public void testUndefinedFunction() {
		
	}
	
}
