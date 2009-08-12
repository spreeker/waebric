package org.cwi.waebric.checker;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.TestUtilities;
import org.cwi.waebric.checker.exception.NonExistingModuleException;
import org.cwi.waebric.checker.exception.SemanticException;
import org.cwi.waebric.parser.ast.AbstractSyntaxTree;
import org.cwi.waebric.util.ModuleRegister;
import org.junit.After;
import org.junit.Test;

public class TestModuleChecker {
	
	private List<SemanticException> exceptions;
	private ModuleChecker checker;
	
	public TestModuleChecker() {
		exceptions = new ArrayList<SemanticException>();
		checker = new ModuleChecker(exceptions);
	}
	
	@After
	public void tearDown() {
		exceptions.clear();
		ModuleRegister.getInstance().clearCache();
	}

	@Test
	public void testInvalidImport() throws IOException {
		AbstractSyntaxTree ast = TestUtilities.quickParse("src/test/waebric/mod/invalidimport.wae");
		checker.visit(ast.getRoot());
		assertEquals(1, exceptions.size());
		assertEquals(NonExistingModuleException.class, exceptions.get(0).getClass());
	}
	
	@Test
	public void testInfiniteImportLoop() throws IOException {
		AbstractSyntaxTree ast = TestUtilities.quickParse("src/test/waebric/mod/selfloop.wae");
		checker.visit(ast.getRoot());
		assertEquals(0, exceptions.size()); // No faults
	}
	
}