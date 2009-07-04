using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Site
{
    /// <summary>
    /// Node which holds directory
    /// </summary>
    public class Directory : ISyntaxNode
    {
        #region Private Members

        private NodeList DirectoryList;

        #endregion

        #region Public Methods

        public Directory()
        {
            DirectoryList = new NodeList();
        }

        /// <summary>
        /// Add directory element
        /// </summary>
        /// <param name="element">Pathelement to add</param>
        public void AddDirectoryElement(PathElement element)
        {
            DirectoryList.Add(element);
        }

        /// <summary>
        /// Get list of directory elements
        /// </summary>
        /// <returns>DirectoryElementList</returns>
        public NodeList GetDirectoryElements()
        {
            return DirectoryList;
        }
        
        /// <summary>
        /// Converts directory's to String
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            PathElement[] elements = (PathElement[]) DirectoryList.ToArray();
            StringBuilder directoryString = new StringBuilder();
            foreach (PathElement current in elements)
            { //rebuild directorypath
                directoryString.Append(current.ToString());
                directoryString.Append("/");
            }
            
            return directoryString.ToString();
        }

        public ISyntaxNode[] GetSubNodes()
        {
            return new ISyntaxNode[] { 
                DirectoryList
            };
        }

        public void AcceptVisitor(ISyntaxNodeVisitor visitor)
        {
            visitor.Visit(this);
        }

        #endregion
    }
}
