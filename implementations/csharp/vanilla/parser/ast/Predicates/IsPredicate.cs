using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Parser.Ast.Expressions;

namespace Parser.Ast.Predicates
{
    /// <summary>
    /// Node which contains an IsPredicate
    /// </summary>
    public class IsPredicate : Predicate
    {
        #region Private Members

        private Expression Expression;
        private Type Type;

        #endregion

        #region Public Methods

        /// <summary>
        /// Set expression of ExpressionPredicate
        /// </summary>
        /// <param name="expression"></param>
        public void SetExpression(Expression expression)
        {
            Expression = expression;
        }

        /// <summary>
        /// Get expression of ExpressionPredicate
        /// </summary>
        /// <returns>Expression</returns>
        public Expression GetExpression()
        {
            return Expression;
        }

        /// <summary>
        /// Set type of ExpressionPredicate
        /// </summary>
        /// <param name="type">Type to set</param>
        public void SetType(Type type)
        {
            Type = type;
        }

        /// <summary>
        /// Get type of ExpressionPredicate
        /// </summary>
        /// <returns>Type</returns>
        public new Type GetType()
        {
            return Type;
        }

        public override String ToString()
        {
            return Expression.ToString() + "." + Type.ToString() + "?";
        }

        #endregion
    }
}
