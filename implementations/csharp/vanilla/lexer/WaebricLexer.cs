using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using Lexer.Tokenizer;
using System.Collections;

namespace Lexer
{
    class WaebricLexer
    {
        #region Private Members

        private StreamReader Stream; // Stream to read from
        private int Line = 1; // Holds current line number
        private List<Token> TokenStream = new List<Token>();

        #endregion

        #region Public Methods

        /// <summary>
        /// Creates a new WaebricLexer which tokenizes a given stream
        /// </summary>
        /// <param name="inputStream">StreamReader to read from</param>
        public WaebricLexer(StreamReader inputStream)
        {
            this.Stream = inputStream;
        }

        /// <summary>
        /// Lexicalizes the stream to tokens
        /// </summary>
        public void LexicalizeStream()
        {
            StreamTokenizer tokenizer = new StreamTokenizer(Stream);
            
            //start with building tokenstream
            int[] whitespaces = {'\t', ' '};
            tokenizer.WhitespaceChars(whitespaces);
            int token;
            token = tokenizer.NextToken();
            while (token != StreamTokenizer.ST_EOF)
            {
                switch (token)
                {
                    case StreamTokenizer.ST_EOL: // ignore EOL
                        break; 
                    case StreamTokenizer.ST_WORD: // check word to determine type
                        if (IsKeyword(tokenizer.GetTextValue())) // Is keyword
                        {
                            TokenStream.Add(new Token(tokenizer.GetTextValue(), TokenType.KEYWORD, tokenizer.GetScannedLines()));
                        }
                        else if(IsIdentifier(tokenizer.GetTextValue()))
                        {
                            TokenStream.Add(new Token(tokenizer.GetTextValue(), TokenType.IDENTIFIER, tokenizer.GetScannedLines()));
                        }
                        else
                        {
                            //exception handling
                        }
                        break;
                    case StreamTokenizer.ST_NUMBER: // numeric value
                        TokenStream.Add(new Token(tokenizer.GetNumericValue(), TokenType.NUMBER, tokenizer.GetScannedLines()));
                        break;
                    default:
                        //System.Console.WriteLine(tokenizer.GetTextValue()); //DEBUG only

                        break;
                }
                token = tokenizer.NextToken();
            }
            System.Console.WriteLine("Lines readed: " + tokenizer.GetScannedLines());
            //return "";
        }

        public bool IsDigit(char c)
        {
            return c >= '0' && c <= '9';
        }

        public bool IsLetter(char c)
        {
            return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
        }

        #endregion

        #region Private Methods

        private bool IsLetterOrDigit(char c)
        {
            return IsDigit(c) || IsLetter(c);
        }

        /// <summary>
        /// Checks if token is a keyword
        /// </summary>
        /// <param name="token">Token to check</param>
        /// <returns>True if token is keyword, otherwise false</returns>
        private bool IsKeyword(String token)
        {
            return Enum.IsDefined(typeof(Waebric.WaebricKeyword), token.ToUpper());
        }

        /// <summary>
        /// Checks if token is an identifier
        /// </summary>
        /// <param name="token">Token to check</param>
        /// <returns>True if token is identifier, otherwise false</returns>
        private bool IsIdentifier(String token)
        {
            if (token == null || token == "")
            {
                return false;

            }
            char[] stringArray = token.ToCharArray();
            foreach (char c in stringArray)
            {
                if (!(Char.IsLetterOrDigit(c) || c == '.'))
                {
                    return false;
                }
            }
            return true;
        }

        #endregion
    }
}
