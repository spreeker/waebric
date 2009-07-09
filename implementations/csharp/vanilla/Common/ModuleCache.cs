using System;
using System.Collections.Generic;
using System.Collections;
using System.Linq;
using System.Text;
using System.IO;
using Parser.Ast.Module;
using Lexer;
using Parser;
using Parser.Ast;

namespace Common
{
    /// <summary>
    /// Class which stores/caches all parsed modules
    /// </summary>
    public class ModuleCache
    {
        #region Private Members

        private static readonly ModuleCache ModuleCacheInstance = new ModuleCache();
        private Hashtable ModuleTable;
        private String DirectoryPath;

        #endregion

        #region Public Methods

        /// <summary>
        /// Instance of ModuleCache singleton
        /// </summary>
        public static ModuleCache Instance
        {
            get
            {
                return ModuleCacheInstance;
            }
        }

        public void SetDirectoryPath(String directoryPath)
        {
            DirectoryPath = directoryPath;
        }

        /// <summary>
        /// Request an specified module
        /// </summary>
        /// <param name="indentifier">ModuleId of requested module</param>
        /// <returns>Requested module if available</returns>
        public Module RequestModule(ModuleId identifier)
        {
            if (ModuleTable.Contains(identifier.ToString()))
            {   //Module already loaded so return instance of module
                return (Module) ModuleTable[identifier];
            }

            //Module not cached, so load it
            StreamReader moduleStream = new StreamReader(GetPath(identifier));
            
            //Lexicalize and parse it
            WaebricLexer lexer = new WaebricLexer(moduleStream);
            lexer.LexicalizeStream();
            WaebricParser parser = new WaebricParser(lexer.GetTokenIterator());
            parser.Parse();

            //Get module of tree
            SyntaxTree tree = parser.GetTree();

            //Add module to hashtable
            Module requestedModule = tree.GetRoot();
            ModuleTable.Add(requestedModule.GetModuleId().ToString(), requestedModule);
            
            return requestedModule;
        }

        /// <summary>
        /// Request dependencies of specified modules
        /// </summary>
        /// <param name="module"></param>
        public void RequestDependencies(Module module, List<Module> list)
        {
            //Check if module is already in Table
            if (!ModuleTable.Contains(module.GetModuleId().ToString()))
            {
                list.Add(module);
                ISyntaxNode[] importArray = module.GetImports().ToArray();

                foreach(Import import in importArray)
                {
                    if(!ModuleTable.Contains(import.GetModuleId().ToString()))
                    {
                        //Get module
                        Module requestedDependency = RequestModule(import.GetModuleId());
                        if (requestedDependency != null)
                        {   //Not null, so add to list
                            list.Add(requestedDependency);

                            //Get dependencies of retrieved module
                            RequestDependencies(requestedDependency, list);
                        }
                    }
                }
            }
        }

        /// <summary>
        /// Get dependencies of module and return a list of founded dependencies
        /// </summary>
        /// <param name="module">Module to get dependencies from</param>
        /// <returns>List of dependencies</returns>
        public List<Module> RequestDependencies(Module module)
        {
            List<Module> moduleList = new List<Module>();
            RequestDependencies(module, moduleList);
            return moduleList;
        }

        /// <summary>
        /// Add an specific module to the modulecache
        /// </summary>
        /// <param name="module"></param>
        public void AddModuleToCache(Module module)
        {
            ModuleTable.Add(module.GetModuleId().ToString(), module);
        }

        /// <summary>
        /// Clear the ModuleCache
        /// </summary>
        public void ClearModuleCache()
        {
            ModuleTable.Clear();
        }

        /// <summary>
        /// Check if ModuleCache contains a specified module
        /// </summary>
        /// <param name="module">ModuleId of module</param>
        /// <returns>True if module is in Cache, otherwise false</returns>
        public bool ContainsModule(ModuleId module)
        {
            return ModuleTable.Contains(module.ToString());
        }

        /// <summary>
        /// Check if ModuleCache contains a specified module
        /// </summary>
        /// <param name="moduleString">String representation of ModuleId</param>
        /// <returns>True if module is in Cache, otherwise false</returns>
        public bool ContainsModule(String moduleString)
        {
            return ModuleTable.Contains(moduleString);
        }

        /// <summary>
        /// Determine relative path of specified identifier
        /// </summary>
        /// <param name="identifier">Identifier of module</param>
        /// <returns>Path of module</returns>
        public String GetPath(ModuleId identifier)
        {
            String[] identifierList = identifier.GetIdentifiers().ToArray();
            String path = DirectoryPath;

            //Create path
            for (int i = 0; i <= (identifierList.Length - 1); i++)
            {
                path += identifierList[i];
                if (i != (identifierList.Length - 1))
                {
                    path += "/";
                }
            }

            //Add extension
            path += ".wae";

            return path;
        }

        #endregion

        #region Private Methods

        /// <summary>
        /// Private constructor for singleton
        /// </summary>
        private ModuleCache()
        {
            ModuleTable = new Hashtable();
        }

        #endregion
    }
}
