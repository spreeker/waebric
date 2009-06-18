using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Expressions
{
    /// <summary>
    /// Node contains CatExpression
    /// Expression + Expression
    /// </summary>
    public class CatExpression : Expression
    {
        #region Private Members

        private Expression LeftExpression;
        private Expression RightExpression;
        
        #endregion

        #region Public Methods

        /// <summary>
        /// Set left expression
        /// </summary>
        /// <param name="leftExpression">Expression</param>
        public void SetLeftExpression(Expression leftExpression)
        {
            LeftExpression = leftExpression;
        }

        /// <summary>
        /// Get left expression
        /// </summary>
        /// <returns>Expression</returns>
        public Expression GetLeftExpression()
        {
            return LeftExpression;
        }

        /// <summary>
        /// Set right expression
        /// </summary>
        /// <param name="rightExpression">Expression</param>
        public void SetRightExpression(Expression rightExpression)
        {
            RightExpression = rightExpression;
        }

        /// <summary>
        /// Get right expression
        /// </summary>
        /// <returns>Expression</returns>
        public Expression GetRightExpression()
        {
            return RightExpression;
        }

        /// <summary>
        /// Get string representation of CatExpression
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            return LeftExpression.ToString() + "+" + RightExpression.ToString();
        }

        #endregion
    }
}
