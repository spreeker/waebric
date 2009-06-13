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

        private List<PathElement> DirectoryList;

        #endregion

        #region Public Methods

        public Directory()
        {
            DirectoryList = new List<PathElement>();
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
        public List<PathElement> GetDirectoryElements()
        {
            return DirectoryList;
        }

        #endregion
    }
}
