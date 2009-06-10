package org.cwi.waebric.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.expression.Expression.NatExpression;
import org.cwi.waebric.parser.ast.module.function.Formals;
import org.cwi.waebric.parser.ast.statement.Assignment;
import org.cwi.waebric.parser.ast.statement.Statement;
import org.cwi.waebric.parser.ast.statement.Statement.Block;
import org.cwi.waebric.parser.ast.statement.Statement.CData;
import org.cwi.waebric.parser.ast.statement.Statement.Comment;
import org.cwi.waebric.parser.ast.statement.Statement.Each;
import org.cwi.waebric.parser.ast.statement.Statement.Echo;
import org.cwi.waebric.parser.ast.statement.Statement.EchoEmbedding;
import org.cwi.waebric.parser.ast.statement.Statement.If;
import org.cwi.waebric.parser.ast.statement.Statement.IfElse;
import org.cwi.waebric.parser.ast.statement.Statement.Let;
import org.cwi.waebric.parser.ast.statement.Statement.MarkupEmbedding;
import org.cwi.waebric.parser.ast.statement.Statement.MarkupExp;
import org.cwi.waebric.parser.ast.statement.Statement.MarkupMarkup;
import org.cwi.waebric.parser.ast.statement.Statement.MarkupStat;
import org.cwi.waebric.parser.ast.statement.Statement.RegularMarkupStatement;
import org.cwi.waebric.parser.ast.statement.embedding.Embed;
import org.cwi.waebric.parser.ast.statement.predicate.Predicate;
import org.cwi.waebric.parser.exception.SyntaxException;
import org.cwi.waebric.scanner.TestScanner;
import org.cwi.waebric.scanner.token.WaebricTokenIterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestStatementParser {

	private StatementParser parser;
	
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
	public void testFormals() throws SyntaxException {
		iterator = TestScanner.quickScan("(var1,var2)");
		parser = new StatementParser(iterator, exceptions);
		
		Formals formals = parser.parseFormals();
		assertEquals(2, formals.getIdentifiers().size());
		assertEquals("var1", formals.getIdentifiers().get(0).getLiteral().toString());
		assertEquals("var2", formals.getIdentifiers().get(1).getLiteral().toString());
	}
	
	@Test
	public void testVarAssignment() throws SyntaxException {
		iterator = TestScanner.quickScan("var=100");
		parser = new StatementParser(iterator, exceptions);
		
		Assignment.VarBind assignment = parser.parseVarAssignment();
		assertEquals("var", assignment.getIdentifier().getLiteral().toString());
		assertEquals(NatExpression.class, assignment.getExpression().getClass());
	}
	
	@Test
	public void testIdConAssignment() throws SyntaxException {
		iterator = TestScanner.quickScan("identifier1(var1,var2) = yield;");
		parser = new StatementParser(iterator, exceptions);
		
		Assignment.FuncBind assignment = parser.parseIdConAssignment();
		assertEquals("identifier1", assignment.getIdentifier().getLiteral().toString());
		assertEquals(2, assignment.getIdentifierCount());
		assertEquals("var1", assignment.getIdentifier(0).getLiteral().toString());
		assertEquals("var2", assignment.getIdentifier(1).getLiteral().toString());
		assertEquals(Statement.Yield.class, assignment.getStatement().getClass());
	}
	
	@Test
	public void testIfStatement() throws SyntaxException {
		iterator = TestScanner.quickScan("if(123) comment \"succes\"");
		parser = new StatementParser(iterator, exceptions);
		
		If statement = parser.parseIfStatement();
		assertEquals(Predicate.RegularPredicate.class, statement.getPredicate().getClass());
		assertEquals(Statement.Comment.class, statement.getStatement().getClass());
	}
	
	@Test
	public void testIfElseStatement() throws SyntaxException {
		iterator = TestScanner.quickScan("if(123) comment \"succes\" else yield;");
		parser = new StatementParser(iterator, exceptions);
		
		IfElse statement = (IfElse) parser.parseIfStatement();
		assertEquals(Predicate.RegularPredicate.class, statement.getPredicate().getClass());
		assertEquals(Statement.Comment.class, statement.getStatement().getClass());
		assertEquals(Statement.Yield.class, statement.getElseStatement().getClass());
	}
	
	@Test
	public void testEchoEmbeddingStatement() throws SyntaxException {
		iterator = TestScanner.quickScan("echo \"<123>\";");
		parser = new StatementParser(iterator, exceptions);
		
		EchoEmbedding statement = parser.parseEchoEmbeddingStatement();
		assertNotNull(statement.getEmbedding());
	}
	
	@Test
	public void testEchoExpressionStatement() throws SyntaxException {
		iterator = TestScanner.quickScan("echo 10;");
		parser = new StatementParser(iterator, exceptions);
		
		Echo statement = parser.parseEchoExpressionStatement();
		assertEquals(Expression.NatExpression.class, statement.getExpression().getClass());
	}
	
	@Test
	public void testEachStatement() throws SyntaxException {
		iterator = TestScanner.quickScan("each(var1:10) comment \"test\"");
		parser = new StatementParser(iterator, exceptions);
		
		Each statement = parser.parseEachStatement();
		assertEquals("var1", statement.getVar().getLiteral().toString());
		assertEquals(Expression.NatExpression.class, statement.getExpression().getClass());
		assertEquals(Statement.Comment.class, statement.getStatement().getClass());
	}
	
	@Test
	public void testStatementCollection() throws SyntaxException {
		iterator = TestScanner.quickScan("{ yield; comment \"text\" markup1 markup2 var; }");
		parser = new StatementParser(iterator, exceptions);
		
		Block statement = parser.parseStatementCollection();
		assertEquals(3, statement.getStatementCount());
		assertEquals(Statement.Yield.class, statement.getStatement(0).getClass());
		assertEquals(Statement.Comment.class, statement.getStatement(1).getClass());
		assertEquals(Statement.MarkupExp.class, statement.getStatement(2).getClass());
	}
	
	@Test
	public void testLetStatement() throws SyntaxException {
		iterator = TestScanner.quickScan("let var=100 in comment \"test\" end");
		parser = new StatementParser(iterator, exceptions);
		
		Let statement = parser.parseLetStatement();
		assertEquals(1, statement.getAssignmentCount());
		assertEquals(Assignment.VarBind.class, statement.getAssignment(0).getClass());
		assertEquals(1, statement.getStatementCount());
		assertEquals(Statement.Comment.class, statement.getStatement(0).getClass());
	}
	
	@Test
	public void testCDataStatement() throws SyntaxException {
		iterator = TestScanner.quickScan("cdata 10;");
		parser = new StatementParser(iterator, exceptions);
		
		CData statement = parser.parseCDataStatement();
		assertEquals(Expression.NatExpression.class, statement.getExpression().getClass());
	}
	
	@Test
	public void testCommentStatement() throws SyntaxException {
		iterator = TestScanner.quickScan("comment \"OH NOES TEH HAXZOR\";");
		parser = new StatementParser(iterator, exceptions);
		
		Comment statement = parser.parseCommentStatement();
		assertEquals("OH NOES TEH HAXZOR", statement.getComment().getLiteral().toString());
	}
	
	@Test
	public void testYieldStatement() throws SyntaxException {
		iterator = TestScanner.quickScan("yield;");
		parser = new StatementParser(iterator, exceptions);
		
		parser.parseYieldStatement();
		assertEquals(0, exceptions.size());
	}
	
	@Test
	public void testMarkupStatement() throws SyntaxException {
		iterator = TestScanner.quickScan("func1;");
		parser = new StatementParser(iterator, exceptions);
		
		RegularMarkupStatement statement = (RegularMarkupStatement) parser.parseStatement();
		assertEquals("func1", statement.getMarkup().getDesignator().getIdentifier().getLiteral().toString());
	}
	
	@Test
	public void testIsMarkup() {
		
	}
	
	@Test
	public void testExpressionMarkupsStatement() throws SyntaxException {
		// Natural expression (easy example)
		iterator = TestScanner.quickScan("func1 func2 123;");
		parser = new StatementParser(iterator, exceptions);
		
		MarkupExp natstm = (MarkupExp) parser.parseMarkupStatements();
		assertEquals(2, natstm.getMarkupCount());
		assertEquals(Expression.NatExpression.class, natstm.getExpression().getClass());
		
		// Var expression (resembles mark-up)
		iterator = TestScanner.quickScan("func1 func2 var;");
		parser = new StatementParser(iterator, exceptions);
		
		MarkupExp varstm = (MarkupExp) parser.parseMarkupStatements();
		assertEquals(2, varstm.getMarkupCount());
		assertEquals(Expression.VarExpression.class, varstm.getExpression().getClass());
		
		// Text expression
		iterator = TestScanner.quickScan("func1 func2 \"123\";");
		parser = new StatementParser(iterator, exceptions);
		
		MarkupExp estatement = (MarkupExp) parser.parseMarkupStatements();
		assertEquals(2, estatement.getMarkupCount());
		assertEquals(Expression.TextExpression.class, estatement.getExpression().getClass());
	}
	
	@Test
	public void testEmbeddingMarkupsStatement() throws SyntaxException {
		iterator = TestScanner.quickScan("func1 func2 \"<123>\";");
		parser = new StatementParser(iterator, exceptions);
		
		MarkupEmbedding statement = (MarkupEmbedding) parser.parseMarkupStatements();
		assertEquals(2, statement.getMarkupCount());
		assertEquals(Embed.ExpressionEmbed.class, statement.getEmbedding().getEmbed().getClass());
	}
	
	@Test
	public void testStatementMarkupsStatement() throws SyntaxException {
		iterator = TestScanner.quickScan("func1 func2 yield;;");
		parser = new StatementParser(iterator, exceptions);
		
		MarkupStat statement = (MarkupStat) parser.parseMarkupStatements();
		assertEquals(2, statement.getMarkupCount());
		assertEquals(Statement.Yield.class, statement.getStatement().getClass());
		
		// Markup statement collection
		iterator = TestScanner.quickScan("markup { func1 func2 var; }");
		parser = new StatementParser(iterator, exceptions);
		
		MarkupStat cstatement = (MarkupStat) parser.parseMarkupStatements();
		assertEquals(1, cstatement.getMarkupCount());
		assertEquals(Statement.Block.class, cstatement.getStatement().getClass());
	}
	
	@Test
	public void testMarkupMarkupsStatement() throws SyntaxException {
		iterator = TestScanner.quickScan("func1 func2 func3();");
		parser = new StatementParser(iterator, exceptions);
		
		MarkupMarkup statement = (MarkupMarkup) parser.parseMarkupStatements();
		assertEquals(2, statement.getMarkupCount());
	}
	
	@Test
	public void testCaveat() {
		// Mark-up
		parser = new StatementParser(TestScanner.quickScan("p;"), exceptions);
		assertTrue(parser.isMarkup(1));
		
		// Mark-up, variable
		parser = new StatementParser(TestScanner.quickScan("p p;"), exceptions);
		assertTrue(parser.isMarkup(1));
		assertFalse(parser.isMarkup(2));
		
		// Mark-up, mark-up
		parser = new StatementParser(TestScanner.quickScan("p p();"), exceptions);
		assertTrue(parser.isMarkup(1));
		assertTrue(parser.isMarkup(2));
		
		// Markup, mark-up, natural
		parser = new StatementParser(TestScanner.quickScan("p p 123;"), exceptions);
		assertTrue(parser.isMarkup(1));
		assertTrue(parser.isMarkup(2));
		assertFalse(parser.isMarkup(3));
	}

}