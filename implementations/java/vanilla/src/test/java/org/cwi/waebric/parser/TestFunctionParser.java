package org.cwi.waebric.parser;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.TestUtilities;
import org.cwi.waebric.parser.ast.module.function.FunctionDef;
import org.cwi.waebric.parser.ast.statement.Statement;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestFunctionParser {

	private FunctionParser parser;
	
	private List<SyntaxException> exceptions;
	private TokenIterator iterator;
	
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
		iterator = TestUtilities.quickScan("home(var1,var2) comment \"lol\"; end");
		parser = new FunctionParser(iterator, exceptions);
		
		FunctionDef def = parser.parseFunctionDef();
		assertEquals(2, def.getFormals().getIdentifiers().size());
		assertEquals(1, def.getStatements().size());
		assertEquals(Statement.Comment.class, def.getStatements().get(0).getClass());
	}
	
}