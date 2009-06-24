package org.cwi.waebric.checker;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.TestUtilities;
import org.cwi.waebric.parser.ast.AbstractSyntaxTree;
import org.junit.After;
import org.junit.Test;

public class TestDeclarationChecker {
	
	private List<SemanticException> exceptions;
	private DeclarationChecker checker;
	
	public TestDeclarationChecker() {
		exceptions = new ArrayList<SemanticException>();
		checker = new DeclarationChecker(exceptions);
	}
	
	@After
	public void tearDown() {
		exceptions.clear();
	}
	
	@Test
	public void testDuplicateFunction() throws IOException {
		AbstractSyntaxTree ast = TestUtilities.quickParse("src/test/waebric/func/duplicate.wae");
		checker.visit(ast.getRoot()); // Check modules
		assertEquals(2, exceptions.size());
		assertEquals(DeclarationChecker.DuplicateFunctionDefinition.class, exceptions.get(0).getClass());
		assertEquals(DeclarationChecker.DuplicateFunctionDefinition.class, exceptions.get(1).getClass());
	}
	
	@Test
	public void testArityMismatch() throws IOException {
		AbstractSyntaxTree ast = TestUtilities.quickParse("src/test/waebric/func/aritymm.wae");
		checker.visit(ast.getRoot()); // Check modules
		assertEquals(2, exceptions.size());
		assertEquals(DeclarationChecker.ArityMismatchException.class, exceptions.get(0).getClass());
		assertEquals(DeclarationChecker.ArityMismatchException.class, exceptions.get(1).getClass());
	}
	
	@Test
	public void testUndefinedFunction() throws IOException {
		AbstractSyntaxTree ast = TestUtilities.quickParse("src/test/waebric/func/undeffunc.wae");
		checker.visit(ast.getRoot()); // Check modules
		assertEquals(3, exceptions.size());
		assertEquals(DeclarationChecker.UndefinedFunctionException.class, exceptions.get(0).getClass());
		assertEquals(DeclarationChecker.UndefinedFunctionException.class, exceptions.get(1).getClass());
		assertEquals(DeclarationChecker.UndefinedFunctionException.class, exceptions.get(2).getClass());
	}
	
	@Test
	public void testVarCheck() throws IOException {
		AbstractSyntaxTree ast = TestUtilities.quickParse("src/test/waebric/var/correctvar.wae");
		checker.visit(ast.getRoot()); // Check modules
		assertEquals(0, exceptions.size()); // No faults
	}
	
	@Test
	public void testUndefinedVar() throws IOException {
		AbstractSyntaxTree ast = TestUtilities.quickParse("src/test/waebric/var/undefinedvar.wae");
		checker.visit(ast.getRoot()); // Check modules
		assertEquals(2, exceptions.size());
		assertEquals(DeclarationChecker.UndefinedVariableException.class, exceptions.get(0).getClass());
		assertEquals(DeclarationChecker.UndefinedVariableException.class, exceptions.get(1).getClass());
	}
	
}