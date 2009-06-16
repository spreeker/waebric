using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Markup
{
    /// <summary>
    /// Node containing Markup
    /// </summary>
    public class Markup : ISyntaxNode
    {
        #region Private Members

        private Arguments Args;
        private Designator Tag;

        #endregion

        #region Public Methods

        public Markup()
        {
            //Intialize arguments (TO REMOVE)
            Args = new Arguments();
        }

        /// <summary>
        /// Set Designator tag
        /// </summary>
        /// <param name="tag">Tag to set</param>
        public void SetDesignator(Designator tag)
        {
            Tag = tag;
        }

        /// <summary>
        /// Get Designator tag
        /// </summary>
        /// <returns>Tag</returns>
        public Designator GetDesignator()
        {
            return Tag;
        }

        /// <summary>
        /// Add arguments to markup
        /// </summary>
        /// <param name="argument">Arguments to add</param>
        public void AddArguments(Arguments arguments)
        {
            Args = arguments;
        }

        /// <summary>
        /// Get arguments
        /// </summary>
        /// <returns>Arguments</returns>
        public Arguments GetArguments()
        {
            return Args;
        }

        #endregion

    }
}
