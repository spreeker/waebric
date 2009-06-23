using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Parser.Ast.Expressions;

namespace Parser.Ast.Statements
{
    /// <summary>
    /// Node which contains an EachStatement
    /// </summary>
    public class EachStatement : ISyntaxNode
    {
        #region Private Members

        private String Identifier;
        private Expression Expression;
        private Statement EachStatement;

        #endregion

        #region Public Methods

        public void SetIdentifier(String identifier)
        {
            Identifier = identifier;
        }

        public String GetIdentifier()
        {
            return Identifier;
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
            return "each (" + Identifier + ":" + Expression.ToString() + ")" + EachStatement.ToString();
        }

        #endregion
    }
}
