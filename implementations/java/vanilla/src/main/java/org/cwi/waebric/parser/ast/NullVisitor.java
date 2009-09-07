package org.cwi.waebric.parser.ast;

import org.cwi.waebric.parser.ast.basic.*;
import org.cwi.waebric.parser.ast.expression.KeyValuePair;
import org.cwi.waebric.parser.ast.expression.Expression.*;
import org.cwi.waebric.parser.ast.markup.*;
import org.cwi.waebric.parser.ast.markup.Markup.Call;
import org.cwi.waebric.parser.ast.markup.Markup.Tag;
import org.cwi.waebric.parser.ast.module.*;
import org.cwi.waebric.parser.ast.module.function.FunctionDef;
import org.cwi.waebric.parser.ast.module.function.Formals.EmptyFormal;
import org.cwi.waebric.parser.ast.module.function.Formals.RegularFormal;
import org.cwi.waebric.parser.ast.module.site.*;
import org.cwi.waebric.parser.ast.statement.Assignment.FuncBind;
import org.cwi.waebric.parser.ast.statement.Assignment.VarBind;
import org.cwi.waebric.parser.ast.statement.Statement.*;
import org.cwi.waebric.parser.ast.statement.embedding.*;
import org.cwi.waebric.parser.ast.statement.embedding.TextTail.MidTail;
import org.cwi.waebric.parser.ast.statement.embedding.TextTail.PostTail;
import org.cwi.waebric.parser.ast.statement.predicate.Type;
import org.cwi.waebric.parser.ast.statement.predicate.Predicate.*;

/**
 * Default implementation for node visiting, all visit functions delegate the
 * visit request to children nodes. Only extend visits that are relevant for
 * your implementations.
 * 
 * @author Jeroen van Schagen
 * @date 11-06-2009
 */
public abstract class NullVisitor<T> implements INodeVisitor<T> {

	public T visit(Module module) {
		visitChildren(module);
		return null;
	}

	public T visit(ModuleId id) {
		visitChildren(id);
		return null;
	}

	public T visit(Import imprt) {
		visitChildren(imprt);
		return null;
	}

	public T visit(Site site) {
		visitChildren(site);
		return null;
	}

	public T visit(Mappings mappings) {
		visitChildren(mappings);
		return null;
	}

	public T visit(Mapping mapping) {
		visitChildren(mapping);
		return null;
	}

	public T visit(Path path) {
		visitChildren(path);
		return null;
	}

	public T visit(FunctionDef function) {
		visitChildren(function);
		return null;
	}

	public T visit(RegularFormal formals) {
		visitChildren(formals);
		return null;
	}

	public T visit(EmptyFormal formals) {
		visitChildren(formals);
		return null;
	}

	public T visit(If statement) {
		visitChildren(statement);
		return null;
	}

	public T visit(IfElse statement) {
		visitChildren(statement);
		return null;
	}

	public T visit(Block statement) {
		visitChildren(statement);
		return null;
	}

	public T visit(CData statement) {
		visitChildren(statement);
		return null;
	}

	public T visit(Comment statement) {
		visitChildren(statement);
		return null;
	}

	public T visit(Each statement) {
		visitChildren(statement);
		return null;
	}

	public T visit(Echo statement) {
		visitChildren(statement);
		return null;
	}

	public T visit(EchoEmbedding statement) {
		visitChildren(statement);
		return null;
	}

	public T visit(Let statement) {
		visitChildren(statement);
		return null;
	}

	public T visit(Yield statement) {
		visitChildren(statement);
		return null;
	}

	public T visit(MarkupStatement statement) {
		visitChildren(statement);
		return null;
	}

	public T visit(MarkupsMarkup statement) {
		visitChildren(statement);
		return null;
	}

	public T visit(MarkupsExpression statement) {
		visitChildren(statement);
		return null;
	}

	public T visit(MarkupsStatement statement) {
		visitChildren(statement);
		return null;
	}

	public T visit(MarkupsEmbedding statement) {
		visitChildren(statement);
		return null;
	}

	public T visit(FuncBind bind) {
		visitChildren(bind);
		return null;
	}

	public T visit(VarBind bind) {
		visitChildren(bind);
		return null;
	}

	public T visit(RegularPredicate predicate) {
		visitChildren(predicate);
		return null;
	}

	public T visit(And predicate) {
		visitChildren(predicate);
		return null;
	}

	public T visit(Is predicate) {
		visitChildren(predicate);
		return null;
	}

	public T visit(Not predicate) {
		visitChildren(predicate);
		return null;
	}

	public T visit(Or predicate) {
		visitChildren(predicate);
		return null;
	}

	public T visit(Type.ListType type) {
		visitChildren(type);
		return null;
	}

	public T visit(Type.RecordType type) {
		visitChildren(type);
		return null;
	}

	public T visit(Type.StringType type) {
		visitChildren(type);
		return null;
	}

	public T visit(Embedding embedding) {
		visitChildren(embedding);
		return null;
	}

	public T visit(Embed.ExpressionEmbed embed) {
		visitChildren(embed);
		return null;
	}

	public T visit(Embed.MarkupEmbed embed) {
		visitChildren(embed);
		return null;
	}

	public T visit(PreText text) {
		visitChildren(text);
		return null;
	}

	public T visit(MidText text) {
		visitChildren(text);
		return null;
	}

	public T visit(PostText text) {
		visitChildren(text);
		return null;
	}

	public T visit(MidTail tail) {
		visitChildren(tail);
		return null;
	}

	public T visit(PostTail tail) {
		visitChildren(tail);
		return null;
	}

	public T visit(Call markup) {
		visitChildren(markup);
		return null;
	}

	public T visit(Tag markup) {
		visitChildren(markup);
		return null;
	}

	public T visit(Designator designator) {
		visitChildren(designator);
		return null;
	}

	public T visit(Attributes attributes) {
		visitChildren(attributes);
		return null;
	}

	public T visit(Attribute.IdAttribute attribute) {
		visitChildren(attribute);
		return null;
	}

	public T visit(Attribute.NameAttribute attribute) {
		visitChildren(attribute);
		return null;
	}

	public T visit(Attribute.TypeAttribute attribute) {
		visitChildren(attribute);
		return null;
	}

	public T visit(Attribute.WidthAttribute attribute) {
		visitChildren(attribute);
		return null;
	}

	public T visit(Attribute.WidthHeightAttribute attribute) {
		visitChildren(attribute);
		return null;
	}

	public T visit(Attribute.ClassAttribute attribute) {
		visitChildren(attribute);
		return null;
	}

	public T visit(Arguments arguments) {
		visitChildren(arguments);
		return null;
	}

	public T visit(Argument argument) {
		visitChildren(argument);
		return null;
	}

	public T visit(CatExpression expression) {
		visitChildren(expression);
		return null;
	}

	public T visit(Field expression) {
		visitChildren(expression);
		return null;
	}

	public T visit(ListExpression expression) {
		visitChildren(expression);
		return null;
	}

	public T visit(NatExpression expression) {
		visitChildren(expression);
		return null;
	}

	public T visit(RecordExpression expression) {
		visitChildren(expression);
		return null;
	}

	public T visit(SymbolExpression expression) {
		visitChildren(expression);
		return null;
	}

	public T visit(TextExpression expression) {
		visitChildren(expression);
		return null;
	}

	public T visit(VarExpression expression) {
		visitChildren(expression);
		return null;
	}

	public T visit(KeyValuePair pair) {
		visitChildren(pair);
		return null;
	}

	public T visit(Text text) {
		visitChildren(text);
		return null;
	}

	public T visit(IdCon id) {
		visitChildren(id);
		return null;
	}

	public T visit(NatCon nat) {
		visitChildren(nat);
		return null;
	}

	public T visit(StrCon str) {
		visitChildren(str);
		return null;
	}

	public T visit(SymbolCon symbol) {
		visitChildren(symbol);
		return null;
	}

	public T visit(SyntaxNodeList<?> list) {
		visitChildren(list);
		return null;
	}

	public T visit(TokenNode node) {
		visitChildren(node);
		return null;
	}

	/**
	 * Delegate visit request to children of node.
	 * 
	 * @param node
	 *            Node
	 */
	private void visitChildren(SyntaxNode node) {
		for (SyntaxNode child : node.getChildren()) {
			child.accept(this);
		}
	}

}