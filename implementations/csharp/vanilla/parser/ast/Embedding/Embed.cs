using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Embedding
{
    /// <summary>
    /// Node which contains an Embed
    /// </summary>
    public abstract class Embed : ISyntaxNode
    {
        #region Public Method

        public abstract void AcceptVisitor(ISyntaxNodeVisitor visitor);
        public abstract ISyntaxNode[] GetSubNodes();

        #endregion
    }
}
