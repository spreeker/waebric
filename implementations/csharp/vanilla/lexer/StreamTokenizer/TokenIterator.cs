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
        private int Index = -1; //Index of iterator
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
            if (Index > TokenArray.Length)
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

        /// <summary>
        /// Poll if there is a next token in stream available
        /// </summary>
        /// <returns>HasNext Token in Stream</returns>
        public bool HasNext()
        {
            return HasNext(1);
        }

        /// <summary>
        /// Poll if there is a token on specified offset of current index
        /// </summary>
        /// <param name="index">Offset of current index</param>
        /// <returns>True if token on index+offset, false if not</returns>
        public bool HasNext(int offset)
        {
            return ((this.Index + offset) > TokenArray.Length);
        }

        /// <summary>
        /// Peek a token on specified offset of current index
        /// </summary>
        /// <param name="index">Offset of current index</param>
        /// <returns>Token if token on index.offset, null if no token exists</returns>
        /// <remarks>Check with HasNext(offset) before try'ing to retrieve token</remarks>
        public Token Peek(int offset)
        {
            //Check if there's something on the specified index
            if (HasNext(offset))
            {
                return TokenArray[(this.Index + offset)];
            }
            else
            {
                return null; //Index out of bound
            }
        }

        #endregion
    }
}
