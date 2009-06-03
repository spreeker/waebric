package org.cwi.waebric.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.functions.FunctionDef;
import org.cwi.waebric.parser.ast.statements.Statement;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.TestScanner;
import org.cwi.waebric.scanner.token.WaebricTokenIterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestFunctionParser {

	private FunctionParser parser;
	
	private List<ParserException> exceptions;
	private WaebricTokenIterator iterator;
	
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
		iterator = TestScanner.quickScan("home(var1,var2) comment \"lol\" end");
		parser = new FunctionParser(iterator, exceptions);
		
		FunctionDef def = parser.parseFunctionDef();
		assertTrue(exceptions.size() == 0);
		assertEquals(2, def.getFormals().size());
		assertEquals(1, def.getStatementCount());
		assertEquals(Statement.CommentStatement.class, def.getStatement(0).getClass());
	}
	
}