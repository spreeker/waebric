package org.cwi.waebric.parser.ast;

import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.ast.module.function.Formals;
import org.cwi.waebric.parser.ast.module.function.FunctionDef;
import org.cwi.waebric.parser.ast.statement.Assignment;
import org.cwi.waebric.parser.ast.statement.Statement;

/**
 * Used visitor pattern to remove the need to cast everywhere during
 * compilation and interpretation.
 * @author Jeroen van Schagen
 * @date 11-06-2009
 */
public interface INodeVisitor {
	
	// Default reference
	public void visit(AbstractSyntaxNode node, Object[] args);

	// Functions
	public void visit(FunctionDef function, Object[] args);
	public void visit(Formals.RegularFormal function, Object[] args);
	
	// Statements
	public void visit(Statement.If statement, Object[] args);
	public void visit(Statement.IfElse statement, Object[] args);
	public void visit(Statement.RegularMarkupStatement statement, Object[] args);
	public void visit(Statement.Block statement, Object[] args);
	public void visit(Statement.CData statement, Object[] args);
	public void visit(Statement.Comment statement, Object[] args);
	public void visit(Statement.Each statement, Object[] args);
	public void visit(Statement.Echo statement, Object[] args);
	public void visit(Statement.EchoEmbedding statement, Object[] args);
	public void visit(Statement.Let statement, Object[] args);
	public void visit(Statement.MarkupEmbedding statement, Object[] args);
	public void visit(Statement.MarkupExp statement, Object[] args);
	public void visit(Statement.MarkupMarkup statement, Object[] args);
	public void visit(Statement.MarkupStat statement, Object[] args);
	public void visit(Statement.Yield statement, Object[] args);
	
	public void visit(Assignment.FuncBind bind, Object[] args);
	public void visit(Assignment.VarBind bind, Object[] args);
	
	// Mark-ups
	public void visit(Markup.Call markup, Object[] args);
	public void visit(Markup.Tag markup, Object[] args);
	
	// Expressions
	public void visit(Expression.CatExpression expression, Object[] args);
	public void visit(Expression.Field expression, Object[] args);
	public void visit(Expression.ListExpression expression, Object[] args);
	public void visit(Expression.NatExpression expression, Object[] args);
	public void visit(Expression.RecordExpression expression, Object[] args);
	public void visit(Expression.SymbolExpression expression, Object[] args);
	public void visit(Expression.TextExpression expression, Object[] args);
	public void visit(Expression.VarExpression expression, Object[] args);
	
}