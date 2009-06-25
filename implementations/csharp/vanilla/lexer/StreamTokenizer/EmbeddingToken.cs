using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Lexer.Tokenizer
{
    /// <summary>
    /// Token which contains an Embedding (sub tokens)
    /// </summary>
    public class EmbeddingToken : Token
    {
        #region Private Members

        private List<Token> TokenList;

        #endregion

        #region Public Methods
        
        public EmbeddingToken(List<Token> tokenList, TokenType type, int line) :  base(type, line)
        {
            TokenList = tokenList;
        }

        public EmbeddingToken()
        {
            TokenList = new List<Token>();
        }

        public void AddToken(Token token)
        {
            TokenList.Add(token);
        }

        public TokenIterator GetTokenIterator()
        {
            return new TokenIterator(TokenList);
        }

        #endregion
    }
}
