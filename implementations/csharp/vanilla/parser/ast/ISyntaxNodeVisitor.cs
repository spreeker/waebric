using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Parser.Ast.Module;
using Parser.Ast.Site;
using Parser.Ast.Functions;
using Parser.Ast.Expressions;
using Parser.Ast.Markup;
using Parser.Ast.Predicates;
using Parser.Ast.Statements;
using Parser.Ast.Embedding;

namespace Parser.Ast
{
    /// <summary>
    /// Visitor which implements an visitor pattern
    /// </summary>
    public interface ISyntaxNodeVisitor
    {
        //Module nodes
        void Visit(Module.Module module);
        void Visit(ModuleId module);
        void Visit(Import import);

        //Site nodes
        void Visit(Site.Site site);
        void Visit(Mapping mapping);
        void Visit(Directory directory);
        void Visit(DirName dirName);
        void Visit(FileExt fileExt);
        void Visit(FileName fileName);
        void Visit(Path path);
        void Visit(PathElement pathElement);

        //FunctionDefinition nodes
        void Visit(FunctionDefinition functionDefinition);
        void Visit(Formal formal);

        //Statement nodes
        void Visit(BlockStatement statement);
        void Visit(CdataStatement statement);
        void Visit(CommentStatement statement);
        void Visit(EachStatement statement);
        void Visit(EchoEmbeddingStatement statement);
        void Visit(EchoExpressionStatement statement);
        void Visit(IfElseStatement statement);
        void Visit(IfStatement statement);
        void Visit(LetStatement statement);
        void Visit(YieldStatement statement);
        void Visit(FuncBindAssignment assignment);
        void Visit(VarBindAssignment assignment);

        //Predicate nodes
        void Visit(AndPredicate predicate);
        void Visit(ExpressionPredicate predicate);
        void Visit(IsPredicate predicate);
        void Visit(ListType type);
        void Visit(NotPredicate predicate);
        void Visit(OrPredicate predicate);
        void Visit(RecordType type);
        void Visit(StringType type);

        //Markup nodes
        void Visit(AttrArgument argument);
        void Visit(ClassAttribute attribute);
        void Visit(Designator designator);
        void Visit(ExpressionArgument argument);
        void Visit(HeightAttribute attribute);
        void Visit(IdAttribute attribute);
        void Visit(Markup.Markup markup);
        void Visit(NameAttribute attribute);
        void Visit(TypeAttribute attribute);
        void Visit(Width_HeightAttribute attribute);

        //Expression nodes
        void Visit(CatExpression expression);
        void Visit(FieldExpression expression);
        void Visit(KeyValuePair keyValuePair);
        void Visit(ListExpression expression);
        void Visit(NumExpression expression);
        void Visit(RecordExpression expression);
        void Visit(SymExpression expression);
        void Visit(TextExpression expression);
        void Visit(VarExpression expression);

        //Embedding nodes
        void Visit(Embedding.Embedding embedding);
        void Visit(ExpressionEmbed embed);
        void Visit(MarkupEmbed embed);
        void Visit(MidTextTail textTail);
        void Visit(PostTextTail textTail);
        void Visit(PreText preText);
        void Visit(MidText midText);
        void Visit(PostText postText);

        //Other
        void Visit(NodeList nodeList);
        void Test(NodeList list);
    }
}
