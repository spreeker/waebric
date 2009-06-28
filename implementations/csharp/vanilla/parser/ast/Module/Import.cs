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

        private ModuleId ModuleIdentifier;

        #endregion

        #region Public Methods

        public Import()
        {

        }
        
        /// <summary>
        /// Get identifier of import
        /// </summary>
        /// <returns>Identifier</returns>
        public ModuleId GetModuleId()
        {
            return ModuleIdentifier;
        }

        /// <summary>
        /// Set identifier of import
        /// </summary>
        /// <param name="identifier">Identifier for this import</param>
        public void SetModuleId(ModuleId identifier)
        {
            ModuleIdentifier = identifier;
        }

        public override String ToString()
        {
            return "import " + ModuleIdentifier.ToString();
        }

        #endregion
    }
}
