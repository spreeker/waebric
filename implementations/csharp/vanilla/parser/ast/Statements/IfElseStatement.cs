using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Parser.Ast.Predicates;

namespace Parser.Ast.Statements
{
    /// <summary>
    /// Node which contains an IfElseStatement
    /// </summary>
    public class IfElseStatement : Statement
    {
        #region Private Members

        private Predicate Predicate;
        private Statement TrueStatement;
        private Statement FalseStatement;

        #endregion 

        #region Public Methods

        /// <summary>
        /// Set predicate of IfElseStatement
        /// </summary>
        /// <param name="predicate">Predicate to set</param>
        public void SetPredicate(Predicate predicate)
        {
            Predicate = predicate;
        }

        /// <summary>
        /// Get predicate of IfElseStatement
        /// </summary>
        /// <returns>Predicate</returns>
        public Predicate GetPredicate()
        {
            return Predicate;
        }

        /// <summary>
        /// Set TrueStatement of IfElseStatement
        /// </summary>
        /// <param name="trueStatement">TrueStatement to set</param>
        public void SetTrueStatement(Statement trueStatement)
        {
            TrueStatement = trueStatement;
        }

        /// <summary>
        /// Get TrueStatement of IfElseStatement
        /// </summary>
        /// <returns>TrueStatement</returns>
        public Statement GetTrueStatement()
        {
            return TrueStatement;
        }

        /// <summary>
        /// Set FalseStatement of IfElseStatement
        /// </summary>
        /// <param name="falseStatement">FalseStatement to set</param>
        public void SetFalseStatement(Statement falseStatement)
        {
            FalseStatement = falseStatement;
        }

        /// <summary>
        /// Get FalseStatement of IfElseStatement
        /// </summary>
        /// <returns>FalseStatement</returns>
        public Statement GetFalseStatement()
        {
            return FalseStatement;
        }

        /// <summary>
        /// Get string representation of IfElseStatement
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            return "if (" + Predicate.ToString() + ")" + TrueStatement.ToString() + "else" + FalseStatement.ToString();
        }


        #endregion
    }
}
