using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Parser.Ast.Expressions;

namespace Parser.Ast.Embedding
{
    /// <summary>
    /// Node which contains an ExpressionEmbed
    /// </summary>
    public class ExpressionEmbed : Embed
    {
        #region Private Members

        private NodeList MarkupList;
        private Expression Expression;

        #endregion

        #region Public Methods

        public ExpressionEmbed()
        {
            //Initalize member
            MarkupList = new NodeList();
        }

        public void SetMarkups(NodeList markupList)
        {
            MarkupList = markupList;
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
            String Buffer = "";

            foreach (Markup.Markup node in MarkupList)
            {
                Buffer += node.ToString();
            }

            return Buffer.ToString() + Expression.ToString();
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
