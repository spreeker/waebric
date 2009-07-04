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
    public class Site : ISyntaxNode
    {
        #region Private Members

        private NodeList MappingList;

        #endregion

        #region Public Methods

        public Site()
        {
            //Initalize containers
            MappingList = new NodeList();
        }

        /// <summary>
        /// Get mappings of site
        /// </summary>
        /// <returns>List of mappings</returns>
        public NodeList GetMappings()
        {
            return MappingList;
        }

        /// <summary>
        /// Add range of mappings
        /// </summary>
        /// <param name="mappings">MappingList</param>
        public void AddMappings(NodeList mappings)
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

        public void AcceptVisitor(ISyntaxNodeVisitor visitor)
        {
            visitor.Visit(this);
        }

        public ISyntaxNode[] GetSubNodes()
        {
            return new ISyntaxNode[] { };
        }

        #endregion

    }
}
