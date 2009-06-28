using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Module
{
    /// <summary>
    /// ModuleIdentifier
    /// </summary>
    public class ModuleId : ISyntaxNode
    {
        #region Private Members

        private String Identifier;

        #endregion

        #region Public Methods

        /// <summary>
        /// Create ModuleId without setting identifier
        /// </summary>
        public ModuleId()
        {
        }

        /// <summary>
        /// Get identifier
        /// </summary>
        /// <returns>Identifier</returns>
        public String GetIdentifier()
        {
            return Identifier;
        }

        /// <summary>
        /// Set identifier
        /// </summary>
        /// <param name="identifier">Identifier to set</param>
        public void SetIdentifier(String identifier)
        {
            this.Identifier = identifier;   
        }

        public override String ToString()
        {
            return Identifier;
        }

        #endregion
    }
}
