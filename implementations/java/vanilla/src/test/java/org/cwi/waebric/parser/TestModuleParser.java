package org.cwi.waebric.parser;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.module.FunctionDef;
import org.cwi.waebric.parser.ast.module.Import;
import org.cwi.waebric.parser.ast.module.Module;
import org.cwi.waebric.parser.ast.module.ModuleId;
import org.cwi.waebric.parser.ast.module.Modules;
import org.cwi.waebric.parser.ast.module.site.Site;
import org.cwi.waebric.parser.exception.SyntaxException;
import org.cwi.waebric.scanner.TestScanner;
import org.cwi.waebric.scanner.token.WaebricTokenIterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestModuleParser {
	
	private ModuleParser parser;
	
	private List<SyntaxException> exceptions;
	private WaebricTokenIterator iterator;
	
	@Before
	public void setUp() {
		exceptions = new ArrayList<SyntaxException>();
	}
	
	@After
	public void tearDown() {
		exceptions.clear();
		exceptions = null;
		parser = null;
		iterator = null;
	}
	
	@Test
	public void testModuleId() throws SyntaxException {
		iterator = TestScanner.quickScan("org.cwi.waebric.mymodule");
		parser = new ModuleParser(iterator, exceptions);
		
		ModuleId moduleId = parser.parseModuleId();
		
		// Assertions
		assertEquals(0, exceptions.size());
		assertEquals(7, moduleId.getChildren().length); // Four elements and three period separators
		assertEquals("org", moduleId.get(0).getLiteral().toString());
		assertEquals("cwi", moduleId.get(1).getLiteral().toString());
		assertEquals("waebric", moduleId.get(2).getLiteral().toString());
		assertEquals("mymodule", moduleId.get(3).getLiteral().toString());
	}
	
	@Test
	public void testModules() throws SyntaxException {
		iterator = TestScanner.quickScan("module mymodule1\nmodule mymodule2");
		parser = new ModuleParser(iterator, exceptions);
		
		Modules modules = parser.parseModules();
		
		// Assertions
		assertEquals(0, exceptions.size());
		assertEquals(2, modules.size());
		assertEquals(Module.class, modules.getChildren()[0].getClass());
		assertEquals(Module.class, modules.getChildren()[1].getClass());
	}
	
	@Test
	public void testModule() throws SyntaxException {
		iterator = TestScanner.quickScan("org.cwi.waebric.mymodule\nimport newmodule\nsite\n\tindex.html: home(1)\nend\ndef home\nend");
		parser = new ModuleParser(iterator, exceptions);
		
		Module module = parser.parseModule();
		
		// Assertions
		assertEquals(0, exceptions.size());
		assertEquals("module", module.getChildren()[0].toString());
		assertEquals(ModuleId.class, module.getChildren()[1].getClass());
		assertEquals(3, module.getElements().length);
		assertEquals(Import.class, module.getElements()[0].getClass());
		assertEquals(Site.class, module.getElements()[1].getClass());
		assertEquals(FunctionDef.class, module.getElements()[2].getClass());
	}
	
	@Test
	public void testImport() throws SyntaxException {
		iterator = TestScanner.quickScan("org.cwi.waebric.mymodule");
		parser = new ModuleParser(iterator, exceptions);
		
		Import imprt = parser.parseImport();
		
		// Assertions
		assertEquals(0, exceptions.size());
		assertEquals("import", imprt.getChildren()[0].toString());
		assertEquals(ModuleId.class, imprt.getChildren()[1].getClass());
	}

}