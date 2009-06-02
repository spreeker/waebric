using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Module
{
    /// <summary>
    /// Module node
    /// </summary>
    public class Module : ISyntaxNode
    {

        #region Private Methods

        private ModuleId Identifier;
        private ModuleElementList Elements;

        #endregion

        #region Public Methods
        
        /// <summary>
        /// Create a new module
        /// </summary>
        public Module()
        {
            Elements = new ModuleElementList();
        }

        /// <summary>
        /// Set an element for this module instance
        /// </summary>
        /// <param name="element">Element to add</param>
        public void SetElement(ModuleElement element)
        {
            Elements.Add(element);
        }

        /// <summary>
        /// Set identifier of this module
        /// </summary>
        /// <param name="identifier">Identifier to set</param>
        public void SetIdentifier(ModuleId identifier)
        {
            this.Identifier = identifier;
        }

        /// <summary>
        /// Get identifier of this module
        /// </summary>
        /// <returns>Identifier of module if exists</returns>
        public ModuleId GetIdentifier()
        {
            return Identifier;
        }


        /// <summary>
        /// Get elements of module
        /// </summary>
        /// <returns>Array of Element Nodes</returns>
        public ISyntaxNode[] GetElements()
        {
            return Elements.GetElements();
        }

        #endregion
    }
}
