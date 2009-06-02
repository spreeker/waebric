using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Parser.Ast;
using Lexer.Tokenizer;

namespace Parser
{
    /// <summary>
    /// WaebricParser, a parser which parses waebric recursive
    /// </summary>
    public class WaebricParser : AbstractParser
    {
        //MODULEPARSER SHOULD BE DECLARED HERE
        private SyntaxTree tree;

        /// <summary>
        /// Creates a WaebricParser
        /// </summary>
        /// <param name="tokenStream"></param>
        public WaebricParser(TokenIterator tokenStream)
            : base(tokenStream, new List<Exception>())
        {
        }

        /// <summary>
        /// Parses a waebric program an returns a list of exceptions raised during parsing
        /// </summary>
        /// <returns></returns>
        public List<Exception> Parse()
        {
            //Clear exceptions before start parsing
            ExceptionList.Clear();

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
    }
}
