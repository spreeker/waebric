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

        private List<Markup.Markup> MarkupList;
        private Expression Expression;

        #endregion

        #region Public Methods

        public ExpressionEmbed()
        {
            //Initalize member
            MarkupList = new List<Markup.Markup>();
        }

        public void AddMarkup(Markup.Markup markup)
        {
            MarkupList.Add(markup);
        }

        public List<Markup.Markup> GetMarkups()
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
            StringBuilder stringBuilder = new StringBuilder();
            Markup.Markup[] markupArray = MarkupList.ToArray();
            
            //build markup string
            for (int i = 0; i <= (markupArray.Length - 1); i++)
            {
                stringBuilder.Append(markupArray[i].ToString());
            }

            return stringBuilder.ToString() + Expression.ToString();
        }

        #endregion
    }
}
