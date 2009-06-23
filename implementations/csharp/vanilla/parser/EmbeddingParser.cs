using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Lexer.Tokenizer;

namespace Parser
{
    public class EmbeddingParser : AbstractParser
    {
        #region Private Members

        MarkupParser markupParser;

        #endregion

        #region Public Methods

        public EmbeddingParser(TokenIterator tokenStream, List<Exception> exceptionList)
            : base(tokenStream, exceptionList)
        {
            //Create subparser
            markupParser = new MarkupParser(tokenStream, exceptionList);
        }

        #endregion
    }
}
