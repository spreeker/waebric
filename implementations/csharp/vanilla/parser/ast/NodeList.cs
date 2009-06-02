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

        /// <summary>
        /// Add element to NodeList
        /// </summary>
        /// <param name="element">Element to add</param>
        public void Add(ISyntaxNode element)
        {
            list.Add(element);
        }


        /// <summary>
        /// Get element from specified index
        /// </summary>
        /// <param name="index">Index of element to retrieve</param>
        /// <returns>Element</returns>
        public ISyntaxNode Get(int index)
        {
            return list.ElementAt(index);
        }

        public ISyntaxNode[] GetElements()
        {
            return list.ToArray();
        }

        /// <summary>
        /// Clear the NodeList
        /// </summary>
        public void Clear()
        {
            list.Clear();
        }


    }
}
