package org.cwi.waebric.checker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cwi.waebric.parser.WaebricParser;
import org.cwi.waebric.parser.ast.module.Import;
import org.cwi.waebric.parser.ast.module.Module;
import org.cwi.waebric.parser.ast.module.ModuleId;
import org.cwi.waebric.parser.ast.module.Modules;
import org.cwi.waebric.parser.exception.SyntaxException;
import org.cwi.waebric.scanner.WaebricScanner;

import org.junit.After;
import org.junit.Test;

public class TestModuleCheck {
	
	private ModuleCheck check;
	private Map<ModuleId, Modules> cache;
	private List<SemanticException> exceptions;
	
	public TestModuleCheck() {
		exceptions = new ArrayList<SemanticException>();
		cache = new HashMap<ModuleId, Modules>();
		check = new ModuleCheck(cache);
	}
	
	@After
	public void tearDown() {
		exceptions.clear();
		cache.clear();
	}
	
	public static Modules quickParse(String path) throws FileNotFoundException {
		FileReader reader = new FileReader(path);
		WaebricScanner scanner = new WaebricScanner(reader);
		WaebricParser parser = new WaebricParser(scanner);
		List<SyntaxException> e = parser.parseTokens();
		assertEquals(0, e.size());

		// Retrieve root node
		return parser.getAbstractSyntaxTree().getRoot();
	}

	@Test
	public void testImport() throws FileNotFoundException {
		Modules modules = quickParse("src/test/waebric/mod/mymodule.wae");
		check.checkAST(modules, exceptions);
		
		assertEquals(0, exceptions.size()); // No faults
		for(Module module : modules) { // All related modules cached
			assertTrue(check.hasCached(module.getIdentifier()));
			for(int i = 0; i < module.getImportCount(); i++) {
				Import imprt = module.getImport(i);
				assertTrue(check.hasCached(imprt.getIdentifier()));
			}
		}	
	}
	
	@Test
	public void testInvalidModuleDec() throws FileNotFoundException {
		Modules modules = quickParse("src/test/waebric/mod/invalidmodule.wae");
		check.checkAST(modules, exceptions);
		assertEquals(1, exceptions.size());
		assertEquals(ModuleCheck.NonExistingModuleException.class, exceptions.get(0).getClass());
	}
	
	@Test
	public void testInvalidImport() throws FileNotFoundException {
		Modules modules = quickParse("src/test/waebric/mod/invalidimport.wae");
		check.checkAST(modules, exceptions);
		assertEquals(1, exceptions.size());
		assertEquals(ModuleCheck.NonExistingModuleException.class, exceptions.get(0).getClass());
	}
	
}