using Parser;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Lexer.Tokenizer;
using System;
using System.Collections.Generic;
using Parser.Ast.Embedding;
using Lexer;
using System.IO;
using Parser.Ast.Markup;

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
        ///A test for ParseEmbedding
        ///</summary>
        [TestMethod()]
        public void ParseEmbeddingTest()
        {
            //Create parser
            EmbeddingParser embeddingParser = new EmbeddingParser(Init("\"left<func1() \"text\">right\""));
            Embedding parsedEmbedding = embeddingParser.ParseEmbedding();

            //Test PreText
            Assert.AreEqual("left", parsedEmbedding.GetPreText().GetText());
           
            //Test Embed
            Assert.AreEqual(typeof(ExpressionEmbed), parsedEmbedding.GetEmbed().GetType());
            ExpressionEmbed expressionEmbed = (ExpressionEmbed) parsedEmbedding.GetEmbed();
            Assert.AreEqual("text", expressionEmbed.GetExpression().ToString());

            //Test Markup
            Markup[] markupArray = expressionEmbed.GetMarkups().ToArray();
            Markup markup = markupArray[0];
            Assert.AreEqual("func1", markup.GetDesignator().ToString());
            Assert.AreEqual(0, markup.GetArguments().Count);

            //Test TextTail
            Assert.AreEqual(typeof(PostTextTail), parsedEmbedding.GetTextTail().GetType());
            PostTextTail postTextTail = (PostTextTail) parsedEmbedding.GetTextTail();
            Assert.AreEqual("right", postTextTail.GetPostText().GetText());
        }
    }
}
