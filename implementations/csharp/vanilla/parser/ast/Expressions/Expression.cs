using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Expressions
{
    /// <summary>
    /// Node which contains an expression
    /// </summary>
    public abstract class Expression : ISyntaxNode
    {
        #region Public Methods

        public abstract void AcceptVisitor(ISyntaxNodeVisitor visitor);
        public abstract ISyntaxNode[] GetSubNodes();

        #endregion
    }
}
