using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Statements
{
    /// <summary>
    /// Node which contains Statements
    /// </summary>
    public class Statements : ISyntaxNode
    {
        #region Private Members

        private List<Statement> StatementList;

        #endregion

        #region Public Methods

        /// <summary>
        /// Add statement to Statements
        /// </summary>
        /// <param name="statement">Statement to add</param>
        public void AddStatement(Statement statement)
        {
            StatementList.Add(statement);
        }

        /// <summary>
        /// Get Statements
        /// </summary>
        /// <returns>StatementList</returns>
        public List<Statement> GetStatements()
        {
            return StatementList;
        }

        /// <summary>
        /// To Implement
        /// </summary>
        /// <returns></returns>
        public override String ToString()
        {
            return null;
        }

        #endregion
    }
}
