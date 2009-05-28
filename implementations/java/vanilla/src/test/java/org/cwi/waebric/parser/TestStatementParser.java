package org.cwi.waebric.parser;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.expressions.Expression.NatExpression;
import org.cwi.waebric.parser.ast.statements.Assignment;
import org.cwi.waebric.parser.ast.statements.Formals;
import org.cwi.waebric.parser.ast.statements.Statement;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.TestScanner;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestStatementParser {

	private StatementParser parser;
	
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
	public void testFormals() {
		iterator = TestScanner.quickScan("(var1,var2)");
		parser = new StatementParser(iterator, exceptions);
		
		Formals formals = parser.parseFormals();
		assertEquals(0, exceptions.size());
		assertEquals(2, formals.getVarCount());
		assertEquals("var1", formals.getVar(0).toString());
		assertEquals("var2", formals.getVar(1).toString());
	}
	
	@Test
	public void testVarAssignment() {
		iterator = TestScanner.quickScan("var=100");
		parser = new StatementParser(iterator, exceptions);
		
		Assignment.VarAssignment assignment = parser.parseVarAssignment();
		assertEquals(0, exceptions.size());
		assertEquals("var", assignment.getVar().toString());
		assertEquals(NatExpression.class, assignment.getExpression().getClass());
	}
	
	@Test
	public void testIdConAssignment() {
		iterator = TestScanner.quickScan("identifier1(var1,var2) = yield;");
		parser = new StatementParser(iterator, exceptions);
		
		Assignment.IdConAssignment assignment = parser.parseIdConAssignment();
		assertEquals(0, exceptions.size());
		assertEquals("identifier1", assignment.getIdentifier().toString());
		assertEquals(2, assignment.getFormals().getVarCount());
		assertEquals("var1", assignment.getFormals().getVar(0).toString());
		assertEquals("var2", assignment.getFormals().getVar(1).toString());
		assertEquals(Statement.YieldStatement.class, assignment.getStatement().getClass());
	}
	
	@Test
	public void testInvalidAssignment() {
		
	}

}