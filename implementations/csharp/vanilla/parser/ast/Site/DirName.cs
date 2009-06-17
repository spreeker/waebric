using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Site
{
    /// <summary>
    /// Node which contains DirName
    /// </summary>
    public class DirName : ISyntaxNode
    {
        #region Private Members

        private Directory DirectoryInstance;

        #endregion

        #region Public Methods

        /// <summary>
        /// Set directory of DirName
        /// </summary>
        /// <param name="directory">Directory to set</param>
        public void SetDirectory(Directory directory)
        {
            DirectoryInstance = directory;
        }

        /// <summary>
        /// Get directory of DirName
        /// </summary>
        /// <returns>Directory</returns>
        public Directory GetDirectory()
        {
            return DirectoryInstance;
        }

        /// <summary>
        /// Returns directory as string
        /// </summary>
        /// <returns>Directory</returns>
        public String ToString()
        {
            return DirectoryInstance.ToString();
        }

        #endregion
    }
}
