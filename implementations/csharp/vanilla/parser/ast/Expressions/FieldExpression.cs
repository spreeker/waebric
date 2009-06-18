using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Expressions
{
    /// <summary>
    /// Node which contains a FieldExpression
    /// Expression.IdCon
    /// </summary>
    public class FieldExpression : Expression
    {
        #region Private Members

        private Expression Expr;
        private String Identifier;

        #endregion

        #region Public Methods

        /// <summary>
        /// Set expression of FieldExpression
        /// </summary>
        /// <param name="expression">Expression to set</param>
        public void SetExpression(Expression expression)
        {
            Expr = expression;
        }

        /// <summary>
        /// Get expression of FieldExpression
        /// </summary>
        /// <returns>Expression</returns>
        public Expression GetExpression()
        {
            return Expr;
        }

        /// <summary>
        /// Set idenntifier of FieldExpression
        /// </summary>
        /// <param name="identifier">Identifier to set</param>
        public void SetIdentifier(String identifier)
        {
            Identifier = identifier;
        }

        /// <summary>
        /// Get identifier of FieldExpression
        /// </summary>
        /// <returns>Identifier</returns>
        public String GetIdentifier()
        {
            return Identifier;
        }

        /// <summary>
        /// Get string representation of FieldExpression
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            return Expr.ToString() + "." + Identifier;
        }

        #endregion
    }
}
