using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Lexer.Tokenizer;

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
    }
}
