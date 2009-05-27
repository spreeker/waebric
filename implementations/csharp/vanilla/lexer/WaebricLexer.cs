using System;
using System.Collections.Generic;
using System.IO;
using Lexer.Tokenizer;

namespace Lexer
{
    class WaebricLexer
    {
        #region Private Members

        private TextReader Stream; // Stream to read from
        private List<Token> TokenStream = new List<Token>();

        #endregion

        #region Public Methods

        /// <summary>
        /// Creates a new WaebricLexer which tokenizes a given stream
        /// </summary>
        /// <param name="inputStream">StreamReader to read from</param>
        public WaebricLexer(TextReader inputStream)
        {
            this.Stream = inputStream;
        }

        /// <summary>
        /// Lexicalizes the stream to tokens
        /// </summary>
        public void LexicalizeStream()
        {
            StreamTokenizer tokenizer = new StreamTokenizer(Stream);

            TokenStream.Clear(); //Clean stream before inserting items
            
            // TODO: Fix whitespace hard coding on this way
            int[] whitespaces = { '\t', ' ' };
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
                        else if (IsIdentifier(tokenizer.GetTextValue()))
                        {
                            TokenStream.Add(new Token(tokenizer.GetTextValue(), TokenType.IDENTIFIER, tokenizer.GetScannedLines()));
                        }
                        else
                        {
                            throw new StreamTokenizerException("Invalid token: " + token, tokenizer.GetScannedLines());
                        }
                        break;
                    case StreamTokenizer.ST_NUMBER: // numeric value
                        TokenStream.Add(new Token(tokenizer.GetNumericValue(), TokenType.NUMBER, tokenizer.GetScannedLines()));
                        break;
                    default: //other token types need to be handled
                        if (token == '\"')
                        {   //quote, so text
                            TokenStream.Add(new Token(tokenizer.GetTextValue(), TokenType.TEXT, tokenizer.GetScannedLines()));
                        }
                        else if (IsSymbol((char)token))
                        {
                            TokenStream.Add(new Token(tokenizer.GetTextValue(), TokenType.SYMBOL, tokenizer.GetScannedLines()));
                        }
                        else
                        {
                            throw new StreamTokenizerException("Invalid token: " + token, tokenizer.GetScannedLines());
                        }
                        break;
                }
                token = tokenizer.NextToken();
            }
        }

        /// <summary>
        /// Returns an TokenIterator to provide a mechanism to handle the stream
        /// </summary>
        /// <returns>TokenIterator if stream is filled, otherwise null</returns>
        public TokenIterator GetTokenIterator()
        {
            if (TokenStream.Count == 0)
            {
                return null;
            }
            else
            {
                return new TokenIterator(TokenStream);
            }
        }

        #endregion

        #region Private Methods

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

        /// <summary>
        /// Checks if character is a symbol
        /// </summary>
        /// <param name="c">Character to check</param>
        /// <returns>True if character is symbol, otherwise false</returns>
        private bool IsSymbol(char c)
        {
            return c > (int)32 && c < (int)126;
        }

        #endregion
    }
}
