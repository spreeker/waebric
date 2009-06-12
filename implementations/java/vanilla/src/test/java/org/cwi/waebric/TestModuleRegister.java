package org.cwi.waebric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.cwi.waebric.parser.WaebricParser;
import org.cwi.waebric.parser.ast.AbstractSyntaxTree;
import org.cwi.waebric.parser.ast.module.ModuleId;
import org.cwi.waebric.scanner.WaebricScanner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestModuleRegister {
	
	private ModuleId identifier;
	private AbstractSyntaxTree ast;
	
	@Before
	public void setUp() throws FileNotFoundException {
		FileReader reader = new FileReader("src/test/waebric/helloworld.wae");
		WaebricScanner scanner = new WaebricScanner(reader);
		scanner.tokenizeStream();
		WaebricParser parser = new WaebricParser(scanner);
		parser.parseTokens();
		
		ast = parser.getAbstractSyntaxTree();
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
		assertEquals(2, dependencies.getRoot().size()); // Sub module is imported
	}
	
	@Test
	public void testClearCache() {
		assertTrue(ModuleRegister.getInstance().hasCached(identifier));
		ModuleRegister.getInstance().clearCache();
		assertFalse(ModuleRegister.getInstance().hasCached(identifier));
	}
	
}