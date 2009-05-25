using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Lexer.Tokenizer
{
    /// <summary>
    /// This class represents an token with an typeindication and a linenumber 
    /// from the original stream
    /// </summary>
    class Token
    {
        private Object Value;
        private TokenType Type;
        private int Line;

        /// <summary>
        /// Create a new token with all fields
        /// </summary>
        /// <param name="value">Value of token</param>
        /// <param name="type">Type of token</param>
        /// <param name="line">Linenumber of token in original stream</param>
        public Token(Object value, TokenType type, int line)
        {
            this.Value = value;
            this.Type = type;
            this.Line = line;
        }

        /// <summary>
        /// Create an empty token
        /// </summary>
        public Token()
        {

        }

        /// <summary>
        /// Get value of token
        /// </summary>
        /// <returns>Value</returns>
        public Object GetValue()
        {
            return Value;
        }
        
        /// <summary>
        /// Set value of token
        /// </summary>
        /// <param name="value">Value</param>
        public void SetValue(Object value)
        {
            this.Value = value;
        }

        /// <summary>
        /// Get type of token
        /// </summary>
        /// <returns>TokenType</returns>
        public TokenType GetType()
        {
            return Type;
        }

        /// <summary>
        /// Set type of token
        /// </summary>
        /// <param name="type">TokenType</param>
        public void SetType(TokenType type)
        {
            this.Type = type;
        }
        
        /// <summary>
        /// Get line number of token in original stream
        /// </summary>
        /// <returns>LineNumber</returns>
        public int GetLine()
        {
            return Line;
        }

        /// <summary>
        /// Set line number of token in original stream
        /// </summary>
        /// <param name="line">LineNumber</param>
        public void SetLine(int line)
        {
            this.Line = line;
        }

        
    }
}
