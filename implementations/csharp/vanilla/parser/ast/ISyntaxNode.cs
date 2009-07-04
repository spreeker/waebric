using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast
{
    /// <summary>
    /// Interface of a Syntax Node. A Syntax Node is an element of the syntax tree.
    /// </summary>
    public interface ISyntaxNode
    {
        #region Public Abstract Methods

        /// <summary>
        /// Accept the visitor to visit node
        /// </summary>
        /// <param name="visitor">Visisor</param>
        void AcceptVisitor(ISyntaxNodeVisitor visitor);

        /// <summary>
        /// Get subnodes of specific syntaxnode
        /// </summary>
        /// <returns></returns>
        ISyntaxNode[] GetSubNodes();

        #endregion
    }
}
