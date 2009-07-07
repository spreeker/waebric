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
            Assert.AreEqual("\"text\"", expressionEmbed.GetExpression().ToString());

            //Test Markup
            Markup markup = (Markup) expressionEmbed.GetMarkups().Get(0);
            Assert.AreEqual("func1", markup.GetDesignator().ToString());
            Assert.AreEqual(0, markup.GetArguments().Count);

            //Test TextTail
            Assert.AreEqual(typeof(PostTextTail), parsedEmbedding.GetTextTail().GetType());
            PostTextTail postTextTail = (PostTextTail) parsedEmbedding.GetTextTail();
            Assert.AreEqual("right", postTextTail.GetPostText().GetText());
        }

        [TestMethod()]
        public void ParseNestedEmbeddingTest()
        {
            //Create parser
            EmbeddingParser embeddingParser = new EmbeddingParser(Init("\"pretext<em \"eerste\">midtext <em \"tweede\">posttexttail\""));
            Embedding parsedEmbedding = embeddingParser.ParseEmbedding();

            //Test pretext
            Assert.AreEqual("\"pretext<", parsedEmbedding.GetPreText().ToString());

            //Test 1st embed
            Assert.AreEqual(typeof(ExpressionEmbed), parsedEmbedding.GetEmbed().GetType());
            ExpressionEmbed expressionEmbed = (ExpressionEmbed)parsedEmbedding.GetEmbed();
            Assert.AreEqual("em", expressionEmbed.GetMarkups().Get(0).ToString());
            Assert.AreEqual("\"eerste\"", expressionEmbed.GetExpression().ToString());

            //Test TextTail
            Assert.AreEqual(typeof(MidTextTail), parsedEmbedding.GetTextTail().GetType());
            MidTextTail midTextTail = (MidTextTail) parsedEmbedding.GetTextTail();
            Assert.AreEqual(">midtext <", midTextTail.GetMidText().ToString());

            //Test 2th embed
            Assert.AreEqual(typeof(ExpressionEmbed), midTextTail.GetEmbed().GetType());
            ExpressionEmbed expressionEmbed2 = (ExpressionEmbed)midTextTail.GetEmbed();
            Assert.AreEqual("em", expressionEmbed2.GetMarkups().Get(0).ToString());
            Assert.AreEqual("\"tweede\"", expressionEmbed2.GetExpression().ToString());
            
            //Test PostTextTail
            Assert.AreEqual(typeof(PostTextTail), midTextTail.GetTextTail().GetType());
            Assert.AreEqual(">posttexttail\"", midTextTail.GetTextTail().ToString());
        }
    }
}
