using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Lexer.Tokenizer;
using Parser.Ast.Module;
using Parser.Ast.Basic;

namespace Parser
{
    /// <summary>
    /// Parser which parses a module
    /// </summary>
    public class ModuleParser : AbstractParser
    {
        #region Private Members

        private SiteParser siteParser;

        #endregion

        #region Public Methods

        /// <summary>
        /// Create a ModuleParser
        /// </summary>
        /// <param name="tokenStream"></param>
        /// <param name="exceptionList"></param>
        public ModuleParser(TokenIterator tokenStream, List<Exception> exceptionList) : base(tokenStream, exceptionList)
        {
            //Create parsers for sub elements in a module
            siteParser = new SiteParser(tokenStream, exceptionList);
        }

        /// <summary>
        /// Parse more than one module
        /// </summary>
        /// <param name="modules">Modules to parse</param>
        public ModuleList ParseModules()
        {
            ModuleList modules = new ModuleList();

            //Parse modules until no new modules are found
            while (TokenStream.HasNext())
            {
                CurrentToken = TokenStream.NextToken();
                if (MatchValue(CurrentToken.GetValue().ToString(), Waebric.WaebricKeyword.MODULE.ToString()))
                {
                    //New module found so create new module object and start parsing it
                    Module module = ParseModule();
                    modules.Add(module);
                }
                else
                {
                    //Exception handling here
                }
            }
            return modules;
        }

        /// <summary>
        /// Parse one module
        /// </summary>
        /// <param name="module">Module to parse</param>
        public Module ParseModule()
        {
            Module module = new Module();
            //Parse first the identifier of the module and set it
            ModuleId moduleIdentifier = ParseModuleId();
            module.SetModuleId(moduleIdentifier);
            
            //Look for elements like SITE, DEF, etc
            while (TokenStream.HasNext())
            {
                CurrentToken = TokenStream.NextToken();
                //TODO: ADD breaking condition here (END of module found)
                //Check for different elements which may appear in a module
                if(MatchValue(CurrentToken.GetValue().ToString(), Waebric.WaebricKeyword.DEF.ToString()))
                {   //Function definition found
                    module.AddFunctionDefinition(null);
                }
                else if(MatchValue(CurrentToken.GetValue().ToString(), Waebric.WaebricKeyword.SITE.ToString()))
                {   //Site definition found, call siteparser
                    module.AddSite(siteParser.ParseSite());
                }
                else if(MatchValue(CurrentToken.GetValue().ToString(), Waebric.WaebricKeyword.IMPORT.ToString()))
                {   //Imports found
                    module.AddImport(ParseImport());
                }
                else
                {
                    //Exception handling here
                }
            }
            //parse identifiers and so on

            return module;
        }

        /// <summary>
        /// Parse a module identifier
        /// </summary>
        /// <param name="identifier">Identifier to parse</param>
        public ModuleId ParseModuleId()
        {
            ModuleId moduleId = new ModuleId();
            //parse single identifier
            if (NextToken("module identifier", "identifier", TokenType.IDENTIFIER))
            {
                moduleId.SetIdentifier(new IdentifierCon(CurrentToken.GetValue().ToString()));
            }
            else
            {
                //Raise exception
            }

            return moduleId;
        }

        /// <summary>
        /// Parse import
        /// </summary>
        /// <returns></returns>
        public Import ParseImport()
        {
            Import import = new Import();
            if (NextToken("import identifier", "identifier", TokenType.IDENTIFIER))
            {
                import.SetIdentifier(new ModuleId(new IdentifierCon(CurrentToken.GetValue().ToString())));
            }
            else
            {
                //raise exception
            }
            return import;
        }

        #endregion

    }
}
