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
 * Default implementation for node visiting, all visit functions contain
 * an empty statement collection. Extend this class to prevent massive 
 * implementations of specific visitors.
 * @author Jeroen van Schagen
 * @date 11-06-2009
 */
public class DefaultNodeVisitor implements INodeVisitor {

	public void visit(Modules modules) { }
	public void visit(Module module) { }
	public void visit(ModuleId id) { }
	public void visit(Import imprt) { }
	public void visit(Site site) { }
	public void visit(Mappings mappings) { }
	public void visit(Mapping mapping) { }
	public void visit(Path path) { }
	public void visit(PathElement element) { }
	public void visit(Directory directory) { }
	public void visit(DirName name) { }
	public void visit(FileName name) { }
	public void visit(FileExt ext) { }
	public void visit(FunctionDef function) { }
	public void visit(RegularFormal formals) { }
	public void visit(EmptyFormal formals) { }
	public void visit(If statement) { }
	public void visit(IfElse statement) { }
	public void visit(Block statement) { }
	public void visit(CData statement) { }
	public void visit(Comment statement) { }
	public void visit(Each statement) { }
	public void visit(Echo statement) { }
	public void visit(EchoEmbedding statement) { }
	public void visit(Let statement) { }
	public void visit(Yield statement) { }
	public void visit(RegularMarkupStatement statement) { }
	public void visit(MarkupMarkup statement) { }
	public void visit(MarkupExp statement) { }
	public void visit(MarkupStat statement) { }
	public void visit(MarkupEmbedding statement) { }
	public void visit(FuncBind bind) { }
	public void visit(VarBind bind) { }
	public void visit(RegularPredicate predicate) { }
	public void visit(And predicate) { }
	public void visit(Is predicate) { }
	public void visit(Not predicate) { }
	public void visit(Or predicate) { }
	public void visit(Type type) { }
	public void visit(Embedding embedding) { }
	public void visit(Embed embed) { }
	public void visit(PreText text) { }
	public void visit(MidText text) { }
	public void visit(PostText text) { }
	public void visit(MidTail tail) { }
	public void visit(PostTail tail) { }
	public void visit(Call markup) { }
	public void visit(Tag markup) { }
	public void visit(Designator designator) { }
	public void visit(Attributes attributes) { }
	public void visit(Attribute attribute) { }
	public void visit(Arguments arguments) { }
	public void visit(Argument argument) { }
	public void visit(CatExpression expression) { }
	public void visit(Field expression) { }
	public void visit(ListExpression expression) { }
	public void visit(NatExpression expression) { }
	public void visit(RecordExpression expression) { }
	public void visit(SymbolExpression expression) { }
	public void visit(TextExpression expression) { }
	public void visit(VarExpression expression) { }
	public void visit(KeyValuePair pair) { }
	public void visit(Text text) { }
	public void visit(IdCon id) { }
	public void visit(NatCon nat) { }
	public void visit(StrCon str) { }
	public void visit(SymbolCon symbol) { }
	public void visit(NodeList<?> list) { }
	public void visit(SeparatedNodeList<?> list) { }
	public void visit(CharacterLiteral literal) { }
	public void visit(IntegerLiteral literal) { }
	public void visit(StringLiteral literal) { }
	public void visit(TokenNode node) { }

}