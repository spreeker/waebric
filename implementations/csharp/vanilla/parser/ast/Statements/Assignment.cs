using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Parser.Ast.Expressions;

namespace Parser.Ast.Statements
{
    /// <summary>
    /// Node which contains an Assignment
    /// </summary>
    public abstract class Assignment : ISyntaxNode
    {
        #region Public Methods

        public abstract ISyntaxNode[] GetSubNodes();
        public abstract void AcceptVisitor(ISyntaxNodeVisitor visitor);

        #endregion
    }
}
