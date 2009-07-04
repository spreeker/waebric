using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Site
{
    /// <summary>
    /// Node which contains filename
    /// </summary>
    public class FileName : ISyntaxNode
    {
        #region Private Members

        private FileExt FileExtension;
        private PathElement Name;

        #endregion

        #region Public Methods

        /// <summary>
        /// Set file extension of filename
        /// </summary>
        /// <param name="fileExtension"></param>
        public void SetFileExtension(FileExt fileExtension)
        {
            FileExtension = fileExtension;
        }

        /// <summary>
        /// Get file extension
        /// </summary>
        /// <returns>FileExtension</returns>
        public FileExt GetFileExtension()
        {
            return FileExtension;
        }

        /// <summary>
        /// Set name of file
        /// </summary>
        /// <param name="name">Name to set</param>
        public void SetName(PathElement name)
        {
            Name = name;
        }

        /// <summary>
        /// Get name of file
        /// </summary>
        /// <returns>Name</returns>
        public PathElement GetName()
        {
            return Name;
        }

        /// <summary>
        /// Get filename.extension as string
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            return Name.ToString() + "." + FileExtension.ToString();
        }

        public ISyntaxNode[] GetSubNodes()
        {
            return new ISyntaxNode[] { 
                Name,
                FileExtension
            };
        }

        public void AcceptVisitor(ISyntaxNodeVisitor visitor)
        {
            visitor.Visit(this);
        }

        #endregion
    }
}
