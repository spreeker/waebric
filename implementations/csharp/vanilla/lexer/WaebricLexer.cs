using System;
using System.Collections.Generic;
using System.IO;
using Lexer.Tokenizer;
using System.Text;
using Waebric;

namespace Lexer
{
    public class WaebricLexer
    {
        #region Private Members

        private TextReader Stream; // Stream to read from
        private List<Token> TokenStream = new List<Token>();
        private StreamTokenizer tokenizer;

        private int CurrentToken;
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
            tokenizer = new StreamTokenizer(Stream);

            TokenStream.Clear(); //Clean stream before inserting items
            
            CurrentToken = tokenizer.NextToken();
            while (CurrentToken != StreamTokenizer.EOF)
            {
                switch (CurrentToken)
                {
                    case StreamTokenizer.LAYOUT: // ignore layout
                        CurrentToken = tokenizer.NextToken();
                        break;
                    case StreamTokenizer.COMMENT: // ignore comments
                        CurrentToken = tokenizer.NextToken();
                        break;
                    case StreamTokenizer.WORD: // check word to determine type
                        LexicalizeWord();
                        break;
                    case StreamTokenizer.NUMBER: // numeric value
                        LexicalizeNumber();
                        break;
                    case StreamTokenizer.CHARACTER: // Character
                        if (tokenizer.GetCharacterValue() == '"') // Possible a quote
                        {
                            LexicalizeQuote();
                        }
                        else if (tokenizer.GetCharacterValue() == '\'') //Waebric Symbol ('symbol ) 
                        {
                            LexicalizeSymbol();
                        }
                        else
                        {   // Just an character
                            LexicalizeCharacter();
                        }
                        break;
                    default: //Other tokens are not correct
                        throw new StreamTokenizerException("Invalid token: " + CurrentToken, tokenizer.GetScannedLines());
                        break;
                }
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

        /// <summary>
        /// Retrieve list of tokens
        /// </summary>
        /// <returns>TokenList</returns>
        public List<Token> GetTokenList()
        {
            return TokenStream;
        }

        #endregion

        #region Private Methods

        /// <summary>
        /// Lexicalizes a word type
        /// </summary>
        private void LexicalizeWord()
        {
            if (IsKeyword(tokenizer.GetTextValue())) // Is probably keyword
            {
                //Check for symbols directly after keyword
                if (IsSymbol(tokenizer.PeekCharacter()))
                {   //It is not a keyword, followed directly by symbol, so maybe a path, etc.
                    LexicalizeIdentifier();
                }
                else
                {   // We are dealing with an keyword
                    TokenStream.Add(new Token(tokenizer.GetTextValue(), TokenType.KEYWORD, tokenizer.GetScannedLines()));
                    CurrentToken = tokenizer.NextToken();
                }
            }
            else if (IsIdentifier(tokenizer.GetTextValue()))
            {
                LexicalizeIdentifier();
                return;
            }
            else
            {
                throw new StreamTokenizerException("Invalid token: " + CurrentToken, tokenizer.GetScannedLines());
            }
        }

        /// <summary>
        /// Lexicalizes a numeric value
        /// </summary>
        private void LexicalizeNumber()
        {
            TokenStream.Add(new Token(tokenizer.GetNumericValue(), TokenType.NUMBER, tokenizer.GetScannedLines()));
            CurrentToken = tokenizer.NextToken();
        }

        /// <summary>
        /// Lexicalizes a character
        /// </summary>
        private void LexicalizeCharacter()
        {
            TokenStream.Add(new Token(tokenizer.GetCharacterValue(), TokenType.SYMBOL, tokenizer.GetScannedLines()));
            CurrentToken = tokenizer.NextToken();
        }

        /// <summary>
        /// Lexicalizes a quote
        /// </summary>
        private void LexicalizeQuote()
        {
            //Store current line number location for backtracking
            int tempLine = tokenizer.GetScannedLines();

            //Skip " token, only text is interesting
            CurrentToken = tokenizer.NextToken();

            //Retrieve possible quoted text
            StringBuilder stringBuilder = new StringBuilder();
            while (tokenizer.GetCharacterValue() != '\"') //Scan until " found
            {
                if(CurrentToken == StreamTokenizer.EOF)
                {   // End of file, so it wasn't a quoted part but just a single "
                    
                    //First add a single quote as token
                    TokenStream.Add(new Token("\"", TokenType.SYMBOL, tempLine));

                    //Second, scan remaining string
                    WaebricLexer tempLexer = new WaebricLexer(new StringReader(stringBuilder.ToString()));
                    List<Token> tempTokenList = tempLexer.GetTokenList();

                    //Add all tokens to stream
                    foreach(Token currentToken in tempTokenList)
                    {
                        TokenStream.Add(new Token(currentToken.GetValue(), currentToken.GetType(), (currentToken.GetLine()+tempLine)));
                    }
                }
                
                //Get next part and add it to stringBuilder
                stringBuilder.Append(tokenizer.ToString());
                CurrentToken = tokenizer.NextToken();
            }

            TokenStream.Add(new Token(stringBuilder.ToString(),TokenType.TEXT, tempLine));
            
            //Skip " token, only text is interesting
            CurrentToken = tokenizer.NextToken();
        }

        /// <summary>
        /// Lexicalizes a waebric symbol
        /// </summary>
        private void LexicalizeSymbol()
        {
            //Create a string with symbol
            StringBuilder stringBuilder = new StringBuilder();
            CurrentToken = tokenizer.NextToken(); //Skip ' token
 
            while(IsWaebricSymbol(tokenizer.ToString()) && CurrentToken != StreamTokenizer.EOF) 
            {
                stringBuilder.Append(tokenizer.ToString());
                CurrentToken = tokenizer.NextToken();
            }

            TokenStream.Add(new Token(stringBuilder.ToString(), TokenType.WAEBRICSYMBOL, tokenizer.GetScannedLines()));
        }

        /// <summary>
        /// Lexicalizes an identifier
        /// </summary>
        private void LexicalizeIdentifier()
        {
            TokenStream.Add(new Token(tokenizer.GetTextValue(), TokenType.IDENTIFIER, tokenizer.GetScannedLines()));
            CurrentToken = tokenizer.NextToken();
        }

        /// <summary>
        /// Checks if token is a keyword
        /// </summary>
        /// <param name="token">Token to check</param>
        /// <returns>True if token is keyword, otherwise false</returns>
        private bool IsKeyword(String token)
        {
            return Enum.IsDefined(typeof(WaebricKeyword), token.ToUpper());
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
        /// Check for a specified string if it contains only symbols
        /// </summary>
        /// <param name="possibleSymbol">String to check</param>
        /// <returns>True if string contains only symbols, otherwise false</returns>
        private bool IsWaebricSymbol(String possibleSymbol)
        {
            if (possibleSymbol == null)
            {
                return false;
            }
            
            //Check out all characters
            char[] possibleSymbolCharacters = possibleSymbol.ToCharArray();
            for(int i = 0; i <= (possibleSymbol.Length - 1); i++)
            {
                if (!IsWaebricSymbol(possibleSymbol[i]))
                {
                    return false;
                }
            }

            return true;
        }

        private bool IsWaebricSymbol(int c)
        {
            //~[0-31] \t\n\r;> [127-255]
            return c > 31 && c < 127 && c != ' ' && c != ';' && c != ',' && c != '>' && c != '}';
        }

        /// <summary>
        /// Check if a specified character is a symbol
        /// </summary>
        /// <param name="c">Character to check</param>
        /// <returns>True if symbol, otherwise false</returns>
        private bool IsSymbol(char c)
        {
            return Char.IsSymbol(c) || Char.IsPunctuation(c);
        }

        #endregion
    }
}
