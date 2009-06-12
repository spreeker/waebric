package org.cwi.waebric.parser.ast;

import org.cwi.waebric.parser.ast.basic.*;
import org.cwi.waebric.parser.ast.expression.*;
import org.cwi.waebric.parser.ast.markup.*;
import org.cwi.waebric.parser.ast.module.*;
import org.cwi.waebric.parser.ast.statement.*;
import org.cwi.waebric.parser.ast.statement.embedding.*;
import org.cwi.waebric.parser.ast.statement.predicate.*;
import org.cwi.waebric.parser.ast.token.CharacterLiteral;
import org.cwi.waebric.parser.ast.token.IntegerLiteral;
import org.cwi.waebric.parser.ast.token.StringLiteral;
import org.cwi.waebric.parser.ast.token.TokenNode;
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
	public void visit(Modules modules, Object[] args);
	public void visit(Module module, Object[] args);
	public void visit(ModuleId id, Object[] args);
	public void visit(Import imprt, Object[] args);
	
	// Site
	public void visit(Site site, Object[] args);
	public void visit(Mappings mappings, Object[] args);
	public void visit(Mapping mapping, Object[] args);
	public void visit(Path path, Object[] args);
	public void visit(PathElement element, Object[] args);
	public void visit(Directory directory, Object[] args);
	public void visit(DirName name, Object[] args);
	public void visit(FileName name, Object[] args);
	public void visit(FileExt ext, Object[] args);
	
	// Function
	public void visit(FunctionDef function, Object[] args);
	public void visit(Formals.RegularFormal formals, Object[] args);
	public void visit(Formals.EmptyFormal formals, Object[] args);
	
	// Statements
	public void visit(Statement.If statement, Object[] args);
	public void visit(Statement.IfElse statement, Object[] args);
	public void visit(Statement.Block statement, Object[] args);
	public void visit(Statement.CData statement, Object[] args);
	public void visit(Statement.Comment statement, Object[] args);
	public void visit(Statement.Each statement, Object[] args);
	public void visit(Statement.Echo statement, Object[] args);
	public void visit(Statement.EchoEmbedding statement, Object[] args);
	public void visit(Statement.Let statement, Object[] args);
	public void visit(Statement.Yield statement, Object[] args);
	
	public void visit(Statement.RegularMarkupStatement statement, Object[] args);
	public void visit(Statement.MarkupMarkup statement, Object[] args);
	public void visit(Statement.MarkupExp statement, Object[] args);
	public void visit(Statement.MarkupStat statement, Object[] args);
	public void visit(Statement.MarkupEmbedding statement, Object[] args);
	
	public void visit(Assignment.FuncBind bind, Object[] args);
	public void visit(Assignment.VarBind bind, Object[] args);
	
	// Predicate
	public void visit(Predicate.RegularPredicate predicate, Object[] args);
	public void visit(Predicate.And predicate, Object[] args);
	public void visit(Predicate.Is predicate, Object[] args);
	public void visit(Predicate.Not predicate, Object[] args);
	public void visit(Predicate.Or predicate, Object[] args);
	public void visit(Type type, Object[] args);
	
	// Embedding
	public void visit(Embedding embedding, Object[] args);
	public void visit(Embed embed, Object[] args);
	public void visit(PreText text, Object[] args);
	public void visit(MidText text, Object[] args);
	public void visit(PostText text, Object[] args);
	public void visit(TextTail.MidTail tail, Object[] args);
	public void visit(TextTail.PostTail tail, Object[] args);
	
	// Mark-ups
	public void visit(Markup.Call markup, Object[] args);
	public void visit(Markup.Tag markup, Object[] args);
	public void visit(Designator designator, Object[] args);
	public void visit(Attributes attributes, Object[] args);
	public void visit(Attribute attribute, Object[] args);
	public void visit(Arguments arguments, Object[] args);
	public void visit(Argument argument, Object[] args);
	
	// Expressions
	public void visit(Expression.CatExpression expression, Object[] args);
	public void visit(Expression.Field expression, Object[] args);
	public void visit(Expression.ListExpression expression, Object[] args);
	public void visit(Expression.NatExpression expression, Object[] args);
	public void visit(Expression.RecordExpression expression, Object[] args);
	public void visit(Expression.SymbolExpression expression, Object[] args);
	public void visit(Expression.TextExpression expression, Object[] args);
	public void visit(Expression.VarExpression expression, Object[] args);
	public void visit(KeyValuePair pair, Object[] args);
	public void visit(Text text, Object[] args);

	// Basic
	public void visit(IdCon id, Object[] args);
	public void visit(NatCon nat, Object[] args);
	public void visit(StrCon str, Object[] args);
	public void visit(SymbolCon symbol, Object[] args);
	
	// Generic
	public void visit(NodeList<?> list, Object[] args);
	public void visit(SeparatedNodeList<?> list, Object[] args);
	public void visit(CharacterLiteral literal, Object[] args);
	public void visit(IntegerLiteral literal, Object[] args);
	public void visit(StringLiteral literal, Object[] args);
	public void visit(TokenNode node, Object[] args);
	
}