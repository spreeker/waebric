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
        private Statement Statement;

        #endregion

        #region Public Methods

        /// <summary>
        /// Set identifier of EachStatement
        /// </summary>
        /// <param name="identifier">Identifier to set</param>
        public void SetIdentifier(String identifier)
        {
            Identifier = identifier;
        }

        /// <summary>
        /// Get identifier of EachStatement
        /// </summary>
        /// <returns>Identifier</returns>
        public String GetIdentifier()
        {
            return Identifier;
        }

        /// <summary>
        /// Set expression of EachStatement
        /// </summary>
        /// <param name="expression">Expression to set</param>
        public void SetExpression(Expression expression)
        {
            Expression = expression;
        }

        /// <summary>
        /// Get expression of EachStatement
        /// </summary>
        /// <returns>Expression</returns>
        public Expression GetExpression()
        {
            return Expression;
        }

        /// <summary>
        /// Set statement of EachStatement
        /// </summary>
        /// <param name="statement">Statement to set</param>
        public void SetStatement(Statement statement)
        {
            Statement = statement;
        }

        /// <summary>
        /// Get statement of EachStatement
        /// </summary>
        /// <returns>Statement</returns>
        public Statement GetStatement()
        {
            return Statement;
        }

        /// <summary>
        /// Get string representation of EachStatement
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            return "each (" + Identifier + ":" + Expression.ToString() + ")" + Statement.ToString();
        }

        #endregion
    }
}
