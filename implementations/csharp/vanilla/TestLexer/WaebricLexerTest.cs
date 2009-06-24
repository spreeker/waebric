using Lexer;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.IO;
using Lexer.Tokenizer;

namespace TestLexer
{
    
    
    /// <summary>
    ///This is a test class for WaebricLexerTest and is intended
    ///to contain all WaebricLexerTest Unit Tests
    ///</summary>
    [TestClass()]
    public class WaebricLexerTest
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
        /// Test for WaebricSymbol
        /// </summary>
        [TestMethod]
        public void WaebricSymbolTokenTest()
        {
            //Set up tokenizer
            WaebricLexer lexer = new WaebricLexer(new StringReader("'test"));
            lexer.LexicalizeStream();

            TokenIterator tokens = lexer.GetTokenIterator();

            //Test token
            Assert.AreEqual(1, tokens.GetSize());
            Token token = tokens.NextToken();
            Assert.AreEqual(TokenType.WAEBRICSYMBOL, token.GetType());
            Assert.AreEqual("test", token.GetValue().ToString());
        }

        /// <summary>
        /// Test for Quotes
        /// </summary>
        [TestMethod]
        public void QuoteTokenTest()
        {
            //Set up tokenizer
            WaebricLexer lexer = new WaebricLexer(new StringReader("\"test\""));
            lexer.LexicalizeStream();

            TokenIterator tokens = lexer.GetTokenIterator();

            //Test token
            Assert.AreEqual(1, tokens.GetSize());
            Assert.AreEqual(TokenType.TEXT, tokens.Peek(1).GetType());
            Assert.AreEqual("test", tokens.Peek(1).GetValue().ToString());
        }

        /// <summary>
        /// Test input with single quote
        /// </summary>
        [TestMethod]
        public void SingleQuoteTest()
        {
            //Set up tokenizer
            WaebricLexer lexer = new WaebricLexer(new StringReader("\" test <"));
            lexer.LexicalizeStream();

            TokenIterator tokens = lexer.GetTokenIterator();

            Assert.AreEqual(3, tokens.GetSize());
        }

        /// <summary>
        /// Test an more complex form of stream
        /// </summary>
        [TestMethod]
        public void TestComplexStream()
        {
            WaebricLexer lexer = new WaebricLexer(new StringReader("module test\n\nsite site/index.html : home()\nend"));
            lexer.LexicalizeStream();

            Assert.IsTrue(lexer.GetTokenList().Count == 13);
        }
    }
}
