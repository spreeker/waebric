using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;

namespace Lexer.Tokenizer
{
    /// <summary>
    /// Test class for TokenIterator
    /// </summary>
    [TestFixture]
    public class TestTokenIterator
    {
        private List<Token> TokenList;

        /// <summary>
        /// Method to set up class members for test
        /// </summary>
        [SetUp]
        public void Init()
        {
            TokenList = new List<Token>();
        }

        [Test]
        public void TestNextToken()
        {
            //Create test tokens and add them to list
            Token token1 = new Token("testToken", TokenType.IDENTIFIER, 0);
            Token token2 = new Token(1, TokenType.NUMBER, 1);
            TokenList.Add(token1);
            TokenList.Add(token2);

            TokenIterator iterator = new TokenIterator(TokenList);

            //Check if same items come out and in right order (FIFO)
            Assert.IsTrue(token1.Equals(iterator.NextToken()));
            Assert.IsTrue(token2.Equals(iterator.NextToken()));
            Assert.IsNull(iterator.NextToken());
        }

        /// <summary>
        /// Method to cleanup class members after test
        /// </summary>
        [TearDown]
        public void Cleanup()
        {
            TokenList = null;
        }
    }
}
