using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Parser.Ast.Expressions;

namespace Parser.Ast.Predicates
{
    /// <summary>
    /// Node which contains an ExpressionPredicate
    /// </summary>
    public class ExpressionPredicate : Predicate
    {
        #region Private Members

        private Expression Expression;

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
        /// Get string representation of ExpressionPredicate
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            return Expression.ToString();
        }

        public override ISyntaxNode[] GetSubNodes()
        {
            return new ISyntaxNode[] { 
                Expression
            };
        }

        public override void AcceptVisitor(ISyntaxNodeVisitor visitor)
        {
            visitor.Visit(this);
        }

        #endregion
    }
}
