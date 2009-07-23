using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Lexer.Tokenizer;

namespace Parser.Exceptions
{
    /// <summary>
    /// Class which implements an UnexpectedToken exception
    /// </summary>
    public class UnexpectedToken : Exception
    {

        public UnexpectedToken(String message, String found, int location)
        {
            Console.WriteLine(message + " " + found + " at line " + location.ToString());
        }
    }
}
