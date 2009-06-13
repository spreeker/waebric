using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Parser.Ast.Functions;
using Parser.Ast.Site;

namespace Parser.Ast.Module
{
    /// <summary>
    /// Module node
    /// </summary>
    public class Module : ISyntaxNode
    {

        #region Private Methods

        private ModuleId Identifier;        //Identifier of this module
        private List<Site.Site> SiteList;        //List containing sites for this module
        private List<Import> ImportList;    //List of imports
        private List<FunctionDefinition> FunctionDefinitionList;//List of functions

        #endregion

        #region Public Methods
        
        /// <summary>
        /// Create a new module
        /// </summary>
        public Module()
        {
            //Initialize list containers
            SiteList = new List<Site.Site>();
            ImportList = new List<Import>();
            FunctionDefinitionList = new List<FunctionDefinition>();
        }

        /// <summary>
        /// Get a list of sites of this module
        /// </summary>
        /// <returns>List of sites</returns>
        public List<Site.Site> GetSites()
        {
            return SiteList;
        }

        /// <summary>
        /// Add site to module
        /// </summary>
        /// <param name="site">Site to add</param>
        public void AddSite(Site.Site site)
        {
            SiteList.Add(site);
        }

        /// <summary>
        /// Get a list of imports of this module
        /// </summary>
        /// <returns>List of imports</returns>
        public List<Import> GetImports()
        {
            return ImportList;
        }

        /// <summary>
        /// Add an import to module
        /// </summary>
        /// <param name="import">Import to add</param>
        public void AddImport(Import import)
        {
            ImportList.Add(import);
        }

        /// <summary>
        /// Get a list of functions of this module
        /// </summary>
        /// <returns>List of function defintions</returns>
        public List<FunctionDefinition> GetFunctionDefinitions()
        {
            return FunctionDefinitionList;
        }

        /// <summary>
        /// Add an 
        /// </summary>
        /// <param name="functionDefinition"></param>
        public void AddFunctionDefinition(FunctionDefinition functionDefinition)
        {
            FunctionDefinitionList.Add(functionDefinition);
        }

        /// <summary>
        /// Set identifier of this module
        /// </summary>
        /// <param name="identifier">Identifier to set</param>
        public void SetModuleId(ModuleId identifier)
        {
            this.Identifier = identifier;
        }

        /// <summary>
        /// Get identifier of this module
        /// </summary>
        /// <returns>Identifier of module if exists</returns>
        public ModuleId GetModuleId()
        {
            return Identifier;
        }

        #endregion
    }
}
