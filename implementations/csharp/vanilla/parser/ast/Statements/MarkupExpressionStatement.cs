using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Parser.Ast.Expressions;

namespace Parser.Ast.Statements
{
    /// <summary>
    /// Node which contains an MarkupExpressionStatement
    /// </summary>
    public class MarkupExpressionStatement : Statement
    {
        #region Private Members

        private NodeList MarkupList;
        private Expression Expression;

        #endregion

        #region Public Methods

        public MarkupExpressionStatement()
        {
            //Initialize member
            MarkupList = new NodeList();
        }

        public void AddMarkup(Markup.Markup markup)
        {
            MarkupList.Add(markup);
        }

        public void SetMarkups(NodeList markups)
        {
            MarkupList = markups;
        }

        public NodeList GetMarkups()
        {
            return MarkupList;
        }

        public void SetExpression(Expression expression)
        {
            Expression = expression;
        }

        public Expression GetExpression()
        {
            return Expression;
        }

        public override String ToString()
        {
            //Get Markups
            String markups = "";
            foreach (ISyntaxNode markup in MarkupList)
            {
                markups += ((Markup.Markup)markup).ToString();
                markups += " ";
            }

            return markups + Expression.ToString() + ";";
        }

        public override ISyntaxNode[] GetSubNodes()
        {
            return new ISyntaxNode[] { 
                MarkupList,
                Expression
            };
        }

        public override void AcceptVisitor(ISyntaxNodeVisitor visitor)
        {
            visitor.Visit(this);
        }
        #endregion
    }
}
