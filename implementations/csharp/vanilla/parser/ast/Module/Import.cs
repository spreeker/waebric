using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Module
{
    /// <summary>
    /// Class which contains import
    /// </summary>
    public class Import : ModuleElement
    {
        #region Private Members

        private ModuleId Identifier;

        #endregion

        #region Public Methods

        public Import()
        {

        }
        
        /// <summary>
        /// Get identifier of import
        /// </summary>
        /// <returns>Identifier</returns>
        public ModuleId GetIdentifier()
        {
            return Identifier;
        }

        /// <summary>
        /// Set identifier of import
        /// </summary>
        /// <param name="identifier">Identifier for this import</param>
        public void SetIdentifier(ModuleId identifier)
        {
            this.Identifier = identifier;
        }

        #endregion
    }
}
