using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Markup
{
    /// <summary>
    /// Node which holds arguments
    /// </summary>
    public class Arguments : ISyntaxNode
    {
        #region Private Members

        private List<Argument> ArgumentList;

        #endregion

        #region Public Methods

        public Arguments()
        {
            ArgumentList = new List<Argument>();
        }

        /// <summary>
        /// Add argument
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
