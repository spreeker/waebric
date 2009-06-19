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
        private Statements.Statement Stmts;

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
        /// Set statements of FunctionDefinition
        /// </summary>
        /// <param name="statement">Statements to set</param>
        public void SetStatements(Statements.Statement statements)
        {
            Stmts = statements;
        }

        /// <summary>
        /// Get statements of FunctionDefinition
        /// </summary>
        /// <returns>Statements</returns>
        public Statements.Statement GetStatements()
        {
            return Stmts;
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
