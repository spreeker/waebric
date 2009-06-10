package org.cwi.waebric.checker;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.TestUtilities;
import org.cwi.waebric.parser.ast.module.Modules;
import org.junit.After;
import org.junit.Test;

public class TestVarCheck {
	
	private VarCheck check;
	private List<SemanticException> exceptions;
	
	public TestVarCheck() {
		exceptions = new ArrayList<SemanticException>();
		check = new VarCheck();
	}
	
	@After
	public void tearDown() {
		exceptions.clear();
	}
	
	@Test
	public void testVarCheck() throws FileNotFoundException {
		Modules modules = TestUtilities.quickParse("src/test/waebric/var/correctvar.wae");
		check.checkAST(modules, exceptions); // Perform variable check
		assertEquals(0, exceptions.size()); // No faults
	}
	
	@Test
	public void testUndefinedVar() throws FileNotFoundException {
		Modules modules = TestUtilities.quickParse("src/test/waebric/var/undefinedvar.wae");
		check.checkAST(modules, exceptions); // Perform variable check
		assertEquals(2, exceptions.size());
		assertEquals(VarCheck.UndefinedVariableException.class, exceptions.get(0).getClass());
		assertEquals(VarCheck.UndefinedVariableException.class, exceptions.get(1).getClass());
	}
	
}