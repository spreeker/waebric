package org.cwi.waebric.parser;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.module.function.FunctionDef;
import org.cwi.waebric.parser.ast.statement.Statement;
import org.cwi.waebric.parser.exception.SyntaxException;
import org.cwi.waebric.scanner.TestScanner;
import org.cwi.waebric.scanner.token.WaebricTokenIterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestFunctionParser {

	private FunctionParser parser;
	
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
	public void testFunctionDef() throws SyntaxException {
		iterator = TestScanner.quickScan("home(var1,var2) comment \"lol\" end");
		parser = new FunctionParser(iterator, exceptions);
		
		FunctionDef def = parser.parseFunctionDef();
		assertEquals(2, def.getFormals().getIdentifiers().size());
		assertEquals(1, def.getStatementCount());
		assertEquals(Statement.Comment.class, def.getStatement(0).getClass());
	}
	
}