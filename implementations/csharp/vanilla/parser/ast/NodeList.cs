using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections;

namespace Parser.Ast
{
    /// <summary>
    /// Class which holds a list of nodes as node
    /// </summary>
    public class NodeList : ISyntaxNode
    {
        private List<ISyntaxNode> list = new List<ISyntaxNode>();

        /// <summary>
        /// Get size of NodeList
        /// </summary>
        /// <returns>Size</returns>
        public int GetSize()
        {
            return list.Count;
        }
    }
}
