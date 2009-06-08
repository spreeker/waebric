using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Lexer.Tokenizer;

namespace Parser
{
    public class FunctionParser : AbstractParser
    {
        public FunctionParser(TokenIterator tokenStream, List<Exception> exceptionList) : base(tokenStream, exceptionList)
        {

        }
    }
}
