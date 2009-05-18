using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Lexer.Waebric
{
    public enum TokenType
    {
        /* IDENTIFIER: Everything that identifies something:
         * module modulename, where modulename is the identifier
         */
        IDENTIFIER,

        /* NUMBER: Everything that is a numeric value:
         * 123 //a number
         */
        NUMBER,

        /* STRING: Everything that is between double quotes:
         * "this is a string" 
         */
        STRING,

        /* SYMBOL: Everything that is directly after a single ':
         * 'symbol
         */
        SYMBOL,

        /* LIST: A list of Numbers, Strings, Symbols and Lists:
         * [123, "abc", ' sym] 
         */
        LIST,

        /* RECORD: 
         * { name : "John Smith", age: 30 } //a record
         */ 
        RECORD,

        /* LITERAL:
         * In module modulename, the keyword module is a literal
         */ 
        LITERAL
    };
}
