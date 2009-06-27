using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Parser.Ast.Expressions;

namespace Parser.Ast.Statements
{
    /// <summary>
    /// Node which contains an EchoExpressionStatement
    /// </summary>
    public class EchoExpressionStatement : EchoStatement
    {
        #region Private Members

        private Expression Expression;

        #endregion

        #region Public Methods

        /// <summary>
        /// Set expression of EchoStatement
        /// </summary>
        /// <param name="expression">Expression to set</param>
        public void SetExpression(Expression expression)
        {
            Expression = expression;
        }

        /// <summary>
        /// Get expression of EchoStatement
        /// </summary>
        /// <returns>Expression</returns>
        public Expression GetExpression()
        {
            return Expression;
        }

        /// <summary>
        /// Get string representation of EchoStatement
        /// </summary>
        /// <returns></returns>
        public override String ToString()
        {
            return "echo " + Expression.ToString();
        }

        #endregion
    }
}
