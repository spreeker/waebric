using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Markup
{
    /// <summary>
    /// Node which contains attribute
    /// </summary>
    public abstract class Attribute : ISyntaxNode
    {
        #region Public Methods

        public abstract void AcceptVisitor(ISyntaxNodeVisitor visitor);
        public abstract ISyntaxNode[] GetSubNodes();

        #endregion
    }
}
