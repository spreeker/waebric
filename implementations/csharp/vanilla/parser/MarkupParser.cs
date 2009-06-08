using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Lexer.Tokenizer;

namespace Parser
{
    /// <summary>
    /// Parser for markup
    /// </summary>
    public class MarkupParser : AbstractParser
    {
        public MarkupParser(TokenIterator iterator, List<Exception> exceptionList)
            : base(iterator, exceptionList)
        {

        }
    }
}
