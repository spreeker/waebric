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
using Parser.Ast.Statements;

namespace Parser.Ast
{
    /// <summary>
    /// Visitor implementation which visits all type of AST nodes
    /// </summary>
    public class SyntaxNodeVisitor : ISyntaxNodeVisitor
    {
        #region Public Methods

        //All visit methods for all kind of NodeTypes
        public virtual void Visit(Module.Module module)
        {
            VisitSubNodes(module);
        }

        public virtual void Visit(ModuleId module)
        {
            VisitSubNodes(module);
        }

        public virtual void Visit(Import import)
        {
            VisitSubNodes(import);
        }

        public virtual void Visit(Site.Site site)
        {
            VisitSubNodes(site);
        }

        public virtual void Visit(Mapping mapping)
        {
            VisitSubNodes(mapping);
        }

        public virtual void Visit(Directory directory)
        {
            VisitSubNodes(directory);   
        }

        public virtual void Visit(DirName dirName)
        {
            VisitSubNodes(dirName);
        }

        public virtual void Visit(FileExt fileExt)
        {
            VisitSubNodes(fileExt);
        }

        public virtual void Visit(FileName fileName)
        {
            VisitSubNodes(fileName);
        }

        public virtual void Visit(Path path)
        {
            VisitSubNodes(path);
        }

        public virtual void Visit(PathElement pathElement)
        {
            VisitSubNodes(pathElement);
        }

        public virtual void Visit(FunctionDefinition functionDefinition)
        {
            VisitSubNodes(functionDefinition);
        }

        public virtual void Visit(Formal formal)
        {
            VisitSubNodes(formal);
        }

        public virtual void Visit(BlockStatement statement)
        {
            VisitSubNodes(statement);
        }

        public virtual void Visit(CdataStatement statement)
        {
            VisitSubNodes(statement);
        }

        public virtual void Visit(CommentStatement statement)
        {
            VisitSubNodes(statement);
        }

        public virtual void Visit(EachStatement statement)
        {
            VisitSubNodes(statement);
        }

        public virtual void Visit(EchoEmbeddingStatement statement)
        {
            VisitSubNodes(statement);
        }

        public virtual void Visit(EchoExpressionStatement statement)
        {
            VisitSubNodes(statement);
        }

        public virtual void Visit(IfElseStatement statement)
        {
            VisitSubNodes(statement);
        }

        public virtual void Visit(IfStatement statement)
        {
            VisitSubNodes(statement);
        }

        public virtual void Visit(LetStatement statement)
        {
            VisitSubNodes(statement);
        }

        public virtual void Visit(YieldStatement statement)
        {
            VisitSubNodes(statement);
        }

        public virtual void Visit(FuncBindAssignment assignment)
        {
            VisitSubNodes(assignment);
        }

        public virtual void Visit(VarBindAssignment assignment)
        {
            VisitSubNodes(assignment);
        }

        public virtual void Visit(MarkupStatStatement statement)
        {
            VisitSubNodes(statement);
        }

        public virtual void Visit(MarkupStatement statement)
        {
            VisitSubNodes(statement);
        }

        public virtual void Visit(MarkupMarkupStatement statement)
        {
            VisitSubNodes(statement);
        }

        public virtual void Visit(MarkupExpressionStatement statement)
        {
            VisitSubNodes(statement);
        }

        public virtual void Visit(MarkupEmbeddingStatement statement)
        {
            VisitSubNodes(statement);
        }

        public virtual void Visit(AndPredicate predicate)
        {
            VisitSubNodes(predicate);
        }

        public virtual void Visit(ExpressionPredicate predicate)
        {
            VisitSubNodes(predicate);
        }

        public virtual void Visit(IsPredicate predicate)
        {
            VisitSubNodes(predicate);
        }

        public virtual void Visit(ListType type)
        {
            VisitSubNodes(type);
        }

        public virtual void Visit(NotPredicate predicate)
        {
            VisitSubNodes(predicate);
        }

        public virtual void Visit(OrPredicate predicate)
        {
            VisitSubNodes(predicate);
        }

        public virtual void Visit(RecordType type)
        {
            VisitSubNodes(type);
        }

        public virtual void Visit(StringType type)
        {
            VisitSubNodes(type);
        }

        public virtual void Visit(AttrArgument argument)
        {
            VisitSubNodes(argument);
        }

        public virtual void Visit(ClassAttribute attribute)
        {
            VisitSubNodes(attribute);
        }

        public virtual void Visit(Designator designator)
        {
            VisitSubNodes(designator);
        }

        public virtual void Visit(ExpressionArgument argument)
        {
            VisitSubNodes(argument);
        }

        public virtual void Visit(HeightAttribute attribute)
        {
            VisitSubNodes(attribute);
        }

        public virtual void Visit(IdAttribute attribute)
        {
            VisitSubNodes(attribute);
        }

        public virtual void Visit(Markup.Markup markup)
        {
            VisitSubNodes(markup);
        }

        public virtual void Visit(NameAttribute attribute)
        {
            VisitSubNodes(attribute);
        }

        public virtual void Visit(TypeAttribute attribute)
        {
            VisitSubNodes(attribute);
        }

        public virtual void Visit(Width_HeightAttribute attribute)
        {
            VisitSubNodes(attribute);
        }

        public virtual void Visit(CatExpression expression)
        {
            VisitSubNodes(expression);
        }

        public virtual void Visit(FieldExpression expression)
        {
            VisitSubNodes(expression);
        }

        public virtual void Visit(KeyValuePair keyValuePair)
        {
            VisitSubNodes(keyValuePair);
        }

        public virtual void Visit(ListExpression expression)
        {
            VisitSubNodes(expression);
        }

        public virtual void Visit(NumExpression expression)
        {
            VisitSubNodes(expression);
        }

        public virtual void Visit(RecordExpression expression)
        {
            VisitSubNodes(expression);
        }

        public virtual void Visit(SymExpression expression)
        {
            VisitSubNodes(expression);
        }

        public virtual void Visit(TextExpression expression)
        {
            VisitSubNodes(expression);
        }

        public virtual void Visit(VarExpression expression)
        {
            VisitSubNodes(expression);
        }

        public virtual void Visit(Embedding.Embedding embedding)
        {
            VisitSubNodes(embedding);
        }

        public virtual void Visit(ExpressionEmbed embed)
        {
            VisitSubNodes(embed);
        }

        public virtual void Visit(MarkupEmbed embed)
        {
            VisitSubNodes(embed);
        }

        public virtual void Visit(MidTextTail textTail)
        {
            VisitSubNodes(textTail);
        }

        public virtual void Visit(PostTextTail textTail)
        {
            VisitSubNodes(textTail);
        }

        public virtual void Visit(PreText preText)
        {
            VisitSubNodes(preText);
        }

        public virtual void Visit(MidText midText)
        {
            VisitSubNodes(midText);
        }

        public virtual void Visit(PostText postText)
        {
            VisitSubNodes(postText);
        }

        public virtual void Visit(NodeList list)
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
