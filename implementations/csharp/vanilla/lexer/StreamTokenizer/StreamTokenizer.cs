using System;
using System.IO;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Lexer.Tokenizer
{
   

    /// <summary>
    /// StreamTokenizer class tokenizes an StreamReader input
    /// </summary>
    class StreamTokenizer
    {
        #region Constants

        //Constants
        public const int ST_EOF = 1200; //End of file
        public const int ST_EOL = 254; //End of line
        public const int ST_NUMBER = 253; //Numeric token
        public const int ST_WORD = 252; //Word token
        public const int ST_OTHER = 251; //Other tokens like symbols, etc

        #endregion

        #region Private Members

        private double NumValue = 0.0; // Numeric value of token
        private String TextValue = ""; // Text value of token
        private TextReader InputStream;
        private int LineNumber = 1; // Linenumber of stream
        private int[] WhitespaceCharacters; // Array of Whitespace characters

        #endregion

        #region Public Methods

        /// <summary>
        /// Initialize StreamTokenizer with inputstream to tokenize
        /// </summary>
        /// <param name="inputStream">InputStream to tokenize</param>
        public StreamTokenizer(TextReader inputStream)
        {
            this.InputStream = inputStream;
        }

        /// <summary>
        /// Set whitespacecharacters to ignore
        /// </summary>
        /// <param name="WhitespaceChars">Array of whitespace characters</param>
        public void WhitespaceChars(int[] WhitespaceChars)
        {
            this.WhitespaceCharacters = WhitespaceChars;
        }


        /// <summary>
        /// Retrieves NextToken from stream
        /// </summary>
        /// <returns>Type of token or character of symbol found</returns>
        public int NextToken()
        {
            //Reset all values
            NumValue = 0.0;
            TextValue = "";
            char peek = (char)InputStream.Peek();
            int status = InputStream.Read();
            while (status != -1) //Read tokens until end of stream has reached
            {
                if (IsWhitespace(peek)) //Ignore whitespace
                {
                    continue;
                }
                else if (peek == '\n') //Newline hit
                {
                    LineNumber++;
                    return ST_EOL;
                }
                else
                {
                    break; //We found something interesting so analyze it
                }
                peek = (char)InputStream.Peek();
                status = InputStream.Read();
            }
            if (status == -1)
            {
                return ST_EOF; //End of stream reached
            }
            if (Char.IsDigit(peek)) //We are dealing with a numeric value
            {
                double v = 0;
                do
                {
                    v = 10 * v + Char.GetNumericValue(peek);
                    peek = (char)InputStream.Peek();
                    InputStream.Read();
                } while (Char.IsDigit(peek));
                if (peek != '.') //end of number so return number
                {
                    NumValue = v;
                    return ST_NUMBER;
                }
                // we have an floating point value
                double x = v; double d = 10;
                for (; ; )
                {
                    peek = (char)InputStream.Peek();
                    InputStream.Read();
                    if (!Char.IsDigit(peek))
                    {
                        break; //read complete number
                    }
                    x = x + Char.GetNumericValue(peek) / d;
                    d = d * 10;                    
                }
                NumValue = v;
                return ST_NUMBER; 
            }

            if (Char.IsLetter(peek)) //We are dealing with a letter
            {
                StringBuilder buffer = new StringBuilder();
                do
                { //Create string while we are dealing with letters or digits
                    buffer.Append(peek);
                    peek = (char)InputStream.Peek();
                    InputStream.Read();
                } while (Char.IsLetterOrDigit(peek));

                TextValue = buffer.ToString();
                return ST_WORD;
            }

            if(peek == '\"') // Is a quote
            { 
                StringBuilder buffer = new StringBuilder();
                //Get all text before new quote sign has been detected
                //TODO: Implement escape characters
                do
                {
                    buffer.Append(peek);
                    peek = (char)InputStream.Peek();
                    InputStream.Read();

                } while (Char.IsLetterOrDigit(peek) || IsWhitespace(peek) || IsNonQuoteSymbol(peek));
                if (peek == '\"') //Quote found
                {
                    buffer.Append(peek);
                }
                else
                {
                    //throw new Exception("No end of quote found at line " + LineNumber);
                }
                TextValue = buffer.ToString();
                return '\"';
            }
            if(IsSymbol(peek)) //other symbol
            {
                TextValue = peek.ToString();
                return peek;
            }
            throw new StreamTokenizerException("Token cannot being matched: " + peek, LineNumber);
        }

        /// <summary>
        /// Get number of scanned lines of stream
        /// </summary>
        /// <returns>Number of scanned lines</returns>
        public int GetScannedLines()
        {
            return LineNumber;
        }

        /// <summary>
        /// Get numeric value of last scanned token
        /// </summary>
        /// <returns>Numeric (double) value</returns>
        public double GetNumericValue()
        {
            return NumValue;
        }

        /// <summary>
        /// Get textual value of last scanned token
        /// </summary>
        /// <returns>String value</returns>
        public String GetTextValue()
        {
            return TextValue;
        }

        #endregion

        #region Private Methods

        /// <summary>
        /// Checks if character is a whitespace character
        /// </summary>
        /// <param name="c">Character to check</param>
        /// <returns>True if character is a whitespace character, otherwise false</returns>
        private bool IsWhitespace(char c)
        {
            if (WhitespaceCharacters == null)
            {
                return false;
            }
            for(int i = 0; i < WhitespaceCharacters.Length; i++)
            {
                if (c == (char)WhitespaceCharacters[i])
                {
                    return true;
                }
            }
            return false;
        }

        /// <summary>
        /// Checks if character is a symbol
        /// </summary>
        /// <param name="c">Character to check</param>
        /// <returns>True if character is symbol, otherwise false</returns>
        private bool IsSymbol(char c) 
        {
            return c > (int)32 && c < (int)126;
        }

        /// <summary>
        /// Looks up if this character is a symbol but not a " character (used for quote detection)
        /// </summary>
        /// <param name="c">Character to check</param>
        /// <returns>True if character is a symbol and not a ", otherwise false</returns>
        private bool IsNonQuoteSymbol(char c)
        {
            return c > (int)32 && c < (int)126 && c != '\"';
        }

        #endregion
    }
}
