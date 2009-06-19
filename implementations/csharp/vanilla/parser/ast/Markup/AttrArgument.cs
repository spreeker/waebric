using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Parser.Ast.Expressions;

namespace Parser.Ast.Markup
{
    /// <summary>
    /// Node which contains an AttrArgument
    /// </summary>
    public class AttrArgument : Argument
    {
        #region Private Members

        private String Identifier;
        private Expression Expr;

        #endregion

        #region Public Methods

        /// <summary>
        /// Set identifier of AttrArgument
        /// </summary>
        /// <param name="identifier">Identifier to set</param>
        public void SetIdentifier(String identifier)
        {
            Identifier = identifier;
        }

        /// <summary>
        /// Get identifier of AttrArgument
        /// </summary>
        /// <returns>Identifier</returns>
        public String GetIdentifier()
        {
            return Identifier;
        }

        /// <summary>
        /// Set expression of AttrArgument
        /// </summary>
        /// <param name="expression">Expression to set</param>
        public void SetExpression(Expression expression)
        {
            Expr = expression;
        }

        /// <summary>
        /// Get expression of AttrArgument
        /// </summary>
        /// <returns>Expression</returns>
        public Expression GetExpression()
        {
            return Expr;
        }

        /// <summary>
        /// Get string representation of AttrArgument
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            return Identifier + "=" + Expr.ToString();
        }
        #endregion

    }
}
