using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Lexer.Tokenizer
{
    /// <summary>
    /// This class provides a mechanism to iterate in the TokenStream
    /// </summary>
    public class TokenIterator
    {
        #region Private Members
        private Token[] TokenArray; //Holds TokenStream as an array
        private int Index = 0; //Index of iterator
        #endregion

        #region Public Methods

        /// <summary>
        /// Creates an TokenIterator for the specified tokenStream
        /// </summary>
        /// <param name="tokenStream">TokenStream</param>
        public TokenIterator(List<Token> tokenStream)
        {
            TokenArray = tokenStream.ToArray();
        }

        /// <summary>
        /// Retrieve next available token
        /// </summary>
        /// <returns>Token if available, otherwise null value</returns>
        public Token NextToken()
        {
            if (Index > (TokenArray.Length - 1))
            {
                return null; //Index out of bound, end of array reached
            }
            else
            {
                int itemIndex = Index;
                Index++;
                return TokenArray[itemIndex];
            }
        }

        #endregion
    }
}
