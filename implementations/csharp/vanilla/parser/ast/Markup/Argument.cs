using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Parser.Ast.Expressions;

namespace Parser.Ast.Markup
{
    /// <summary>
    /// Node containing an argument
    /// </summary>
    public abstract class Argument : ISyntaxNode
    {
        #region Public Methods

        public abstract void AcceptVisitor(ISyntaxNodeVisitor visitor);
        public abstract ISyntaxNode[] GetSubNodes();

        #endregion
    }
}
