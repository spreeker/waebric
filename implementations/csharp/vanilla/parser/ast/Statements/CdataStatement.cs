using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Parser.Ast.Expressions;

namespace Parser.Ast.Statements
{
    /// <summary>
    /// Node which contains an CdataStatement
    /// </summary>
    public class CdataStatement : Statement
    {
        #region Private Members

        private Expression Expression;

        #endregion

        #region Public Methods

        /// <summary>
        /// Set expression of CdataStatement
        /// </summary>
        /// <param name="expression">Expression to set</param>
        public void SetExpression(Expression expression)
        {
            Expression = expression;
        }

        /// <summary>
        /// Get expression of CdataStatement
        /// </summary>
        /// <returns>Expression</returns>
        public Expression GetExpression()
        {
            return Expression;
        }

        /// <summary>
        /// Get string representation of CdataStatement
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            return "cdata " + Expression.ToString();
        }

        #endregion
    }
}
