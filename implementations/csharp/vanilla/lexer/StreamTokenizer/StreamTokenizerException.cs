using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Lexer.Tokenizer
{
    /// <summary>
    /// Class used for exceptions for StreamTokenizer
    /// </summary>
    public class StreamTokenizerException : Exception
    {
        public StreamTokenizerException(String message, int lineNumber)
        {
            System.Console.WriteLine("Unexpected error: " + message + " at " + lineNumber);
        }
    }
}
