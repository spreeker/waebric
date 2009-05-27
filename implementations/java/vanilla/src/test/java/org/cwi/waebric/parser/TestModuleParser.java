package org.cwi.waebric.parser;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.functions.FunctionDef;
import org.cwi.waebric.parser.ast.module.Import;
import org.cwi.waebric.parser.ast.module.Module;
import org.cwi.waebric.parser.ast.module.ModuleId;
import org.cwi.waebric.parser.ast.module.Modules;
import org.cwi.waebric.parser.ast.site.Site;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.TestScanner;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestModuleParser {
	
	private ModuleParser parser;
	
	private List<ParserException> exceptions;
	private TokenIterator iterator;
	
	@Before
	public void setUp() {
		exceptions = new ArrayList<ParserException>();
	}
	
	@After
	public void tearDown() {
		exceptions.clear();
		exceptions = null;
		parser = null;
		iterator = null;
	}
	
	@Test
	public void testModuleId() {
		iterator = TestScanner.quickScan("org.cwi.waebric.mymodule");
		parser = new ModuleParser(iterator, exceptions);
		
		ModuleId moduleId = new ModuleId();
		parser.parse(moduleId);
		
		// Assertions
		assertEquals(0, exceptions.size());
		assertEquals(7, moduleId.getChildren().length); // Four elements and three period separators
		assertEquals("org", moduleId.getIdentifierElements()[0].toString());
		assertEquals("cwi", moduleId.getIdentifierElements()[1].toString());
		assertEquals("waebric", moduleId.getIdentifierElements()[2].toString());
		assertEquals("mymodule", moduleId.getIdentifierElements()[3].toString());
	}
	
	@Test
	public void testModules() {
		iterator = TestScanner.quickScan("module mymodule1\nmodule mymodule2");
		parser = new ModuleParser(iterator, exceptions);
		
		Modules modules = new Modules();
		parser.parse(modules);
		
		// Assertions
		assertEquals(0, exceptions.size());
		assertEquals(2, modules.size());
		assertEquals(Module.class, modules.getChildren()[0].getClass());
		assertEquals(Module.class, modules.getChildren()[1].getClass());
		
		// TODO: Word outside module definition error
	}
	
	@Test
	public void testModule() {
		iterator = TestScanner.quickScan("org.cwi.waebric.mymodule\nimport newmodule\nsite\n\tindex.html: home(1)\nend\ndef home\nend");
		parser = new ModuleParser(iterator, exceptions);
		
		Module module = new Module();
		parser.parse(module);
		
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
	public void testImport() {
		iterator = TestScanner.quickScan("org.cwi.waebric.mymodule");
		parser = new ModuleParser(iterator, exceptions);
		
		Import imprt = new Import();
		parser.parse(imprt); // Fill import object
		
		// Assertions
		assertEquals(0, exceptions.size());
		assertEquals("import", imprt.getChildren()[0].toString());
		assertEquals(ModuleId.class, imprt.getChildren()[1].getClass());
		
		// TODO: Invalid identifier
	}

}