package org.cwi.waebric.parser;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.module.Import;
import org.cwi.waebric.parser.ast.module.Module;
import org.cwi.waebric.parser.ast.module.ModuleId;
import org.cwi.waebric.parser.ast.module.Modules;
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
		assertTrue(exceptions.size() == 0);
		assertTrue(moduleId.getChildren().length == 7); // Four elements and three period separators
		assertTrue(moduleId.getIdentifierElements()[0].equals("org"));
		assertTrue(moduleId.getIdentifierElements()[1].equals("cwi"));
		assertTrue(moduleId.getIdentifierElements()[2].equals("waebric"));
		assertTrue(moduleId.getIdentifierElements()[3].equals("mymodule"));
	}
	
	@Test
	public void testModules() {
		iterator = TestScanner.quickScan("module mymodule1\nmodule mymodule2");
		parser = new ModuleParser(iterator, exceptions);
		
		Modules modules = new Modules();
		parser.parse(modules);
		
		// Assertions
		assertTrue(exceptions.size() == 0);
		assertTrue(modules.size() == 2);
		assertTrue(modules.getChildren()[0] instanceof Module);
		assertTrue(modules.getChildren()[1] instanceof Module);
		
		// TODO: Word outside module definition error
	}
	
	@Test
	public void testModule() {
		iterator = TestScanner.quickScan("org.cwi.waebric.mymodule");
		parser = new ModuleParser(iterator, exceptions);
		
		Module module = new Module();
		parser.parse(module);
		
		// Assertions
		assertTrue(exceptions.size() == 0);
		assertTrue(module.getChildren()[0].equals("module"));
		assertTrue(module.getChildren()[1] instanceof ModuleId);
		
		// TODO: Invalid identifier
		// TODO: Invalid keyword
	}
	
	@Test
	public void testImport() {
		iterator = TestScanner.quickScan("org.cwi.waebric.mymodule");
		parser = new ModuleParser(iterator, exceptions);
		
		Import imprt = new Import();
		parser.parse(imprt); // Fill import object
		
		// Assertions
		assertTrue(exceptions.size() == 0);
		assertTrue(imprt.getChildren()[0].equals("import"));
		assertTrue(imprt.getChildren()[1] instanceof ModuleId);
		
		// TODO: Invalid identifier
	}

}