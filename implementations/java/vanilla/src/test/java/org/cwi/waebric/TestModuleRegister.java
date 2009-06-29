package org.cwi.waebric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.cwi.waebric.parser.ast.module.Module;
import org.cwi.waebric.parser.ast.module.ModuleId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestModuleRegister {
	
	private ModuleId identifier;
	private Module content;
	
	@Before
	public void setUp() throws IOException {
		content = TestUtilities.quickParse("src/test/waebric/mod/dependantmod1.wae").getRoot();
		identifier = content.getIdentifier();
		ModuleRegister.getInstance().cacheModule(identifier, content);
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
		List<Module> dependencies = ModuleRegister.getInstance().loadDependencies(content);
		assertEquals(3, dependencies.size()); // Sub module is imported
	}
	
	@Test
	public void testClearCache() {
		assertTrue(ModuleRegister.getInstance().hasCached(identifier));
		ModuleRegister.getInstance().clearCache();
		assertFalse(ModuleRegister.getInstance().hasCached(identifier));
	}
	
}