package org.cwi.waebric.parser;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.expressions.Expression;
import org.cwi.waebric.parser.ast.expressions.Expression.NatExpression;
import org.cwi.waebric.parser.ast.predicates.Predicate;
import org.cwi.waebric.parser.ast.statements.Assignment;
import org.cwi.waebric.parser.ast.statements.Formals;
import org.cwi.waebric.parser.ast.statements.Statement;
import org.cwi.waebric.parser.ast.statements.Statement.IfElseStatement;
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
	public void testIfStatement() {
		iterator = TestScanner.quickScan("if(123) comment \"succes\"");
		parser = new StatementParser(iterator, exceptions);
		
		Statement.IfStatement statement = parser.parseIfStatement();
		assertEquals(0, exceptions.size());
		assertEquals(Predicate.PredicateWithoutType.class, statement.getPredicate().getClass());
		assertEquals(Statement.CommentStatement.class, statement.getStatement().getClass());
	}
	
	@Test
	public void testIfElseStatement() {
		iterator = TestScanner.quickScan("if(123) comment \"succes\" else yield;");
		parser = new StatementParser(iterator, exceptions);
		
		Statement.IfElseStatement statement = (IfElseStatement) parser.parseIfStatement();
		assertEquals(0, exceptions.size());
		assertEquals(Predicate.PredicateWithoutType.class, statement.getPredicate().getClass());
		assertEquals(Statement.CommentStatement.class, statement.getStatement().getClass());
		assertEquals(Statement.YieldStatement.class, statement.getElseStatement().getClass());
	}
	
	@Test
	public void testEchoEmbeddingStatement() {
		iterator = TestScanner.quickScan("identifier1(var1,var2) = yield;");
		parser = new StatementParser(iterator, exceptions);
		
		// TODO: Cannot be tested until embedding is completed
	}
	
	@Test
	public void testEchoExpressionStatement() {
		iterator = TestScanner.quickScan("echo 10;");
		parser = new StatementParser(iterator, exceptions);
		
		Statement.EchoExpressionStatement statement = parser.parseEchoExpressionStatement();
		assertEquals(0, exceptions.size());
		assertEquals(Expression.NatExpression.class, statement.getExpression().getClass());
	}
	
	@Test
	public void testEachStatement() {
		iterator = TestScanner.quickScan("each(var1:10) comment \"test\"");
		parser = new StatementParser(iterator, exceptions);
		
		Statement.EachStatement statement = parser.parseEachStatement();
		assertEquals(0, exceptions.size());
		assertEquals("var1", statement.getVar().getIdentifier().toString());
		assertEquals(Expression.NatExpression.class, statement.getExpression().getClass());
		assertEquals(Statement.CommentStatement.class, statement.getStatement().getClass());
	}
	
	@Test
	public void testLetStatement() {
		iterator = TestScanner.quickScan("let var=100 in comment \"test\" end");
		parser = new StatementParser(iterator, exceptions);
		
		Statement.LetStatement statement = parser.parseLetStatement();
		assertEquals(0, exceptions.size());
		assertEquals(1, statement.getAssignmentCount());
		assertEquals(Assignment.VarAssignment.class, statement.getAssignment(0).getClass());
		assertEquals(1, statement.getStatementCount());
		assertEquals(Statement.CommentStatement.class, statement.getStatement(0).getClass());
	}
	
	@Test
	public void testCDataStatement() {
		iterator = TestScanner.quickScan("cdata 10;");
		parser = new StatementParser(iterator, exceptions);
		
		Statement.CDataStatement statement = parser.parseCDataStatement();
		assertEquals(0, exceptions.size());
		assertEquals(Expression.NatExpression.class, statement.getExpression().getClass());
	}
	
	@Test
	public void testCommentStatement() {
		iterator = TestScanner.quickScan("comment \"OH NOES TEH HAXZOR\";");
		parser = new StatementParser(iterator, exceptions);
		
		Statement.CommentStatement statement = parser.parseCommentStatement();
		assertEquals(0, exceptions.size());
		assertEquals("OH NOES TEH HAXZOR", statement.getComment().toString());
	}
	
	@Test
	public void testStatementCollection() {
		iterator = TestScanner.quickScan("yield;");
		parser = new StatementParser(iterator, exceptions);
		
		parser.parseYieldStatement();
		assertEquals(0, exceptions.size());
	}

}