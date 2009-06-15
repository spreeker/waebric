using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Parser.Ast.Predicates;
using Lexer.Tokenizer;

namespace Parser
{
    /// <summary>
    /// Parser for Expressions
    /// </summary>
    public class ExpressionParser : AbstractParser
    {
        public ExpressionParser(TokenIterator iterator, List<Exception> exceptionList)
            : base(iterator, exceptionList)
        {
        }

        /// <summary>
        /// Parse an expression
        /// </summary>
        /// <returns>Parsed Expression</returns>
        public Expression ParseExpression()
        {
            return null;
        }
    }
}
