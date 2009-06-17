package org.cwi.waebric.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import org.cwi.waebric.TestUtilities;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestStatementParser {

	private StatementParser parser;
	
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
	public void testFormals() throws SyntaxException {
		iterator = TestUtilities.quickScan("(var1,var2)");
		parser = new StatementParser(iterator, exceptions);
		
		Formals formals = parser.parseFormals();
		assertEquals(2, formals.getIdentifiers().size());
		assertEquals("var1", formals.getIdentifiers().get(0).getToken().getLexeme().toString());
		assertEquals("var2", formals.getIdentifiers().get(1).getToken().getLexeme().toString());
	}
	
	@Test
	public void testVarAssignment() throws SyntaxException {
		iterator = TestUtilities.quickScan("var=100");
		parser = new StatementParser(iterator, exceptions);
		
		Assignment.VarBind assignment = parser.parseVarAssignment();
		assertEquals("var", assignment.getIdentifier().getToken().getLexeme().toString());
		assertEquals(NatExpression.class, assignment.getExpression().getClass());
	}
	
	@Test
	public void testIdConAssignment() throws SyntaxException {
		iterator = TestUtilities.quickScan("identifier1(var1,var2) = yield;");
		parser = new StatementParser(iterator, exceptions);
		
		Assignment.FuncBind assignment = parser.parseIdConAssignment();
		assertEquals("identifier1", assignment.getIdentifier().getToken().getLexeme().toString());
		assertEquals(2, assignment.getVariables().size());
		assertEquals("var1", assignment.getVariables().get(0).getToken().getLexeme().toString());
		assertEquals("var2", assignment.getVariables().get(1).getToken().getLexeme().toString());
		assertEquals(Statement.Yield.class, assignment.getStatement().getClass());
	}
	
	@Test
	public void testIfStatement() throws SyntaxException {
		iterator = TestUtilities.quickScan("if(123) comment \"succes\";");
		parser = new StatementParser(iterator, exceptions);
		
		If statement = parser.parseIfStatement();
		assertEquals(Predicate.RegularPredicate.class, statement.getPredicate().getClass());
		assertEquals(Statement.Comment.class, statement.getStatement().getClass());
	}
	
	@Test
	public void testIfElseStatement() throws SyntaxException {
		iterator = TestUtilities.quickScan("if(123) comment \"succes\"; else yield;");
		parser = new StatementParser(iterator, exceptions);
		
		IfElse statement = (IfElse) parser.parseIfStatement();
		assertEquals(Predicate.RegularPredicate.class, statement.getPredicate().getClass());
		assertEquals(Statement.Comment.class, statement.getStatement().getClass());
		assertEquals(Statement.Yield.class, statement.getElseStatement().getClass());
	}
	
	@Test
	public void testEchoEmbeddingStatement() throws SyntaxException {
		iterator = TestUtilities.quickScan("echo \"<\"text\">\";");
		parser = new StatementParser(iterator, exceptions);
		
		EchoEmbedding statement = (EchoEmbedding) parser.parseStatement();
		assertEquals(Embed.ExpressionEmbed.class, statement.getEmbedding().getEmbed().getClass());
	}
	
	@Test
	public void testEchoExpressionStatement() throws SyntaxException {
		iterator = TestUtilities.quickScan("echo 10;");
		parser = new StatementParser(iterator, exceptions);
		
		Echo statement = (Echo) parser.parseStatement();
		assertEquals(Expression.NatExpression.class, statement.getExpression().getClass());
	}
	
	@Test
	public void testEachStatement() throws SyntaxException {
		iterator = TestUtilities.quickScan("each(var1:10) comment \"test\";");
		parser = new StatementParser(iterator, exceptions);
		
		Each statement = parser.parseEachStatement();
		assertEquals("var1", statement.getVar().getToken().getLexeme().toString());
		assertEquals(Expression.NatExpression.class, statement.getExpression().getClass());
		assertEquals(Statement.Comment.class, statement.getStatement().getClass());
	}
	
	@Test
	public void testStatementCollection() throws SyntaxException {
		iterator = TestUtilities.quickScan("{ yield; comment \"text\"; markup1 markup2 var; }");
		parser = new StatementParser(iterator, exceptions);
		
		Block statement = parser.parseStatementCollection();
		assertEquals(3, statement.getStatements().size());
		assertEquals(Statement.Yield.class, statement.getStatements().get(0).getClass());
		assertEquals(Statement.Comment.class, statement.getStatements().get(1).getClass());
		assertEquals(Statement.MarkupExp.class, statement.getStatements().get(2).getClass());
	}
	
	@Test
	public void testLetStatement() throws SyntaxException {
		iterator = TestUtilities.quickScan("let var=100 in comment \"test\"; end");
		parser = new StatementParser(iterator, exceptions);
		
		Let statement = parser.parseLetStatement();
		assertEquals(1, statement.getAssignments().size());
		assertEquals(Assignment.VarBind.class, statement.getAssignments().get(0).getClass());
		assertEquals(1, statement.getAssignments().size());
		assertEquals(Statement.Comment.class, statement.getStatements().get(0).getClass());
	}
	
	@Test
	public void testCDataStatement() throws SyntaxException {
		iterator = TestUtilities.quickScan("cdata 10;");
		parser = new StatementParser(iterator, exceptions);
		
		CData statement = parser.parseCDataStatement();
		assertEquals(Expression.NatExpression.class, statement.getExpression().getClass());
	}
	
	@Test
	public void testCommentStatement() throws SyntaxException {
		iterator = TestUtilities.quickScan("comment \"OH NOES TEH HAXZOR\";");
		parser = new StatementParser(iterator, exceptions);
		
		Comment statement = parser.parseCommentStatement();
		assertEquals("OH NOES TEH HAXZOR", statement.getComment().getLiteral().toString());
	}
	
	@Test
	public void testYieldStatement() throws SyntaxException {
		iterator = TestUtilities.quickScan("yield;");
		parser = new StatementParser(iterator, exceptions);
		
		parser.parseYieldStatement();
		assertEquals(0, exceptions.size());
	}
	
	@Test
	public void testMarkupStatement() throws SyntaxException {
		iterator = TestUtilities.quickScan("func1;");
		parser = new StatementParser(iterator, exceptions);
		
		RegularMarkupStatement statement = (RegularMarkupStatement) parser.parseStatement();
		assertEquals("func1", statement.getMarkup().getDesignator().getIdentifier().getToken().getLexeme().toString());
	}
	
	@Test
	public void testIsMarkup() {
		
	}
	
	@Test
	public void testExpressionMarkupsStatement() throws SyntaxException {
		// Natural expression (easy example)
		iterator = TestUtilities.quickScan("func1 func2 123;");
		parser = new StatementParser(iterator, exceptions);
		
		MarkupExp natstm = (MarkupExp) parser.parseMarkupStatements();
		assertEquals(2, natstm.getMarkups().size());
		assertEquals(Expression.NatExpression.class, natstm.getExpression().getClass());
		
		// Var expression (resembles mark-up)
		iterator = TestUtilities.quickScan("func1 func2 var;");
		parser = new StatementParser(iterator, exceptions);
		
		MarkupExp varstm = (MarkupExp) parser.parseMarkupStatements();
		assertEquals(2, varstm.getMarkups().size());
		assertEquals(Expression.VarExpression.class, varstm.getExpression().getClass());
		
		// Text expression
		iterator = TestUtilities.quickScan("func1 func2 \"123\";");
		parser = new StatementParser(iterator, exceptions);
		
		MarkupExp estatement = (MarkupExp) parser.parseMarkupStatements();
		assertEquals(2, estatement.getMarkups().size());
		assertEquals(Expression.TextExpression.class, estatement.getExpression().getClass());
	}
	
	@Test
	public void testEmbeddingMarkupsStatement() throws SyntaxException {
		iterator = TestUtilities.quickScan("func1 func2 \"<123>\";");
		parser = new StatementParser(iterator, exceptions);
		
		MarkupEmbedding statement = (MarkupEmbedding) parser.parseMarkupStatements();
		assertEquals(2, statement.getMarkups().size());
		assertEquals(Embed.ExpressionEmbed.class, statement.getEmbedding().getEmbed().getClass());
	}
	
	@Test
	public void testStatementMarkupsStatement() throws SyntaxException {
		iterator = TestUtilities.quickScan("func1 func2 yield;;");
		parser = new StatementParser(iterator, exceptions);
		
		MarkupStat statement = (MarkupStat) parser.parseMarkupStatements();
		assertEquals(2, statement.getMarkups().size());
		assertEquals(Statement.Yield.class, statement.getStatement().getClass());
		
		// Markup statement collection
		iterator = TestUtilities.quickScan("markup { func1 func2 var; }");
		parser = new StatementParser(iterator, exceptions);
		
		MarkupStat cstatement = (MarkupStat) parser.parseMarkupStatements();
		assertEquals(1, cstatement.getMarkups().size());
		assertEquals(Statement.Block.class, cstatement.getStatement().getClass());
	}
	
	@Test
	public void testMarkupMarkupsStatement() throws SyntaxException {
		iterator = TestUtilities.quickScan("func1 func2 func3();");
		parser = new StatementParser(iterator, exceptions);
		
		MarkupMarkup statement = (MarkupMarkup) parser.parseMarkupStatements();
		assertEquals(2, statement.getMarkups().size());
	}
	
	@Test
	public void testCaveat() {
		// Mark-up
		parser = new StatementParser(TestUtilities.quickScan("p;"), exceptions);
		assertTrue(parser.isMarkup(1));
		
		// Mark-up, variable
		parser = new StatementParser(TestUtilities.quickScan("p p;"), exceptions);
		assertTrue(parser.isMarkup(1));
		assertFalse(parser.isMarkup(2));
		
		// Mark-up, mark-up
		parser = new StatementParser(TestUtilities.quickScan("p p();"), exceptions);
		assertTrue(parser.isMarkup(1));
		assertTrue(parser.isMarkup(2));
		
		// Markup, mark-up, natural
		parser = new StatementParser(TestUtilities.quickScan("p p 123;"), exceptions);
		assertTrue(parser.isMarkup(1));
		assertTrue(parser.isMarkup(2));
		assertFalse(parser.isMarkup(3));
	}

}