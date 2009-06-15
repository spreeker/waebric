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

        private List<Argument> ArgumentList;
        private Designator Tag;

        #endregion

        #region Public Methods

        public Markup()
        {
            ArgumentList = new List<Argument>();
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
        /// Add argument to markup
        /// </summary>
        /// <param name="argument">Argument to add</param>
        public void AddArgument(Argument argument)
        {
            ArgumentList.Add(argument);
        }

        /// <summary>
        /// Get list of arguments
        /// </summary>
        /// <returns>ArgumentList</returns>
        public List<Argument> GetArguments()
        {
            return ArgumentList;
        }

        #endregion

    }
}
