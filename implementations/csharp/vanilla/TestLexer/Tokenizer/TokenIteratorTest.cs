using Lexer.Tokenizer;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Collections.Generic;

namespace TestLexer
{
    
    
    /// <summary>
    ///This is a test class for TokenIteratorTest and is intended
    ///to contain all TokenIteratorTest Unit Tests
    ///</summary>
    [TestClass()]
    public class TokenIteratorTest
    {


        private TestContext testContextInstance;

        /// <summary>
        ///Gets or sets the test context which provides
        ///information about and functionality for the current test run.
        ///</summary>
        public TestContext TestContext
        {
            get
            {
                return testContextInstance;
            }
            set
            {
                testContextInstance = value;
            }
        }

        #region Additional test attributes
        // 
        //You can use the following additional attributes as you write your tests:
        //
        //Use ClassInitialize to run code before running the first test in the class
        //[ClassInitialize()]
        //public static void MyClassInitialize(TestContext testContext)
        //{
        //}
        //
        //Use ClassCleanup to run code after all tests in a class have run
        //[ClassCleanup()]
        //public static void MyClassCleanup()
        //{
        //}
        //
        //Use TestInitialize to run code before running each test
        //[TestInitialize()]
        //public void MyTestInitialize()
        //{
        //}
        //
        //Use TestCleanup to run code after each test has run
        //[TestCleanup()]
        //public void MyTestCleanup()
        //{
        //}
        //
        #endregion


        /// <summary>
        ///A test for Peek
        ///</summary>
        [TestMethod()]
        public void PeekTest()
        {
            List<Token> tokenStream = null; // TODO: Initialize to an appropriate value
            TokenIterator target = new TokenIterator(tokenStream); // TODO: Initialize to an appropriate value
            int offset = 0; // TODO: Initialize to an appropriate value
            Token expected = null; // TODO: Initialize to an appropriate value
            Token actual;
            actual = target.Peek(offset);
            Assert.AreEqual(expected, actual);
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for NextToken
        ///</summary>
        [TestMethod()]
        public void NextTokenTest()
        {
            List<Token> tokenStream = null; // TODO: Initialize to an appropriate value
            TokenIterator target = new TokenIterator(tokenStream); // TODO: Initialize to an appropriate value
            Token expected = null; // TODO: Initialize to an appropriate value
            Token actual;
            actual = target.NextToken();
            Assert.AreEqual(expected, actual);
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for HasNext
        ///</summary>
        [TestMethod()]
        public void HasNextTest()
        {
            //Create list and add dummy tokens to it
            List<Token> tokenStream = new List<Token>();
            Token token1 = new Token();
            Token token2 = new Token();
            tokenStream.Add(token1);
            tokenStream.Add(token2);
            
            //Create iterator with tokenstream and then test some things out
            TokenIterator iterator = new TokenIterator(tokenStream);
            
            //Check size
            Assert.IsTrue(iterator.GetSize() == tokenStream.Count);

            //Check if tokens are in list
            Assert.IsTrue(iterator.HasNext());
            
            //Check also if there are 2 items in list
            Assert.IsTrue(iterator.HasNext(2));
            
            //Get first token and check
            
            Token CurrentToken = iterator.NextToken();
            Assert.AreEqual(token1, CurrentToken);

            //Get second token and check
            Assert.IsTrue(iterator.HasNext());
            CurrentToken = iterator.NextToken();
            Assert.AreEqual(token2, CurrentToken);

            //No items in list left
            Assert.IsFalse(iterator.HasNext());
        }

        /// <summary>
        ///A test for TokenIterator Constructor
        ///</summary>
        [TestMethod()]
        public void TokenIteratorConstructorTest()
        {
            List<Token> tokenStream = null; // TODO: Initialize to an appropriate value
            TokenIterator target = new TokenIterator(tokenStream);
            Assert.Inconclusive("TODO: Implement code to verify target");
        }
    }
}
