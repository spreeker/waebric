using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Lexer.Tokenizer;
using Parser.Ast.Module;
using Parser.Exceptions;

namespace Parser
{
    /// <summary>
    /// Parser which parses a module
    /// </summary>
    public class ModuleParser : AbstractParser
    {
        #region Private Members

        private SiteParser siteParser;
        private FunctionParser functionParser;

        #endregion

        #region Public Methods

        /// <summary>
        /// Create a ModuleParser
        /// </summary>
        /// <param name="tokenStream"></param>
        /// <param name="exceptionList"></param>
        public ModuleParser(TokenIterator tokenStream) : base(tokenStream)
        {
            //Create parsers for sub elements in a module
            siteParser = new SiteParser(tokenStream);
            functionParser = new FunctionParser(tokenStream);
        }

        /// <summary>
        /// Parse one module
        /// </summary>
        /// <param name="module">Module to parse</param>
        public Module ParseModule()
        {
            Module module = new Module();

            //Skip module token
            NextToken("module", "module identifier", "module");

            //Parse first the identifier of the module and set it
            ModuleId moduleIdentifier = ParseModuleId();
            module.SetModuleId(moduleIdentifier);
            
            //Look for elements like SITE, DEF, etc
            while (TokenStream.HasNext())
            {
                if (MatchValue(TokenStream.Peek(1).GetValue().ToString(), Waebric.WaebricKeyword.MODULE.ToString()))
                {   //New module found, raise exception, only one module per file allowed
                    throw new UnexpectedToken("Unexpected token, found second module definition:", "module", TokenStream.Peek(1).GetLine());
                }
                CurrentToken = TokenStream.NextToken();
                

                //Check for different elements which may appear in a module
                if(MatchValue(CurrentToken.GetValue().ToString(), Waebric.WaebricKeyword.DEF.ToString()))
                {   //Function definition found
                    module.AddFunctionDefinition(functionParser.ParseFunctionDefinition());
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
                    //Exception
                    throw new UnexpectedToken("Unexpected token found:", CurrentToken.GetValue().ToString(), CurrentToken.GetLine());
                }
            }

            return module;
        }

        /// <summary>
        /// Parse a module identifier
        /// </summary>
        /// <param name="identifier">Identifier to parse</param>
        public ModuleId ParseModuleId()
        {
            ModuleId moduleId = new ModuleId();
            
            //parse module identifier
            while (TokenStream.HasNext())
            {
                if (NextToken("identifier", "module identifier.identifier", TokenType.IDENTIFIER))
                {
                    moduleId.AddIdentifier(CurrentToken.GetValue().ToString());
                }
                else
                {
                    //Raise exception
                    throw new UnexpectedToken("Unexpected token found:", CurrentToken.GetValue().ToString(), CurrentToken.GetLine());
                }

                if (TokenStream.HasNext() && TokenStream.Peek(1).GetValue().ToString() == ".")
                {   //Period, so another identifier will appear after this one
                    NextToken(".", "module identifier.identifier", '.');
                }
                else
                {
                    break; //No more module identifier stuff will appear
                }
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
            if (TokenStream.HasNext() && TokenStream.Peek(1).GetType() == TokenType.IDENTIFIER)
            {
                import.SetModuleId(ParseModuleId());
            }
            else
            {
                //Raise exception
                throw new UnexpectedToken("Unexpected token found:", CurrentToken.GetValue().ToString(), CurrentToken.GetLine());
            }
            return import;
        }

        #endregion

    }
}
