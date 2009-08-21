package org.cwi.waebric.checker;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.cwi.waebric.TestUtilities;
import org.cwi.waebric.parser.ast.AbstractSyntaxTree;
import org.junit.Test;

public class TestDeclarationChecker {
	
	private List<SemanticException> exceptions;
	private WaebricChecker checker;
	
	public TestDeclarationChecker() {
		checker = new WaebricChecker();
	}
	
	@Test
	public void testDuplicateFunction() throws IOException {
		AbstractSyntaxTree ast = TestUtilities.quickParse("src/test/waebric/func/duplicate.wae");
		exceptions = checker.checkAST(ast);
		assertEquals(2, exceptions.size());
		assertEquals(DuplicateFunctionDefinition.class, exceptions.get(0).getClass());
		assertEquals(DuplicateFunctionDefinition.class, exceptions.get(1).getClass());
	}
	
	@Test
	public void testArityMismatch() throws IOException {
		AbstractSyntaxTree ast = TestUtilities.quickParse("src/test/waebric/func/aritymm.wae");
		exceptions = checker.checkAST(ast);
		assertEquals(2, exceptions.size());
		assertEquals(ArityMismatchException.class, exceptions.get(0).getClass());
		assertEquals(ArityMismatchException.class, exceptions.get(1).getClass());
	}
	
	@Test
	public void testUndefinedFunction() throws IOException {
		AbstractSyntaxTree ast = TestUtilities.quickParse("src/test/waebric/func/undeffunc.wae");
		exceptions = checker.checkAST(ast);
		assertEquals(3, exceptions.size());
		assertEquals(UndefinedFunctionException.class, exceptions.get(0).getClass());
		assertEquals(UndefinedFunctionException.class, exceptions.get(1).getClass());
		assertEquals(UndefinedFunctionException.class, exceptions.get(2).getClass());
	}
	
	@Test
	public void testVarCheck() throws IOException {
		AbstractSyntaxTree ast = TestUtilities.quickParse("src/test/waebric/var/correctvar.wae");
		exceptions = checker.checkAST(ast);
		assertEquals(0, exceptions.size()); // No faults
	}
	
	@Test
	public void testUndefinedVar() throws IOException {
		AbstractSyntaxTree ast = TestUtilities.quickParse("src/test/waebric/var/undefinedvar.wae");
		exceptions = checker.checkAST(ast);
		assertEquals(2, exceptions.size());
		assertEquals(UndefinedVariableException.class, exceptions.get(0).getClass());
		assertEquals(UndefinedVariableException.class, exceptions.get(1).getClass());
	}
	
}