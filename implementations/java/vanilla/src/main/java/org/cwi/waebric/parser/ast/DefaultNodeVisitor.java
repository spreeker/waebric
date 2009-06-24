package org.cwi.waebric.parser.ast;

import org.cwi.waebric.parser.ast.basic.*;
import org.cwi.waebric.parser.ast.expression.KeyValuePair;
import org.cwi.waebric.parser.ast.expression.Text;
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
import org.cwi.waebric.parser.ast.token.CharacterLiteral;
import org.cwi.waebric.parser.ast.token.IntegerLiteral;
import org.cwi.waebric.parser.ast.token.StringLiteral;
import org.cwi.waebric.parser.ast.token.TokenNode;

/**
 * Default implementation for node visiting, all visit functions delegate
 * the visit request to children nodes. Only extend visits that are
 * relevant for your implementations.
 * @author Jeroen van Schagen
 * @date 11-06-2009
 */
public class DefaultNodeVisitor implements INodeVisitor {

	public void visit(Modules modules) { visitChildren(modules); }
	public void visit(Module module) { visitChildren(module); }
	public void visit(ModuleId id) { visitChildren(id); }
	public void visit(Import imprt) { visitChildren(imprt); }
	public void visit(Site site) { visitChildren(site); }
	public void visit(Mappings mappings) { visitChildren(mappings); }
	public void visit(Mapping mapping) { visitChildren(mapping); }
	public void visit(Path path) { visitChildren(path); }
	public void visit(PathElement element) { visitChildren(element); }
	public void visit(Directory directory) { visitChildren(directory); }
	public void visit(DirName name) { visitChildren(name); }
	public void visit(FileName name) { visitChildren(name); }
	public void visit(FileExt ext) { visitChildren(ext); }
	public void visit(FunctionDef function) { visitChildren(function); }
	public void visit(RegularFormal formals) { visitChildren(formals); }
	public void visit(EmptyFormal formals) { visitChildren(formals); }
	public void visit(If statement) { visitChildren(statement); }
	public void visit(IfElse statement) { visitChildren(statement); }
	public void visit(Block statement) { visitChildren(statement); }
	public void visit(CData statement) { visitChildren(statement); }
	public void visit(Comment statement) { visitChildren(statement); }
	public void visit(Each statement) { visitChildren(statement); }
	public void visit(Echo statement) { visitChildren(statement); }
	public void visit(EchoEmbedding statement) { visitChildren(statement); }
	public void visit(Let statement) { visitChildren(statement); }
	public void visit(Yield statement) { visitChildren(statement); }
	public void visit(RegularMarkupStatement statement) { visitChildren(statement); }
	public void visit(MarkupMarkup statement) { visitChildren(statement); }
	public void visit(MarkupExp statement) { visitChildren(statement); }
	public void visit(MarkupStat statement) { visitChildren(statement); }
	public void visit(MarkupEmbedding statement) { visitChildren(statement); }
	public void visit(FuncBind bind) { visitChildren(bind); }
	public void visit(VarBind bind) { visitChildren(bind); }
	public void visit(RegularPredicate predicate) { visitChildren(predicate); }
	public void visit(And predicate) { visitChildren(predicate); }
	public void visit(Is predicate) { visitChildren(predicate); }
	public void visit(Not predicate) { visitChildren(predicate); }
	public void visit(Or predicate) { visitChildren(predicate); }
	public void visit(Type type) { visitChildren(type); }
	public void visit(Embedding embedding) { visitChildren(embedding); }
	public void visit(Embed.ExpressionEmbed embed) { visitChildren(embed); }
	public void visit(Embed.MarkupEmbed embed) { visitChildren(embed); }
	public void visit(PreText text) { visitChildren(text); }
	public void visit(MidText text) { visitChildren(text); }
	public void visit(PostText text) { visitChildren(text); }
	public void visit(MidTail tail) { visitChildren(tail); }
	public void visit(PostTail tail) { visitChildren(tail); }
	public void visit(Call markup) { visitChildren(markup); }
	public void visit(Tag markup) { visitChildren(markup); }
	public void visit(Designator designator) { visitChildren(designator); }
	public void visit(Attributes attributes) { visitChildren(attributes); }
	public void visit(Attribute.IdAttribute attribute) { visitChildren(attribute); }
	public void visit(Attribute.NameAttribute attribute) { visitChildren(attribute); }
	public void visit(Attribute.TypeAttribute attribute) { visitChildren(attribute); }
	public void visit(Attribute.WidthAttribute attribute) { visitChildren(attribute); }
	public void visit(Attribute.WidthHeightAttribute attribute) { visitChildren(attribute); }
	public void visit(Attribute.ClassAttribute attribute) { visitChildren(attribute); }
	public void visit(Arguments arguments) { visitChildren(arguments); }
	public void visit(Argument argument) { visitChildren(argument); }
	public void visit(CatExpression expression) { visitChildren(expression); }
	public void visit(Field expression) { visitChildren(expression); }
	public void visit(ListExpression expression) { visitChildren(expression); }
	public void visit(NatExpression expression) { visitChildren(expression); }
	public void visit(RecordExpression expression) { visitChildren(expression); }
	public void visit(SymbolExpression expression) { visitChildren(expression); }
	public void visit(TextExpression expression) { visitChildren(expression); }
	public void visit(VarExpression expression) { visitChildren(expression); }
	public void visit(KeyValuePair pair) { visitChildren(pair); }
	public void visit(Text text) { visitChildren(text); }
	public void visit(IdCon id) { visitChildren(id); }
	public void visit(NatCon nat) { visitChildren(nat); }
	public void visit(StrCon str) { visitChildren(str); }
	public void visit(SymbolCon symbol) { visitChildren(symbol); }
	public void visit(NodeList<?> list) { visitChildren(list); }
	public void visit(SeparatedNodeList<?> list) { visitChildren(list); }
	public void visit(CharacterLiteral literal) { visitChildren(literal); }
	public void visit(IntegerLiteral literal) { visitChildren(literal); }
	public void visit(StringLiteral literal) { visitChildren(literal); }
	public void visit(TokenNode node) { visitChildren(node); }
	
	/**
	 * Delegate visit request to children of node.
	 * @param node Node
	 */
	private void visitChildren(AbstractSyntaxNode node) {
		for(AbstractSyntaxNode child: node.getChildren()) {
			child.accept(this);
		}
	}

}