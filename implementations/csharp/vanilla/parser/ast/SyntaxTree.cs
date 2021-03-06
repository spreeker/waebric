﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Parser.Ast.Module;

namespace Parser.Ast
{
    /// <summary>
    /// Representation of abstract syntax tree
    /// </summary>
    public class SyntaxTree
    {
        #region Private Members

        private Module.Module root;

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
        public SyntaxTree(Module.Module rootNode)
        {
            root = rootNode;
        }

        /// <summary>
        /// Get root of tree
        /// </summary>
        /// <returns>Root of tree</returns>
        public Module.Module GetRoot()
        {
            return root;
        }

        /// <summary>
        /// Set root of tree
        /// </summary>
        /// <param name="rootNode">Node to set as root</param>
        public void SetRoot(Module.Module rootNode)
        {
            this.root = rootNode;
        }

        #endregion

    }
}
