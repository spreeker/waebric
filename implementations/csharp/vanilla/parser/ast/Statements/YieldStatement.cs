using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Statements
{
    /// <summary>
    /// Node which contains an YieldStatement
    /// </summary>
    public class YieldStatement : Statement
    {
        #region Public Methods

        /// <summary>
        /// Get string representation of yield
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            return "yield;";
        }

        public override void AcceptVisitor(ISyntaxNodeVisitor visitor)
        {
            visitor.Visit(this);
        }

        public override ISyntaxNode[] GetSubNodes()
        {
            return new ISyntaxNode[] { };
        }

        #endregion
    }
}
