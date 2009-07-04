using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Parser.Ast.Predicates;

namespace Parser.Ast.Statements
{
    /// <summary>
    /// Node which contains an IfStatement
    /// </summary>
    public class IfStatement : Statement
    {
        #region Private Members

        private Predicate Predicate;
        private Statement TrueStatement;

        #endregion 

        #region Public Methods

        /// <summary>
        /// Set predicate of IfStatement
        /// </summary>
        /// <param name="predicate">Predicate to set</param>
        public void SetPredicate(Predicate predicate)
        {
            Predicate = predicate;
        }

        /// <summary>
        /// Get predicate of IfStatement
        /// </summary>
        /// <returns>Predicate</returns>
        public Predicate GetPredicate()
        {
            return Predicate;
        }

        /// <summary>
        /// Set truestatement of IfStatement
        /// </summary>
        /// <param name="trueStatement"></param>
        public void SetTrueStatement(Statement trueStatement)
        {
            TrueStatement = trueStatement;
        }

        /// <summary>
        /// Get truestatement of IfStatement
        /// </summary>
        /// <returns>Statement</returns>
        public Statement GetTrueStatement()
        {
            return TrueStatement;
        }

        /// <summary>
        /// Get string representation of IfStatement
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            return "if (" + Predicate.ToString() + ")" + TrueStatement.ToString();
        }

        public override ISyntaxNode[] GetSubNodes()
        {
            return new ISyntaxNode[] { 
                Predicate,
                TrueStatement
            };
        }

        public override void AcceptVisitor(ISyntaxNodeVisitor visitor)
        {
            visitor.Visit(this);
        }

        #endregion 
    }
}
