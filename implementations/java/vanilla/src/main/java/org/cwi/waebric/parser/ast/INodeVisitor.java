package org.cwi.waebric.parser.ast;

import org.cwi.waebric.parser.ast.basic.*;
import org.cwi.waebric.parser.ast.expression.*;
import org.cwi.waebric.parser.ast.markup.*;
import org.cwi.waebric.parser.ast.module.*;
import org.cwi.waebric.parser.ast.statement.*;
import org.cwi.waebric.parser.ast.statement.embedding.*;
import org.cwi.waebric.parser.ast.statement.predicate.*;
import org.cwi.waebric.parser.ast.module.site.*;
import org.cwi.waebric.parser.ast.module.function.*;

/**
 * Used visitor pattern to remove the need to cast everywhere during
 * compilation and interpretation.
 * @author Jeroen van Schagen
 * @date 11-06-2009
 */
public interface INodeVisitor {

	// Module
	public void visit(Module module);
	public void visit(ModuleId id);
	public void visit(Import imprt);
	
	// Site
	public void visit(Site site);
	public void visit(Mappings mappings);
	public void visit(Mapping mapping);
	public void visit(Path path);
	
	// Function
	public void visit(FunctionDef function);
	public void visit(Formals.RegularFormal formals);
	public void visit(Formals.EmptyFormal formals);
	
	// Statements
	public void visit(Statement.If statement);
	public void visit(Statement.IfElse statement);
	public void visit(Statement.Block statement);
	public void visit(Statement.CData statement);
	public void visit(Statement.Comment statement);
	public void visit(Statement.Each statement);
	public void visit(Statement.Echo statement);
	public void visit(Statement.EchoEmbedding statement);
	public void visit(Statement.Let statement);
	public void visit(Statement.Yield statement);
	
	public void visit(Statement.MarkupStatement statement);
	public void visit(Statement.MarkupsMarkup statement);
	public void visit(Statement.MarkupsExpression statement);
	public void visit(Statement.MarkupsStatement statement);
	public void visit(Statement.MarkupsEmbedding statement);
	
	public void visit(Assignment.FuncBind bind);
	public void visit(Assignment.VarBind bind);
	
	// Predicate
	public void visit(Predicate.RegularPredicate predicate);
	public void visit(Predicate.And predicate);
	public void visit(Predicate.Is predicate);
	public void visit(Predicate.Not predicate);
	public void visit(Predicate.Or predicate);
	public void visit(Type type);
	
	// Embedding
	public void visit(Embedding embedding);
	public void visit(Embed.ExpressionEmbed embed);
	public void visit(Embed.MarkupEmbed embed);
	public void visit(PreText text);
	public void visit(MidText text);
	public void visit(PostText text);
	public void visit(TextTail.MidTail tail);
	public void visit(TextTail.PostTail tail);
	
	// Mark-ups
	public void visit(Markup.Call markup);
	public void visit(Markup.Tag markup);
	public void visit(Designator designator);
	public void visit(Attributes attributes);
	public void visit(Attribute.ClassAttribute attribute);
	public void visit(Attribute.IdAttribute attribute);
	public void visit(Attribute.NameAttribute attribute);
	public void visit(Attribute.TypeAttribute attribute);
	public void visit(Attribute.WidthAttribute attribute);
	public void visit(Attribute.WidthHeightAttribute attribute);
	public void visit(Arguments arguments);
	public void visit(Argument argument);
	
	// Expressions
	public void visit(Expression.CatExpression expression);
	public void visit(Expression.Field expression);
	public void visit(Expression.ListExpression expression);
	public void visit(Expression.NatExpression expression);
	public void visit(Expression.RecordExpression expression);
	public void visit(Expression.SymbolExpression expression);
	public void visit(Expression.TextExpression expression);
	public void visit(Expression.VarExpression expression);
	public void visit(KeyValuePair pair);
	public void visit(Text text);

	// Basic
	public void visit(IdCon id);
	public void visit(NatCon nat);
	public void visit(StrCon str);
	public void visit(SymbolCon symbol);
	
	// Generic
	public void visit(AbstractSyntaxNodeList<?> list);
	public void visit(TokenNode node);
	
}