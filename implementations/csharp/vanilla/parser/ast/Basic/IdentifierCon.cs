using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Basic
{
    /// <summary>
    /// Class which holds an identifier
    /// Identifier exists of following:
    /// Head: [A-Za-z]
    /// Tail: [A-Za-z\-0-9]*
    /// </summary>
    public class IdentifierCon : ISyntaxNode
    {
        #region Private Members

        private String IdCon;

        #endregion

        #region Public Methods

        public IdentifierCon(String identifier)
        {
            IdCon = identifier;
        }

        /// <summary>
        /// Get Identifier
        /// </summary>
        /// <returns>Identifier</returns>
        public override String ToString()
        {
            return IdCon;
        }

        /// <summary>
        /// Set identifier
        /// </summary>
        /// <param name="identifier">Identifier to set</param>
        public void SetIdCon(String identifier)
        {
            IdCon = identifier;
        }

        #endregion


    }
}
