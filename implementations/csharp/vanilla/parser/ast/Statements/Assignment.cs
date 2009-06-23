using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Parser.Ast.Expressions;

namespace Parser.Ast.Statements
{
    /// <summary>
    /// Node which contains an Assignment
    /// </summary>
    public class Assignment : ISyntaxNode
    {
        #region Private Members

        private String Identifier;
        private Expression Expression;

        #endregion

        #region Public Methods

        /// <summary>
        /// Set identifier of Assignment
        /// </summary>
        /// <param name="identifier">Identifier to set</param>
        public void SetIdentifier(String identifier)
        {
            Identifier = identifier;
        }

        /// <summary>
        /// Get identifier of Assignment
        /// </summary>
        /// <returns>Identifier</returns>
        public String GetIdentifier()
        {
            return Identifier;
        }

        /// <summary>
        /// Set expression of Assignment
        /// </summary>
        /// <param name="expression">Expression to set</param>
        public void SetExpression(Expression expression)
        {
            Expression = expression;
        }

        /// <summary>
        /// Get expression of Assignment
        /// </summary>
        /// <returns>Expression</returns>
        public Expression GetExpression()
        {
            return Expression;
        }

        /// <summary>
        /// Get string representation of Assignment
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            return Identifier + "=" + Expression.ToString() + ";";
        }

        #endregion
    }
}
