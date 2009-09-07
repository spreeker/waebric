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
 * Used visitor pattern to remove the need to cast everywhere during compilation
 * and interpretation.
 * 
 * @author Jeroen van Schagen
 * @date 11-06-2009
 */
public interface INodeVisitor<T> {

	// Module
	public T visit(Module module);

	public T visit(ModuleId id);

	public T visit(Import imprt);

	// Site
	public T visit(Site site);

	public T visit(Mappings mappings);

	public T visit(Mapping mapping);

	public T visit(Path path);

	// Function
	public T visit(FunctionDef function);

	public T visit(Formals.RegularFormal formals);

	public T visit(Formals.EmptyFormal formals);

	// Statements
	public T visit(Statement.If statement);

	public T visit(Statement.IfElse statement);

	public T visit(Statement.Block statement);

	public T visit(Statement.CData statement);

	public T visit(Statement.Comment statement);

	public T visit(Statement.Each statement);

	public T visit(Statement.Echo statement);

	public T visit(Statement.EchoEmbedding statement);

	public T visit(Statement.Let statement);

	public T visit(Statement.Yield statement);

	public T visit(Statement.MarkupStatement statement);

	public T visit(Statement.MarkupsMarkup statement);

	public T visit(Statement.MarkupsExpression statement);

	public T visit(Statement.MarkupsStatement statement);

	public T visit(Statement.MarkupsEmbedding statement);

	public T visit(Assignment.FuncBind bind);

	public T visit(Assignment.VarBind bind);

	// Predicate
	public T visit(Predicate.RegularPredicate predicate);

	public T visit(Predicate.And predicate);

	public T visit(Predicate.Is predicate);

	public T visit(Predicate.Not predicate);

	public T visit(Predicate.Or predicate);

	public T visit(Type.ListType type);

	public T visit(Type.RecordType type);

	public T visit(Type.StringType type);

	// Embedding
	public T visit(Embedding embedding);

	public T visit(Embed.ExpressionEmbed embed);

	public T visit(Embed.MarkupEmbed embed);

	public T visit(PreText text);

	public T visit(MidText text);

	public T visit(PostText text);

	public T visit(TextTail.MidTail tail);

	public T visit(TextTail.PostTail tail);

	// Mark-ups
	public T visit(Markup.Call markup);

	public T visit(Markup.Tag markup);

	public T visit(Designator designator);

	public T visit(Attributes attributes);

	public T visit(Attribute.ClassAttribute attribute);

	public T visit(Attribute.IdAttribute attribute);

	public T visit(Attribute.NameAttribute attribute);

	public T visit(Attribute.TypeAttribute attribute);

	public T visit(Attribute.WidthAttribute attribute);

	public T visit(Attribute.WidthHeightAttribute attribute);

	public T visit(Arguments arguments);

	public T visit(Argument argument);

	// Expressions
	public T visit(Expression.CatExpression expression);

	public T visit(Expression.Field expression);

	public T visit(Expression.ListExpression expression);

	public T visit(Expression.NatExpression expression);

	public T visit(Expression.RecordExpression expression);

	public T visit(Expression.SymbolExpression expression);

	public T visit(Expression.TextExpression expression);

	public T visit(Expression.VarExpression expression);

	public T visit(KeyValuePair pair);

	public T visit(Text text);

	// Basic
	public T visit(IdCon id);

	public T visit(NatCon nat);

	public T visit(StrCon str);

	public T visit(SymbolCon symbol);

	// Generic
	public T visit(SyntaxNodeList<?> list);

	public T visit(TokenNode node);

}