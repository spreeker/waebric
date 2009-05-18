using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Lexer.Waebric;

namespace Lexer
{
    class Token
    {
        private Object value;
        private TokenType type;
        private int line;

        /**
         * Get value of token
         */
        public Object GetValue()
        {
            return value;
        }
        
        /**
         * Set value of token
         */
        public void SetValue(Object value)
        {
            this.value = value;
        }

        /**
         * Get type of token
         */
        public TokenType GetType()
        {
            return type;
        }

        /**
         * Set type of token
         */
        public void SetType(TokenType type)
        {
            this.type = type;
        }
        
        /**
         * Get line number of token
         */
        public int GetLine()
        {
            return line;
        }

        /**
         * Set line number of token
         */
        public void SetLine(int line)
        {
            this.line = line;
        }

        
    }
}
