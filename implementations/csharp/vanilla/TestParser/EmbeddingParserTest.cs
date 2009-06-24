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
            List<Exception> exceptionList = null; // TODO: Initialize to an appropriate value
            EmbeddingParser target = new EmbeddingParser(iterator, exceptionList); // TODO: Initialize to an appropriate value
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
            List<Exception> exceptions = new List<Exception>();
            EmbeddingParser embeddingParser = new EmbeddingParser(Init("\" sometext <"),exceptions);
            PreText parsedPreText = embeddingParser.ParsePreText();

            //Test output
            Assert.AreEqual(0, exceptions.Count);

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
            List<Exception> exceptions = new List<Exception>();
            EmbeddingParser embeddingParser = new EmbeddingParser(Init("> post \""), exceptions);
            PostText parsedPostText = embeddingParser.ParsePostText();

            //Test output
            Assert.AreEqual(0, exceptions.Count);

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
            List<Exception> exceptions = new List<Exception>();
            EmbeddingParser embeddingParser = new EmbeddingParser(Init("> midtext <"), exceptions);
            MidText parsedMidText = embeddingParser.ParseMidText();

            //Test output
            Assert.AreEqual(0, exceptions.Count);

            //Check midtext
            Assert.AreEqual("midtext", parsedMidText.GetText());
        }

        /// <summary>
        ///A test for ParseEmbedding
        ///</summary>
        [TestMethod()]
        public void ParseEmbeddingTest()
        {
            TokenIterator iterator = null; // TODO: Initialize to an appropriate value
            List<Exception> exceptionList = null; // TODO: Initialize to an appropriate value
            EmbeddingParser target = new EmbeddingParser(iterator, exceptionList); // TODO: Initialize to an appropriate value
            Embedding expected = null; // TODO: Initialize to an appropriate value
            Embedding actual;
            actual = target.ParseEmbedding();
            Assert.AreEqual(expected, actual);
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for ParseEmbed
        ///</summary>
        [TestMethod()]
        public void ParseEmbedTest()
        {
            TokenIterator iterator = null; // TODO: Initialize to an appropriate value
            List<Exception> exceptionList = null; // TODO: Initialize to an appropriate value
            EmbeddingParser target = new EmbeddingParser(iterator, exceptionList); // TODO: Initialize to an appropriate value
            Embed expected = null; // TODO: Initialize to an appropriate value
            Embed actual;
            actual = target.ParseEmbed();
            Assert.AreEqual(expected, actual);
            Assert.Inconclusive("Verify the correctness of this test method.");
        }
    }
}
