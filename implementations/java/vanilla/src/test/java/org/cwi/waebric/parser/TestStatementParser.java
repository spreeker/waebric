package org.cwi.waebric.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.expression.Expression.NatExpression;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.ast.statement.Assignment;
import org.cwi.waebric.parser.ast.statement.Formals;
import org.cwi.waebric.parser.ast.statement.Statement;
import org.cwi.waebric.parser.ast.statement.Statement.*;
import org.cwi.waebric.parser.ast.statement.embedding.Embed;
import org.cwi.waebric.parser.ast.statement.predicate.Predicate;
import org.cwi.waebric.parser.exception.SyntaxException;
import org.cwi.waebric.scanner.TestScanner;
import org.cwi.waebric.scanner.token.WaebricToken;
import org.cwi.waebric.scanner.token.WaebricTokenIterator;
import org.cwi.waebric.scanner.token.WaebricTokenSort;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestStatementParser {

	private StatementParser parser;
	private Formals.Regular formals;
	
	private List<SyntaxException> exceptions;
	private WaebricTokenIterator iterator;
	
	@Before
	public void setUp() {
		exceptions = new ArrayList<SyntaxException>();
		formals = new Formals.Regular();
		formals.addIdentifier(new IdCon("var"));
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
		
		Assignment.FuncBind assignment = parser.parseIdConAssignment(formals);
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
		
		If statement = parser.parseIfStatement(formals);
		assertEquals(Predicate.RegularPredicate.class, statement.getPredicate().getClass());
		assertEquals(Statement.Comment.class, statement.getStatement().getClass());
	}
	
	@Test
	public void testIfElseStatement() throws SyntaxException {
		iterator = TestScanner.quickScan("if(123) comment \"succes\" else yield;");
		parser = new StatementParser(iterator, exceptions);
		
		IfElse statement = (IfElse) parser.parseIfStatement(formals);
		assertEquals(Predicate.RegularPredicate.class, statement.getPredicate().getClass());
		assertEquals(Statement.Comment.class, statement.getStatement().getClass());
		assertEquals(Statement.Yield.class, statement.getElseStatement().getClass());
	}
	
	@Test
	public void testEchoEmbeddingStatement() throws SyntaxException {
		iterator = TestScanner.quickScan("echo \"<123>\";");
		parser = new StatementParser(iterator, exceptions);
		
		EchoEmbedding statement = parser.parseEchoEmbeddingStatement(formals);
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
		
		Each statement = parser.parseEachStatement(formals);
		assertEquals("var1", statement.getVar().getIdentifier().getLiteral().toString());
		assertEquals(Expression.NatExpression.class, statement.getExpression().getClass());
		assertEquals(Statement.Comment.class, statement.getStatement().getClass());
	}
	
	@Test
	public void testStatementCollection() throws SyntaxException {
		iterator = TestScanner.quickScan("{ yield; comment \"text\" markup1 markup2 var; }");
		parser = new StatementParser(iterator, exceptions);
		
		Block statement = parser.parseStatementCollection(formals);
		assertEquals(3, statement.getStatementCount());
		assertEquals(Statement.Yield.class, statement.getStatement(0).getClass());
		assertEquals(Statement.Comment.class, statement.getStatement(1).getClass());
		assertEquals(Statement.MarkupExp.class, statement.getStatement(2).getClass());
	}
	
	@Test
	public void testLetStatement() throws SyntaxException {
		iterator = TestScanner.quickScan("let var=100 in comment \"test\" end");
		parser = new StatementParser(iterator, exceptions);
		
		Let statement = parser.parseLetStatement(formals);
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
		
		SingleMarkupStatement statement = (SingleMarkupStatement) parser.parseStatement(formals);
		assertEquals(Markup.MarkupWithoutArguments.class, statement.getMarkup().getClass());
	}
	
	@Test
	public void testIsMarkup() {
		assertTrue(StatementParser.isMarkup(new WaebricToken("func1", WaebricTokenSort.IDCON, 0, 0), formals)); // Markup
		assertFalse(StatementParser.isMarkup(new WaebricToken("var", WaebricTokenSort.IDCON, 0, 0), formals)); // Arg, part of formals
		assertFalse(StatementParser.isMarkup(new WaebricToken(123, WaebricTokenSort.NATCON, 0, 0), formals)); // Natural
	}
	
	@Test
	public void testExpressionMarkupsStatement() throws SyntaxException {
		// Natural expression (easy example)
		iterator = TestScanner.quickScan("func1 func2 123;");
		parser = new StatementParser(iterator, exceptions);
		
		MarkupExp natstm = (MarkupExp) parser.parseMarkupStatements(formals);
		assertEquals(2, natstm.getMarkupCount());
		assertEquals(Expression.NatExpression.class, natstm.getExpression().getClass());
		
		// Var expression (resembles mark-up)
		iterator = TestScanner.quickScan("func1 func2 var;");
		parser = new StatementParser(iterator, exceptions);
		
		MarkupExp varstm = (MarkupExp) parser.parseMarkupStatements(formals);
		assertEquals(2, varstm.getMarkupCount());
		assertEquals(Expression.VarExpression.class, varstm.getExpression().getClass());
		
		// Text expression
		iterator = TestScanner.quickScan("func1 func2 \"123\";");
		parser = new StatementParser(iterator, exceptions);
		
		MarkupExp estatement = (MarkupExp) parser.parseMarkupStatements(formals);
		assertEquals(2, estatement.getMarkupCount());
		assertEquals(Expression.TextExpression.class, estatement.getExpression().getClass());
	}
	
	@Test
	public void testEmbeddingMarkupsStatement() throws SyntaxException {
		iterator = TestScanner.quickScan("func1 func2 \"<123>\";");
		parser = new StatementParser(iterator, exceptions);
		
		MarkupEmbedding statement = (MarkupEmbedding) parser.parseMarkupStatements(formals);
		assertEquals(2, statement.getMarkupCount());
		assertEquals(Embed.ExpressionEmbed.class, statement.getEmbedding().getEmbed().getClass());
	}
	
	@Test
	public void testStatementMarkupsStatement() throws SyntaxException {
		iterator = TestScanner.quickScan("func1 func2 yield;;");
		parser = new StatementParser(iterator, exceptions);
		
		MarkupStat statement = (MarkupStat) parser.parseMarkupStatements(formals);
		assertEquals(2, statement.getMarkupCount());
		assertEquals(Statement.Yield.class, statement.getStatement().getClass());
		
		// Markup statement collection
		iterator = TestScanner.quickScan("markup { func1 func2 var; }");
		parser = new StatementParser(iterator, exceptions);
		
		MarkupStat cstatement = (MarkupStat) parser.parseMarkupStatements(formals);
		assertEquals(1, cstatement.getMarkupCount());
		assertEquals(Statement.Block.class, cstatement.getStatement().getClass());
	}
	
	@Test
	public void testMarkupMarkupsStatement() throws SyntaxException {
		iterator = TestScanner.quickScan("func1 func2 func3;");
		parser = new StatementParser(iterator, exceptions);
		
		MarkupMarkup statement = (MarkupMarkup) parser.parseMarkupStatements(formals);
		assertEquals(2, statement.getMarkupCount());
		assertEquals(Markup.MarkupWithoutArguments.class, statement.getMarkup().getClass());
	}

}