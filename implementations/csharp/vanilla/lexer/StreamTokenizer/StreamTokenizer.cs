using System;
using System.IO;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Lexer.StreamTokenizer
{
    /**
     * Class StreamTokenizer
     * Based on Java StreamTokenizer Interface
     */
    class StreamTokenizer
    {
        public static int EOF = 255; // End of Stream
        public static int NUMBER = 100; // Token is a number
        public static int WORD = 50; // Token is a word

        private StreamReader InputStream;
        private int LineNumber = 1; // Linenumber of stream
        private int TokenType = 0; // Type of last read token
        private double NumericValue = 0.0; // Numeric value of Token (if it is a number)
        private String StringValue = ""; // String value of Token (if it is a word)
        private int[] WhitespaceCharacters; // Array of Whitespace characters

        public StreamTokenizer(StreamReader InputStream)
        {
            this.InputStream = InputStream;
        }

        /*
         * Set all whitespace characters
         */
        public void WhitespaceChars(int[] WhitespaceChars)
        {
            this.WhitespaceCharacters = WhitespaceChars;
        }
        
        /**
         * Retrieves NextToken from Stream
         * Returns type of next token
         */
        public int NextToken()
        {
            //Reset all values 
            TokenType = 0;
            NumericValue = 0;
            StringValue = "";

            //Read next token
            char Peek = (char)InputStream.Peek();
            InputStream.Read();
            while (!InputStream.EndOfStream) //Read tokens until end of stream has reached
            {
                if (IsWhitespace(Peek)) //Ignore whitespace
                {
                    continue;
                }
                else if (Peek == '\n') //Newline hit
                {
                    LineNumber++;
                }
                else
                {
                    break;
                }
            }
            if (InputStream.EndOfStream)
            {
                return EOF; //End of stream reached
            }
            if (Char.IsDigit(Peek)) //We are dealing with a numeric value
            {
                double v = 0;
                do
                {
                    v = 10 * v + Char.GetNumericValue(Peek);
                    Peek = (char)InputStream.Peek();
                    InputStream.Read();
                } while (Char.IsDigit(Peek));
                if (Peek != '.') //end of number so return number
                {
                    NumericValue = v;
                    TokenType = NUMBER;
                    return TokenType;
                }
                // we have an floating point value
                double x = v; double d = 10;
                for (; ; )
                {
                    Peek = (char)InputStream.Peek();
                    InputStream.Read();
                    if (!Char.IsDigit(Peek))
                    {
                        break; //read complete number
                    }
                    x = x + Char.GetNumericValue(Peek) / d;
                    d = d * 10;                    
                }
                v = x;
                TokenType = NUMBER;
                return TokenType; 
            }

            if (Char.IsLetter(Peek)) //We are dealing with a letter
            {
                StringBuilder buffer = new StringBuilder();
                do
                { //Create string while we are dealing with letters or digits
                    buffer.Append(Peek);
                    Peek = (char)InputStream.Peek();
                    InputStream.Read();
                } while (Char.IsLetterOrDigit(Peek));
                StringValue = buffer.ToString();
                TokenType = WORD;
                return TokenType;
            }

            return 0;
        }

        /**
         * Returns type of last read token
         */
        public int GetTokenType()
        {
            return TokenType;
        }

        /**
         * Returns the numeric value of last read token if token is a number
         */
        public double GetNumericValue()
        {
            return NumericValue;
        }

        /**
         * Returns the string value of last read token if token is a word
         */
        public String GetStringValue()
        {
            return StringValue;
        }

        private bool IsWhitespace(char c)
        {
            for(int i = 0; i < WhitespaceCharacters.Length; i++)
            {
                if (c == (char)WhitespaceCharacters[i])
                {
                    return true;
                }
            }
            return false;
        }
    }
}
