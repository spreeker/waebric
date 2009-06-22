using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Parser.Ast.Statements;

namespace Parser.Ast.Functions
{
    /// <summary>
    /// Node which contains FunctionDefinition
    /// </summary>
    public class FunctionDefinition : ISyntaxNode
    {
        #region Private Members

        private String Identifier;
        private Formals Frmls;
        private List<Statement> StatementList;

        #endregion

        #region Public Methods

        /// <summary>
        /// Set identifier of FunctionDefinition
        /// </summary>
        /// <param name="identifier">Identifier to set</param>
        public void SetIdentifier(String identifier)
        {
            Identifier = identifier;
        }

        /// <summary>
        /// Get identifier of FunctionDefinition
        /// </summary>
        /// <returns>Identifier</returns>
        public String GetIdentifier()
        {
            return Identifier;
        }

        /// <summary>
        /// Set formals of FunctionDefinition
        /// </summary>
        /// <param name="formal">Formals to set</param>
        public void SetFormals(Formals formals)
        {
            Frmls = formals;
        }

        /// <summary>
        /// Get formals of FunctionDefinition
        /// </summary>
        /// <returns>Formals</returns>
        public Formals GetFormals()
        {
            return Frmls;
        }

        /// <summary>
        /// Add statement to FunctionDefinition
        /// </summary>
        /// <param name="statement">Statement to add</param>
        public void AddStatement(Statement statement)
        {
            StatementList.Add(statement);
        }

        /// <summary>
        /// Get statements of FunctionDefinition
        /// </summary>
        /// <returns>StatementList</returns>
        public List<Statement> GetStatements()
        {
            return StatementList;
        }

        /// <summary>
        /// Get string representation of FunctionDefinition
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            return null; //TO IMPLEMENT!
            //return "def" + Identifier + "(" + Frmls.ToString() + ")" + StatementList.ToString() + "end";
        }

        #endregion

    }
}
