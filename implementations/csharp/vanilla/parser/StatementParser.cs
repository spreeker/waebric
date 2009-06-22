using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Lexer.Tokenizer;
using Parser.Ast.Statements;

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

        /// <summary>
        /// Parser for Statements
        /// </summary>
        /// <returns>Parsed Statements</returns>
        public Statement ParseStatement()
        {
            return new Statement();
        }

        #endregion
    }
}
