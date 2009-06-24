package org.cwi.waebric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.cwi.waebric.parser.ast.AbstractSyntaxTree;
import org.cwi.waebric.parser.ast.module.ModuleId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestModuleRegister {
	
	private ModuleId identifier;
	private AbstractSyntaxTree ast;
	
	@Before
	public void setUp() throws IOException {
		ast = TestUtilities.quickParse("src/test/waebric/mod/dependantmod1.wae");
		identifier = ast.getRoot().get(0).getIdentifier();
		ModuleRegister.getInstance().cacheModule(identifier, ast);
	}
	
	@After
	public void tearDown() {
		ModuleRegister.getInstance().clearCache();
	}

	@Test
	public void testHasCached() {
		assertTrue(ModuleRegister.getInstance().hasCached(identifier));
		assertFalse(ModuleRegister.getInstance().hasCached(new ModuleId()));
		assertFalse(ModuleRegister.getInstance().hasCached(null));
	}
	
	@Test
	public void testLoadDependancies() {
		assertEquals(1, ast.getRoot().size());
		AbstractSyntaxTree dependencies = ModuleRegister.getInstance().loadDependancies(ast);
		assertEquals(3, dependencies.getRoot().size()); // Sub module is imported
	}
	
	@Test
	public void testClearCache() {
		assertTrue(ModuleRegister.getInstance().hasCached(identifier));
		ModuleRegister.getInstance().clearCache();
		assertFalse(ModuleRegister.getInstance().hasCached(identifier));
	}
	
}