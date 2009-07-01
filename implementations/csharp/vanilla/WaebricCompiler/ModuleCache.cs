using System;
using System.Collections.Generic;
using System.Collections;
using System.Linq;
using System.Text;
using Parser.Ast.Module;
using System.IO;
using Lexer;
using Parser;
using Parser.Ast;

namespace WaebricCompiler
{
    /// <summary>
    /// Class which stores/caches all parsed modules
    /// </summary>
    public class ModuleCache
    {
        #region Private Members

        private static readonly ModuleCache ModuleCacheInstance = new ModuleCache();
        private Hashtable ModuleTable;

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

        /// <summary>
        /// Request an specified module
        /// </summary>
        /// <param name="indentifier">ModuleId of requested module</param>
        /// <returns>Requested module if available</returns>
        public Module RequestModule(ModuleId identifier)
        {
            if (ModuleTable.Contains(identifier))
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
            ModuleTable.Add(requestedModule.GetModuleId(), requestedModule);
            
            return requestedModule;
        }

        /// <summary>
        /// Request dependencies of specified modules
        /// </summary>
        /// <param name="module"></param>
        public void RequestDependencies(Module module)
        {
            Import[] importArray = module.GetImports().ToArray();

            //Retrieve dependencies
            for (int i = 0; i < (importArray.Length - 1); i++)
            {
                //Get module
                Module requestedDependency = RequestModule(importArray[i].GetModuleId());
                
                //Get dependencies of retrieved module
                RequestDependencies(requestedDependency);
            }
        }

        /// <summary>
        /// Add an specific module to the modulecache
        /// </summary>
        /// <param name="module"></param>
        public void AddModuleToCache(Module module)
        {
            ModuleTable.Add(module.GetModuleId(), module);
        }

        /// <summary>
        /// Clear the ModuleCache
        /// </summary>
        public void ClearModuleCache()
        {
            ModuleTable.Clear();
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

        /// <summary>
        /// Determine relative path of specified identifier
        /// </summary>
        /// <param name="identifier"></param>
        /// <returns></returns>
        private String GetPath(ModuleId identifier)
        {
            String[] identifierList = identifier.GetIdentifiers().ToArray();
            String path = "";

            //Create path
            for (int i = 0; i <= (identifierList.Length - 1); i++)
            {
                path += identifierList[i];
                if(i != (identifierList.Length - 1))
                {
                    path += "/";
                }
            }
            
            //Add extension
            path += ".wae";

            return path;
        }

        #endregion
    }
}
