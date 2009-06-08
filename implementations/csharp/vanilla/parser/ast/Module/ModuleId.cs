using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Parser.Ast.Basic;

namespace Parser.Ast.Module
{
    /// <summary>
    /// ModuleIdentifier
    /// </summary>
    public class ModuleId : ISyntaxNode
    {
        #region Private Members

        private IdentifierCon Identifier;

        #endregion

        #region Public Methods

        /// <summary>
        /// Create ModuleId with setting identifier
        /// </summary>
        /// <param name="identifier">Identifier to set</param>
        public ModuleId(IdentifierCon identifier)
        {
            this.Identifier = identifier;
        }

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
        public IdentifierCon GetIdentifier()
        {
            return Identifier;
        }

        /// <summary>
        /// Set identifier
        /// </summary>
        /// <param name="identifier">Identifier to set</param>
        public void SetIdentifier(IdentifierCon identifier)
        {
            this.Identifier = identifier;   
        }

        #endregion
    }
}
