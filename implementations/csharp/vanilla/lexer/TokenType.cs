using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Lexer.Tokenizer
{
    public enum TokenType : int
    {
        /* KEYWORD:
         * In module modulename, module is a keyword
         */ 
        KEYWORD,

        /* IDENTIFIER
         */
        IDENTIFIER,

        /* NUMBER: Everything that is a numeric value:
         * 123 //a number
         */
        NUMBER,

        /* TEXT: Everything that is between double quotes:
         * "this is a string" 
         */
        TEXT,

        /* SYMBOL: All other characters that can appear
         */
        SYMBOL        
    };
}
