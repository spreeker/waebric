using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Site
{
    /// <summary>
    /// Node which holds file extension
    /// </summary>
    public class FileExt : ISyntaxNode
    {
        #region Private Members

        private String FileExtension;

        #endregion

        #region Public Methods

        /// <summary>
        /// Empty constructor
        /// </summary>
        public FileExt()
        {
        }

        /// <summary>
        /// Constructor with creation of fileExtension
        /// </summary>
        /// <param name="fileExtension">Extension</param>
        public FileExt(String fileExtension)
        {
            FileExtension = fileExtension;
        }

        /// <summary>
        /// Set file extension
        /// </summary>
        /// <param name="fileExtension">File extension</param>
        public void SetFileExtension(String fileExtension)
        {
            FileExtension = fileExtension;
        }

        /// <summary>
        /// Get file extension
        /// </summary>
        /// <returns>FileExtension</returns>
        public String GetFileExtension()
        {
            return FileExtension;
        }

        #endregion
    }
}
