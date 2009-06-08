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
            //Create markup parser here
        }

        /// <summary>
        /// Parse a site
        /// </summary>
        /// <returns></returns>
        public Site ParseSite()
        {
            //One or more mappings separated by ;
            Site site = new Site();
            NextToken("Mapping", "mapping1;mapping2");
            
           
            return site;
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
