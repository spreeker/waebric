package org.cwi.waebric.checker;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.cwi.waebric.TestUtilities;
import org.cwi.waebric.parser.ast.AbstractSyntaxTree;
import org.cwi.waebric.util.ModuleRegister;
import org.junit.After;
import org.junit.Test;

public class TestModuleChecker {
	
	private WaebricChecker checker;
	private List<SemanticException> exceptions;
	
	public TestModuleChecker() {
		checker = new WaebricChecker();
	}
	
	@After
	public void tearDown() {
		ModuleRegister.getInstance().clearCache();
	}

	@Test
	public void testInvalidImport() throws IOException {
		AbstractSyntaxTree ast = TestUtilities.quickParse("src/test/waebric/mod/invalidimport.wae");
		exceptions = checker.checkAST(ast);
		assertEquals(1, exceptions.size());
		assertEquals(NonExistingModuleException.class, exceptions.get(0).getClass());
	}
	
	@Test
	public void testInfiniteImportLoop() throws IOException {
		AbstractSyntaxTree ast = TestUtilities.quickParse("src/test/waebric/mod/selfloop.wae");
		exceptions = checker.checkAST(ast);
		assertEquals(0, exceptions.size()); // No faults
	}
	
}