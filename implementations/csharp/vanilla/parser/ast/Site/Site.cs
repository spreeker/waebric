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

        private List<Mapping> MappingList;

        #endregion

        #region Public Methods

        public Site()
        {
            //Initalize containers
            MappingList = new List<Mapping>();
        }

        /// <summary>
        /// Get mappings of site
        /// </summary>
        /// <returns>List of mappings</returns>
        public List<Mapping> GetMappings()
        {
            return MappingList;
        }

        /// <summary>
        /// Add range of mappings
        /// </summary>
        /// <param name="mappings">MappingList</param>
        public void AddMappings(List<Mapping> mappings)
        {
            MappingList.AddRange(mappings);
        }

        /// <summary>
        /// Add mapping to site
        /// </summary>
        /// <param name="mapping">Mapping to add</param>
        public void AddMapping(Mapping mapping)
        {
            MappingList.Add(mapping);
        }

        #endregion

    }
}
