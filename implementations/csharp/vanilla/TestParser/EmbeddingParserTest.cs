using Parser;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Lexer.Tokenizer;
using System;
using System.Collections.Generic;
using Parser.Ast.Embedding;
using Lexer;
using System.IO;

namespace TestParser
{
    
    
    /// <summary>
    ///This is a test class for EmbeddingParserTest and is intended
    ///to contain all EmbeddingParserTest Unit Tests
    ///</summary>
    [TestClass()]
    public class EmbeddingParserTest
    {

        private WaebricLexer lexer;
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

        /// <summary>
        /// Initialize test
        /// </summary>
        /// <param name="stream">Stream to lexicalize</param>
        /// <returns>TokenIterator</returns>
        private TokenIterator Init(String stream)
        {
            lexer = new WaebricLexer(new StringReader(stream));
            lexer.LexicalizeStream();

            return lexer.GetTokenIterator();
        }

        [TestCleanup]
        public void CleanUp()
        {
            lexer = null;
        }

        #endregion


        /// <summary>
        ///A test for ParseTextTail
        ///</summary>
        [TestMethod()]
        public void ParseTextTailTest()
        {
            TokenIterator iterator = null; // TODO: Initialize to an appropriate value
            EmbeddingParser target = new EmbeddingParser(iterator); // TODO: Initialize to an appropriate value
            TextTail expected = null; // TODO: Initialize to an appropriate value
            TextTail actual;
            actual = target.ParseTextTail();
            Assert.AreEqual(expected, actual);
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for ParsePreText
        ///</summary>
        [TestMethod()]
        public void ParsePreTextTest()
        {
            //Create parser
            EmbeddingParser embeddingParser = new EmbeddingParser(Init("\" sometext <"));
            PreText parsedPreText = embeddingParser.ParsePreText();

            //Check pretext
            Assert.AreEqual("sometext", parsedPreText.GetText());
        }

        /// <summary>
        ///A test for ParsePostText
        ///</summary>
        [TestMethod()]
        public void ParsePostTextTest()
        {
            //Create parser

            EmbeddingParser embeddingParser = new EmbeddingParser(Init("> post \""));
            PostText parsedPostText = embeddingParser.ParsePostText();

            //Check posttext
            Assert.AreEqual("post", parsedPostText.GetText());
        }

        /// <summary>
        ///A test for ParseMidText
        ///</summary>
        [TestMethod()]
        public void ParseMidTextTest()
        {
            //Create parser
            List<Token> tokenList = new List<Token>();
            tokenList.Add(new Token('>', TokenType.SYMBOL, 0));
            tokenList.Add(new Token("midtext", TokenType.TEXT, 0));
            tokenList.Add(new Token('<', TokenType.TEXT, 0));

            TokenIterator tokens = new TokenIterator(tokenList);

            EmbeddingParser embeddingParser = new EmbeddingParser(tokens);
            MidText parsedMidText = embeddingParser.ParseMidText();

            //Check midtext
            Assert.AreEqual("midtext", parsedMidText.GetText());
        }

        /// <summary>
        ///A test for ParseEmbedding
        ///</summary>
        [TestMethod()]
        public void ParseEmbeddingTest()
        {
            //Create parser
            EmbeddingParser embeddingParser = new EmbeddingParser(Init("\"left<func1() \"text\">right\""));
            Embedding parsedEmbedding = embeddingParser.ParseEmbedding();
        }

        /// <summary>
        ///A test for ParseEmbed
        ///</summary>
        [TestMethod()]
        public void ParseEmbedTest()
        {
            TokenIterator iterator = null; // TODO: Initialize to an appropriate value
            EmbeddingParser target = new EmbeddingParser(iterator); // TODO: Initialize to an appropriate value
            Embed expected = null; // TODO: Initialize to an appropriate value
            Embed actual;
            actual = target.ParseEmbed();
            Assert.AreEqual(expected, actual);
            Assert.Inconclusive("Verify the correctness of this test method.");
        }
    }
}
