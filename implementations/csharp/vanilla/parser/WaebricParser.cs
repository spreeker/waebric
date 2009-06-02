using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Parser.Ast;
using Lexer.Tokenizer;
using Parser.Ast.Module;

namespace Parser
{
    /// <summary>
    /// WaebricParser, a parser which parses waebric recursive
    /// </summary>
    public class WaebricParser : AbstractParser
    {
        #region Private Members

        private ModuleParser moduleParser;
        private SyntaxTree tree = new SyntaxTree();

        #endregion

        #region Public Methods

        /// <summary>
        /// Creates a WaebricParser
        /// </summary>
        /// <param name="tokenStream"></param>
        public WaebricParser(TokenIterator tokenStream)
            : base(tokenStream, new List<Exception>())
        {
            moduleParser = new ModuleParser(tokenStream, ExceptionList);
        }

        /// <summary>
        /// Parses a waebric program an returns a list of exceptions raised during parsing
        /// </summary>
        /// <returns></returns>
        public List<Exception> Parse()
        {
            //Clear exceptions before start parsing
            ExceptionList.Clear();

            //Make new ModuleList to store modules in and add it to root of tree
            ModuleList moduleList = new ModuleList();
            tree.SetRoot(moduleList);

            //TODO: ModuleParser must called here

            return ExceptionList;
        }

        /// <summary>
        /// Retrieves the AST of the program if succesfully parsed.
        /// </summary>
        /// <returns>AST or null if nothing parsed or exception raised</returns>
        public SyntaxTree GetTree()
        {
            return tree;
        }

        #endregion
    }
}
