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
        
        //HERE SHOULD ALL IMPORTS, MODULEELEMENTS AND MODULES BEING DECLARED

        #endregion

        #region Public Methods

        /// <summary>
        /// Create a ModuleParser
        /// </summary>
        /// <param name="tokenStream"></param>
        /// <param name="exceptionList"></param>
        public ModuleParser(TokenIterator tokenStream, List<Exception> exceptionList) : base(tokenStream, exceptionList)
        {
            //Create parsers for other items, like site and so on
        }

        /// <summary>
        /// Parse more than one module
        /// </summary>
        /// <param name="modules">Modules to parse</param>
        public void Parse(ModuleList modules)
        {
            //Parse modules until no new modules are found
            while (TokenStream.HasNext())
            {
                CurrentToken = TokenStream.NextToken();
                if (Match(CurrentToken.GetValue(), Waebric.WaebricKeyword.MODULE))
                {
                    //New module found so create new module object and start parsing it
                    Module module = new Module();
                    Parse(module);
                    modules.Add(module);
                }
                else
                {
                    //Exception handling here
                }
            }
        }

        /// <summary>
        /// Parse one module
        /// </summary>
        /// <param name="module">Module to parse</param>
        public void Parse(Module module)
        {
            //Parse first the identifier of the module and set it
            ModuleId moduleIdentifier = new ModuleId();
            Parse(moduleIdentifier);
            module.SetIdentifier(moduleIdentifier);
            
            //Look for elements like SITE, DEF, etc
            while (TokenStream.HasNext())
            {
                //Breaking condition, if empty module found t

                CurrentToken = TokenStream.NextToken();
                //Check for different elements which may appear in a module
                if(Match(CurrentToken.GetValue(), Waebric.WaebricKeyword.DEF))
                {   //Function definition found

                }
                else if(Match(CurrentToken.GetValue(), Waebric.WaebricKeyword.SITE))
                {   //Site definition found
                }
                else if(Match(CurrentToken.GetValue(), Waebric.WaebricKeyword.IMPORT))
                {   //Imports found

                }
                else
                {
                    //Exception handling here
                }
                            
            }
            //parse identifiers and so on
        }

        /// <summary>
        /// Parse a module identifier
        /// </summary>
        /// <param name="identifier">Identifier to parse</param>
        public void Parse(ModuleId identifier)
        {
            //parse single identifier
            if(NextToken("module identifier", "identifier", TokenType.IDENTIFIER))
            {
                identifier.SetIdentifier(new IdentifierCon(CurrentToken.GetValue().ToString()));
            }
        }

        #endregion

    }
}
