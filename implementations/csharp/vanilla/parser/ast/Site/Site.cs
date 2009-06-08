using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Parser.Ast.Module;

namespace Parser.Ast.Site
{
    /// <summary>
    /// Site Node
    /// </summary>
    public class Site : ModuleElement
    {
        #region Private Members

        private MappingList Mappings;

        #endregion

        #region Public Methods

        /// <summary>
        /// Get mappings of site
        /// </summary>
        /// <returns>MappingList</returns>
        public MappingList GetMappings()
        {
            return Mappings;
        }

        /// <summary>
        /// Set mappings of site
        /// </summary>
        /// <param name="mappings">MappingList</param>
        public void SetMappings(MappingList mappings)
        {
            this.Mappings = mappings;
        }

        #endregion

    }
}
