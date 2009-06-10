package org.cwi.waebric.checker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.TestUtilities;
import org.cwi.waebric.parser.ast.AbstractSyntaxTree;
import org.cwi.waebric.parser.ast.module.Import;
import org.cwi.waebric.parser.ast.module.Module;
import org.junit.After;
import org.junit.Test;

public class TestModuleCheck {
	
	private List<SemanticException> exceptions;
	private WaebricChecker checker;
	private ModuleCheck check;
	
	public TestModuleCheck() {
		exceptions = new ArrayList<SemanticException>();
		checker = new WaebricChecker();
		check = new ModuleCheck(checker);
	}
	
	@After
	public void tearDown() {
		exceptions.clear();
		checker.clearCache();
	}

	@Test
	public void testImport() throws FileNotFoundException {
		AbstractSyntaxTree ast = TestUtilities.quickParse("src/test/waebric/mod/mymodule.wae");
		check.checkAST(ast, exceptions);
		
		assertEquals(0, exceptions.size()); // No faults
		for(Module module: ast.getRoot()) { // All related modules cached
			assertTrue(checker.hasCached(module.getIdentifier()));
			for(Import imprt: module.getImports()) {
				assertTrue(checker.hasCached(imprt.getIdentifier()));
			}
		}	
	}
	
	@Test
	public void testInvalidModuleDec() throws FileNotFoundException {
		AbstractSyntaxTree ast = TestUtilities.quickParse("src/test/waebric/mod/invalidmodule.wae");
		check.checkAST(ast, exceptions);
		assertEquals(1, exceptions.size());
		assertEquals(ModuleCheck.NonExistingModuleException.class, exceptions.get(0).getClass());
	}
	
	@Test
	public void testInvalidImport() throws FileNotFoundException {
		AbstractSyntaxTree ast = TestUtilities.quickParse("src/test/waebric/mod/invalidimport.wae");
		check.checkAST(ast, exceptions);
		assertEquals(1, exceptions.size());
		assertEquals(ModuleCheck.NonExistingModuleException.class, exceptions.get(0).getClass());
	}
	
	@Test
	public void testInfiniteImportLoop() throws FileNotFoundException {
		AbstractSyntaxTree ast = TestUtilities.quickParse("src/test/waebric/mod/mymodule.wae");
		check.checkAST(ast, exceptions);
		assertEquals(0, exceptions.size()); // No faults
		assertTrue(checker.hasCached(ast.getRoot().get(0).getIdentifier())); // Module cached
	}
	
}