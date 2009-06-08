using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Lexer.Tokenizer;
using Parser.Ast.Site;

namespace Parser
{
    /// <summary>
    /// Parser which parses a site
    /// </summary>
    public class SiteParser : AbstractParser
    {
        public SiteParser(TokenIterator tokenStream, List<Exception> exceptionList) : base(tokenStream, exceptionList)
        {

        }

        /// <summary>
        /// Parse a site
        /// </summary>
        /// <returns></returns>
        public Site ParseSite()
        {
            return null;
        }

        /// <summary>
        /// Parse mappings
        /// TODO: NEEDS A RETURN TYPE
        /// </summary>
        public void ParseMappings()
        {

        }

    }
}
