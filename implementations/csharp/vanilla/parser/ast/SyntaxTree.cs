using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast
{
    /// <summary>
    /// Representation of abstract syntax tree
    /// </summary>
    public class SyntaxTree
    {
        #region Private Members

        private ISyntaxNode root;

        #endregion

        #region Public Methods

        /// <summary>
        /// Empty constructor, does not fill root node at creation time
        /// </summary>
        public SyntaxTree()
        {

        }

        /// <summary>
        /// Constructor which fills root with specified rootNode
        /// </summary>
        /// <param name="rootNode">Node to set as root</param>
        public SyntaxTree(ISyntaxNode rootNode)
        {
            root = rootNode;
        }

        public ISyntaxNode GetRoot()
        {
            return root;
        }

        public void SetRoot(ISyntaxNode rootNode)
        {
            this.root = rootNode();
        }

        #endregion

    }
}
