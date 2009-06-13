using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Lexer.Tokenizer;
using Parser.Ast.Site;

namespace Parser
{
    /// <summary>
    /// Parser for markup
    /// </summary>
    public class MarkupParser : AbstractParser
    {
        #region Public Methods

        public MarkupParser(TokenIterator iterator, List<Exception> exceptionList)
            : base(iterator, exceptionList)
        {

        }

        /// <summary>
        /// Parser for markup
        /// </summary>
        /// <returns>Parsed markup</returns>
        public Markup ParseMarkup()
        {
            return null;
        }

        #endregion

    }
}
