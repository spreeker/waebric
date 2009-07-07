using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections;
using Parser.Ast;

namespace Parser.Ast
{
    /// <summary>
    /// Class which holds a list of nodes of type T
    /// </summary>
    public class NodeList : List<ISyntaxNode>, ISyntaxNode
    {
        #region Private Members

        private List<ISyntaxNode> list = new List<ISyntaxNode>();

        #endregion

        #region Public Members

        public new int Count = 0;

        #endregion

        #region Public Methods

        /// <summary>
        /// Add element to NodeList
        /// </summary>
        /// <param name="element">Element to add</param>
        public new void Add(ISyntaxNode element)
        {
            list.Add(element);
            Count++;
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

        /// <summary>
        /// Remove element at specified index
        /// </summary>
        /// <param name="index">Index of item to remove</param>
        public void Remove(int index)
        {
            list.RemoveAt(index);
            Count--;
        }

        /// <summary>
        /// Get all elements of list as array of syntax nodes
        /// </summary>
        /// <returns>Array of ISyntaxNode</returns>
        public ISyntaxNode[] GetElements()
        {
            return list.ToArray();
        }

        /// <summary>
        /// Clear the NodeList
        /// </summary>
        public new void Clear()
        {
            list.Clear();
            Count = 0;
        }

        /// <summary>
        /// Converts list to array
        /// </summary>
        /// <returns>Array of list</returns>
        public new ISyntaxNode[] ToArray()
        {
            return list.ToArray();
        }

        public override String ToString()
        {
            String text = "";
            ISyntaxNode[] nodes = list.ToArray();
            for (int i = 0; i <= (nodes.Length - 1); i++)
            {
                text += nodes[i].ToString();
            }

            return text;
        }

        public ISyntaxNode[] GetSubNodes()
        {
            return list.ToArray();
        }

        public void AcceptVisitor(ISyntaxNodeVisitor visitor)
        {
            visitor.Visit(this);
        }

        #endregion
    }
}
