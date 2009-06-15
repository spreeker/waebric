using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Lexer.Tokenizer;

namespace Parser
{
    /// <summary>
    /// Parser for statements
    /// </summary>
    public class StatementParser : AbstractParser
    {
        #region Private Members

        #endregion

        #region Public Methods


        public StatementParser(TokenIterator iterator, List<Exception> exceptionList)
            : base(iterator, exceptionList)
        {

        }

        #endregion
    }
}
