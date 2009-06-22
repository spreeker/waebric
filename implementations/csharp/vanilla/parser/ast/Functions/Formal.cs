using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Functions
{
    /// <summary>
    /// Node which contains a Formal
    /// </summary>
    public class Formal : ISyntaxNode
    {
        #region Private Members

        private String Identifier;

        #endregion

        #region Public Methods

        /// <summary>
        /// Set identifier of Formal
        /// </summary>
        /// <param name="identifier">Identifier to set</param>
        public void SetIdentifier(String identifier)
        {
            Identifier = identifier;
        }

        /// <summary>
        /// Get identifier of Formal
        /// </summary>
        /// <returns>Identifier</returns>
        public String GetIdentifier()
        {
            return Identifier;
        }

        /// <summary>
        /// Get string representation of Formal
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            return Identifier;
        }

        #endregion

    }
}
