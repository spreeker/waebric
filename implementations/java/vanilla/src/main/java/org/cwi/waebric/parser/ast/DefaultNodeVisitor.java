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

	public void visit(Modules modules, Object[] args) { }
	public void visit(Module module, Object[] args) { }
	public void visit(ModuleId id, Object[] args) { }
	public void visit(Import imprt, Object[] args) { }
	public void visit(Site site, Object[] args) { }
	public void visit(Mappings mappings, Object[] args) { }
	public void visit(Mapping mapping, Object[] args) { }
	public void visit(Path path, Object[] args) { }
	public void visit(PathElement element, Object[] args) { }
	public void visit(Directory directory, Object[] args) { }
	public void visit(DirName name, Object[] args) { }
	public void visit(FileName name, Object[] args) { }
	public void visit(FileExt ext, Object[] args) { }
	public void visit(FunctionDef function, Object[] args) { }
	public void visit(RegularFormal formals, Object[] args) { }
	public void visit(EmptyFormal formals, Object[] args) { }
	public void visit(If statement, Object[] args) { }
	public void visit(IfElse statement, Object[] args) { }
	public void visit(Block statement, Object[] args) { }
	public void visit(CData statement, Object[] args) { }
	public void visit(Comment statement, Object[] args) { }
	public void visit(Each statement, Object[] args) { }
	public void visit(Echo statement, Object[] args) { }
	public void visit(EchoEmbedding statement, Object[] args) { }
	public void visit(Let statement, Object[] args) { }
	public void visit(Yield statement, Object[] args) { }
	public void visit(RegularMarkupStatement statement, Object[] args) { }
	public void visit(MarkupMarkup statement, Object[] args) { }
	public void visit(MarkupExp statement, Object[] args) { }
	public void visit(MarkupStat statement, Object[] args) { }
	public void visit(MarkupEmbedding statement, Object[] args) { }
	public void visit(FuncBind bind, Object[] args) { }
	public void visit(VarBind bind, Object[] args) { }
	public void visit(RegularPredicate predicate, Object[] args) { }
	public void visit(And predicate, Object[] args) { }
	public void visit(Is predicate, Object[] args) { }
	public void visit(Not predicate, Object[] args) { }
	public void visit(Or predicate, Object[] args) { }
	public void visit(Type type, Object[] args) { }
	public void visit(Embedding embedding, Object[] args) { }
	public void visit(Embed embed, Object[] args) { }
	public void visit(PreText text, Object[] args) { }
	public void visit(MidText text, Object[] args) { }
	public void visit(PostText text, Object[] args) { }
	public void visit(MidTail tail, Object[] args) { }
	public void visit(PostTail tail, Object[] args) { }
	public void visit(Call markup, Object[] args) { }
	public void visit(Tag markup, Object[] args) { }
	public void visit(Designator designator, Object[] args) { }
	public void visit(Attributes attributes, Object[] args) { }
	public void visit(Attribute attribute, Object[] args) { }
	public void visit(Arguments arguments, Object[] args) { }
	public void visit(Argument argument, Object[] args) { }
	public void visit(CatExpression expression, Object[] args) { }
	public void visit(Field expression, Object[] args) { }
	public void visit(ListExpression expression, Object[] args) { }
	public void visit(NatExpression expression, Object[] args) { }
	public void visit(RecordExpression expression, Object[] args) { }
	public void visit(SymbolExpression expression, Object[] args) { }
	public void visit(TextExpression expression, Object[] args) { }
	public void visit(VarExpression expression, Object[] args) { }
	public void visit(KeyValuePair pair, Object[] args) { }
	public void visit(Text text, Object[] args) { }
	public void visit(IdCon id, Object[] args) { }
	public void visit(NatCon nat, Object[] args) { }
	public void visit(StrCon str, Object[] args) { }
	public void visit(SymbolCon symbol, Object[] args) { }
	public void visit(NodeList<?> list, Object[] args) { }
	public void visit(SeparatedNodeList<?> list, Object[] args) { }
	public void visit(CharacterLiteral literal, Object[] args) { }
	public void visit(IntegerLiteral literal, Object[] args) { }
	public void visit(StringLiteral literal, Object[] args) { }
	public void visit(TokenNode node, Object[] args) { }

}