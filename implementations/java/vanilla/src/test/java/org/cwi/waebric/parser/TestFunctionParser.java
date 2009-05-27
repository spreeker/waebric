package org.cwi.waebric.parser;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.functions.FunctionDef;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.TestScanner;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestFunctionParser {

	private FunctionParser parser;
	
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
	public void testFunctionDef() {
		iterator = TestScanner.quickScan("home end");
		parser = new FunctionParser(iterator, exceptions);
		
		FunctionDef def = new FunctionDef();
		parser.parse(def);
		
		// Assertions: TODO
	}
	
}