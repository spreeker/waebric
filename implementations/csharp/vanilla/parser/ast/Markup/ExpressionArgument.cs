using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Parser.Ast.Expressions;

namespace Parser.Ast.Markup
{
    /// <summary>
    /// Node which contains an ExpressionArgument
    /// </summary>
    public class ExpressionArgument : Argument
    {
        #region Private Members

        private Expression Expr;

        #endregion

        #region Public Methods

        /// <summary>
        /// Set expression of ExpressionArgument
        /// </summary>
        /// <param name="expression">Expression to set</param>
        public void SetExpression(Expression expression)
        {
            Expr = expression;
        }

        /// <summary>
        /// Get expression of ExpressionArgument
        /// </summary>
        /// <returns>Expression</returns>
        public Expression GetExpression()
        {
            return Expr;
        }

        /// <summary>
        /// Get string representation of ExpressionArgument
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            return Expr.ToString();
        }

        #endregion

    }
}
