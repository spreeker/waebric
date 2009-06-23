using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Statements
{
    /// <summary>
    /// Node which contains an CommentStatement
    /// </summary>
    public class CommentStatement : Statement
    {
        #region Private Members

        private String CommentString;

        #endregion

        #region Public Methods

        /// <summary>
        /// Set commentstring of CommentStatement
        /// </summary>
        /// <param name="expression">Expression to set</param>
        public void SetCommentString(String commentString)
        {
            CommentString = commentString;
        }

        /// <summary>
        /// Get commentstring of CommentStatement
        /// </summary>
        /// <returns>CommentString</returns>
        public String GetCommentString()
        {
            return CommentString;
        }

        /// <summary>
        /// Get string representation of CommentStatement
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            return "comment " + CommentString;
        }

        #endregion
    }
}
