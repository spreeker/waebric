using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Parser.Ast.Predicates;
using Parser.Ast.Markup;
using Parser.Ast.Expressions;
using Parser.Ast.Embedding;
using Parser.Ast.Site;
using Parser.Ast.Module;
using Parser.Ast.Functions;

namespace Parser.Ast.Statements
{
    /// <summary>
    /// Visitor implementation which visits all type of AST nodes
    /// </summary>
    public class SyntaxNodeVisitor : ISyntaxNodeVisitor
    {
        #region Public Methods

        //All visit methods for all kind of NodeTypes
        public void Visit(Module.Module module)
        {
            VisitSubNodes(module);
        }

        public void Visit(ModuleId module)
        {
            VisitSubNodes(module);
        }

        public void Visit(Import import)
        {
            VisitSubNodes(import);
        }

        public void Visit(Site.Site site)
        {
            VisitSubNodes(site);
        }

        public void Visit(Mapping mapping)
        {
            VisitSubNodes(mapping);
        }

        public void Visit(Directory directory)
        {
            VisitSubNodes(directory);   
        }

        public void Visit(DirName dirName)
        {
            VisitSubNodes(dirName);
        }

        public void Visit(FileExt fileExt)
        {
            VisitSubNodes(fileExt);
        }

        public void Visit(FileName fileName)
        {
            VisitSubNodes(fileName);
        }

        public void Visit(Path path)
        {
            VisitSubNodes(path);
        }

        public void Visit(PathElement pathElement)
        {
            VisitSubNodes(pathElement);
        }

        public void Visit(FunctionDefinition functionDefinition)
        {
            VisitSubNodes(functionDefinition);
        }

        public void Visit(Formal formal)
        {
            VisitSubNodes(formal);
        }

        public void Visit(BlockStatement statement)
        {
            VisitSubNodes(statement);
        }

        public void Visit(CdataStatement statement)
        {
            VisitSubNodes(statement);
        }

        public void Visit(CommentStatement statement)
        {
            VisitSubNodes(statement);
        }

        public void Visit(EachStatement statement)
        {
            VisitSubNodes(statement);
        }

        public void Visit(EchoEmbeddingStatement statement)
        {
            VisitSubNodes(statement);
        }

        public void Visit(EchoExpressionStatement statement)
        {
            VisitSubNodes(statement);
        }

        public void Visit(IfElseStatement statement)
        {
            VisitSubNodes(statement);
        }

        public void Visit(IfStatement statement)
        {
            VisitSubNodes(statement);
        }

        public void Visit(LetStatement statement)
        {
            VisitSubNodes(statement);
        }

        public void Visit(YieldStatement statement)
        {
            VisitSubNodes(statement);
        }

        public void Visit(FuncBindAssignment assignment)
        {
            VisitSubNodes(assignment);
        }

        public void Visit(VarBindAssignment assignment)
        {
            VisitSubNodes(assignment);
        }

        public void Visit(MarkupStatStatement statement)
        {
            VisitSubNodes(statement);
        }

        public void Visit(MarkupStatement statement)
        {
            VisitSubNodes(statement);
        }

        public void Visit(MarkupMarkupStatement statement)
        {
            VisitSubNodes(statement);
        }

        public void Visit(MarkupExpressionStatement statement)
        {
            VisitSubNodes(statement);
        }

        public void Visit(MarkupEmbeddingStatement statement)
        {
            VisitSubNodes(statement);
        }

        public void Visit(AndPredicate predicate)
        {
            VisitSubNodes(predicate);
        }

        public void Visit(ExpressionPredicate predicate)
        {
            VisitSubNodes(predicate);
        }

        public void Visit(IsPredicate predicate)
        {
            VisitSubNodes(predicate);
        }

        public void Visit(ListType type)
        {
            VisitSubNodes(type);
        }

        public void Visit(NotPredicate predicate)
        {
            VisitSubNodes(predicate);
        }

        public void Visit(OrPredicate predicate)
        {
            VisitSubNodes(predicate);
        }

        public void Visit(RecordType type)
        {
            VisitSubNodes(type);
        }

        public void Visit(StringType type)
        {
            VisitSubNodes(type);
        }

        public void Visit(AttrArgument argument)
        {
            VisitSubNodes(argument);
        }

        public void Visit(ClassAttribute attribute)
        {
            VisitSubNodes(attribute);
        }

        public void Visit(Designator designator)
        {
            VisitSubNodes(designator);
        }

        public void Visit(ExpressionArgument argument)
        {
            VisitSubNodes(argument);
        }

        public void Visit(HeightAttribute attribute)
        {
            VisitSubNodes(attribute);
        }

        public void Visit(IdAttribute attribute)
        {
            VisitSubNodes(attribute);
        }

        public void Visit(Markup.Markup markup)
        {
            VisitSubNodes(markup);
        }

        public void Visit(NameAttribute attribute)
        {
            VisitSubNodes(attribute);
        }

        public void Visit(TypeAttribute attribute)
        {
            VisitSubNodes(attribute);
        }

        public void Visit(Width_HeightAttribute attribute)
        {
            VisitSubNodes(attribute);
        }

        public void Visit(CatExpression expression)
        {
            VisitSubNodes(expression);
        }

        public void Visit(FieldExpression expression)
        {
            VisitSubNodes(expression);
        }

        public void Visit(KeyValuePair keyValuePair)
        {
            VisitSubNodes(keyValuePair);
        }

        public void Visit(ListExpression expression)
        {
            VisitSubNodes(expression);
        }

        public void Visit(NumExpression expression)
        {
            VisitSubNodes(expression);
        }

        public void Visit(RecordExpression expression)
        {
            VisitSubNodes(expression);
        }

        public void Visit(SymExpression expression)
        {
            VisitSubNodes(expression);
        }

        public void Visit(TextExpression expression)
        {
            VisitSubNodes(expression);
        }

        public void Visit(VarExpression expression)
        {
            VisitSubNodes(expression);
        }

        public void Visit(Embedding.Embedding embedding)
        {
            VisitSubNodes(embedding);
        }

        public void Visit(ExpressionEmbed embed)
        {
            VisitSubNodes(embed);
        }

        public void Visit(MarkupEmbed embed)
        {
            VisitSubNodes(embed);
        }

        public void Visit(MidTextTail textTail)
        {
            VisitSubNodes(textTail);
        }

        public void Visit(PostTextTail textTail)
        {
            VisitSubNodes(textTail);
        }

        public void Visit(PreText preText)
        {
            VisitSubNodes(preText);
        }

        public void Visit(MidText midText)
        {
            VisitSubNodes(midText);
        }

        public void Visit(PostText postText)
        {
            VisitSubNodes(postText);
        }

        public void Visit(NodeList list)
        {
            VisitSubNodes(list);
        }

        #endregion

        #region Private Members

        /// <summary>
        /// Visit sub nodes of node
        /// </summary>
        /// <param name="node">Node to visit children from</param>
        private void VisitSubNodes(ISyntaxNode node)
        {
            foreach(ISyntaxNode subNode in node.GetSubNodes())
            {
                subNode.AcceptVisitor(this);
            }
        }

        #endregion
    }
}
