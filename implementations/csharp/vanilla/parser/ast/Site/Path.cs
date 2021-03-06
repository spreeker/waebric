﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Site
{

    /// <summary>
    /// Node contains path
    /// </summary>
    public class Path : ISyntaxNode
    {
        #region Private Members

        private FileName Filename;
        private DirName DirectoryName;

        #endregion

        #region Public Methods

        /// <summary>
        /// Set filename of path
        /// </summary>
        /// <param name="filename">Filename to set</param>
        public void SetFilename(FileName filename)
        {
            Filename = filename;
        }

        /// <summary>
        /// Get filename of path
        /// </summary>
        /// <returns>Filename</returns>
        public FileName GetFilename()
        {
            return Filename;
        }

        /// <summary>
        /// Set DirName
        /// </summary>
        /// <param name="element">DirName</param>
        public void SetDirectoryName(DirName directoryName)
        {
            DirectoryName = directoryName;
        }

        /// <summary>
        /// Get DirName
        /// </summary>
        /// <returns>DirName</returns>
        public DirName GetDirectoryName()
        {
            return DirectoryName;
        }

        /// <summary>
        /// Gives complete path back as string
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            if (DirectoryName != null)
            {
                return DirectoryName.ToString() + Filename.ToString();
            }
            else
            {
                return Filename.ToString();
            }
        }

        public ISyntaxNode[] GetSubNodes()
        {
            if (DirectoryName != null)
            {
                return new ISyntaxNode[] { 
                    DirectoryName,
                    Filename
                };
            }
            else
            {
                return new ISyntaxNode[] { Filename };
            }
        }

        public void AcceptVisitor(ISyntaxNodeVisitor visitor)
        {
            visitor.Visit(this);
        }

        #endregion
    }
}
