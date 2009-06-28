using Parser;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Lexer.Tokenizer;
using System;
using System.Collections.Generic;
using Parser.Ast.Markup;
using Attribute = Parser.Ast.Markup.Attribute;
using System.IO;
using Lexer;
using Parser.Ast.Expressions;

namespace TestParser
{
    
    
    /// <summary>
    ///This is a test class for MarkupParserTest and is intended
    ///to contain all MarkupParserTest Unit Tests
    ///</summary>
    [TestClass()]
    public class MarkupParserTest
    {

        private TestContext testContextInstance;
        private WaebricLexer lexer;

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
        ///Testing Markup Parser with default markup
        ///</summary>
        [TestMethod()]
        public void ParseDefaultMarkupTest()
        {
            //Parse tokens
            MarkupParser markupParser = new MarkupParser(Init("(\"test\")"));
            Markup parsedMarkup = markupParser.ParseMarkup();

            //Check attributes
            Assert.AreEqual(0, parsedMarkup.GetArguments().Count);
        }

        /// <summary>
        /// Test ParseAttributeTest
        /// </summary>
        [TestMethod]
        public void ParseAttributeTest()
        {
            //Parse tokens
            MarkupParser markupParser = new MarkupParser(Init("#id1"));
            Attribute parsedAttribute = markupParser.ParseAttribute();

            //Check attribute
            Assert.AreEqual(typeof(IdAttribute), parsedAttribute.GetType());

            //Check id attribute
            Assert.AreEqual("id1", parsedAttribute.ToString());
        }

        /// <summary>
        /// Test ParseHeightAttributeTest
        /// </summary>
        [TestMethod]
        public void ParseHeightAttributeTest()
        {
            //Parse tokens
            MarkupParser markupParser = new MarkupParser(Init("120"));
            HeightAttribute parsedHeightAttribute = markupParser.ParseHeightAttribute();

            //Check Id Attribute
            Assert.AreEqual(120, parsedHeightAttribute.GetHeight());
        }

        /// <summary>
        /// Test ParseWidth_HeightAttributeTest
        /// </summary>
        [TestMethod]
        public void ParseWidth_HeightAttributeTest()
        {
            //Parse tokens
            MarkupParser markupParser = new MarkupParser(Init("80%20"));
            Width_HeightAttribute parsedWidth_HeightAttribute = markupParser.ParseWidth_HeightAttribute();

            //Check Id Attribute
            Assert.AreEqual(80, parsedWidth_HeightAttribute.GetWidth());
            Assert.AreEqual(20, parsedWidth_HeightAttribute.GetHeight());
        }

        /// <summary>
        /// Test ParseTypeAttribute
        /// </summary>
        [TestMethod]
        public void ParseTypeAttributeTest()
        {
            //Parse tokens
            MarkupParser markupParser = new MarkupParser(Init("type1"));
            TypeAttribute parsedTypeAttribute = markupParser.ParseTypeAttribute();

            //Check Id Attribute
            Assert.AreEqual("type1", parsedTypeAttribute.GetType());
        }

        /// <summary>
        /// Test ParseNameAttribute
        /// </summary>
        [TestMethod]
        public void ParseNameAttributeTest()
        {
            //Parse tokens
            MarkupParser markupParser = new MarkupParser(Init("nametest"));
            NameAttribute parsedNameAttribute = markupParser.ParseNameAttribute();

            //Check Id Attribute
            Assert.AreEqual("nametest", parsedNameAttribute.GetName());
        }

        /// <summary>
        /// Test ParseClassAttributeTest
        /// </summary>
        [TestMethod]
        public void ParseClassAttributeTest()
        {
            //Parse tokens
            MarkupParser markupParser = new MarkupParser(Init("classname"));
            ClassAttribute parsedClassAttribute = markupParser.ParseClassAttribute();

            //Check Id Attribute
            Assert.AreEqual("classname", parsedClassAttribute.GetClass());
        }

        /// <summary>
        /// Test ParseIdAttribute
        /// </summary>
        [TestMethod]
        public void ParseIdAttributeTest()
        {
            //Parse tokens
            MarkupParser markupParser = new MarkupParser(Init("testid"));
            IdAttribute parsedIdAttribute = markupParser.ParseIdAttribute();

            //Check Id Attribute
            Assert.AreEqual("testid",parsedIdAttribute.GetId());
        }

        /// <summary>
        /// Test Designator Parser with attribute
        /// </summary>
        [TestMethod]
        public void ParseDesignatorWithAttributeTest()
        {
            //Parse tokens
            MarkupParser markupParser = new MarkupParser(Init("img@100%50"));
            Designator parsedDesignator = markupParser.ParseDesignator();

            //Check designator
            Assert.AreEqual("img", parsedDesignator.GetIdentifier());
            Assert.AreEqual(1, parsedDesignator.GetAttributes().Count);

            //Check attribute
            Attribute[] parsedAttributes = parsedDesignator.GetAttributes().ToArray();
            Assert.AreEqual(typeof(Width_HeightAttribute),parsedAttributes[0].GetType());

            //Check Width_HeightAttribute contents
            Width_HeightAttribute attribute = (Width_HeightAttribute) parsedAttributes[0];
            Assert.AreEqual(100, attribute.GetWidth());
            Assert.AreEqual(50, attribute.GetHeight());
        }

        /// <summary>
        ///A test for Call Markup without arguments
        ///</summary>
        [TestMethod()]
        public void ParseCallNoArgsMarkupTest()
        {
            //Parse tokens
            MarkupParser markupParser = new MarkupParser(Init("home()"));
            Markup parsedMarkup = markupParser.ParseMarkup();

            //Check markup
            Assert.AreEqual("home", parsedMarkup.GetDesignator().GetIdentifier()); //identifier check
            Assert.AreEqual(0, parsedMarkup.GetDesignator().GetAttributes().Count); //no attributes
            Assert.AreEqual(0, parsedMarkup.GetArguments().Count); //no arguments
        }

        /// <summary>
        ///Test ArgumentsParser without arguments to parse
        ///</summary>
        [TestMethod()]
        public void ParseNoArgumentsTest()
        {
            //Parse tokens
            MarkupParser markupParser = new MarkupParser(Init("()"));
            Markup markup = new Markup();
            markupParser.ParseArguments(markup);

            //Test arguments
            Assert.AreEqual(0, markup.GetArguments().Count);
        }

        /// <summary>
        ///A test for ParseExpressionArgument
        ///</summary>
        [TestMethod()]
        public void ParseExpressionArgumentTest()
        {
            //Parse tokens
            MarkupParser markupParser = new MarkupParser(Init("([1234,2345,3556,646])"));
            Markup markup = new Markup();
            markupParser.ParseArguments(markup);

            //Test argument
            Assert.AreEqual(1, markup.GetArguments().Count);
            Argument[] arguments = markup.GetArguments().ToArray();
            Assert.AreEqual(typeof(ExpressionArgument), arguments[0].GetType());

            //Test expression argument
            ExpressionArgument exprArgument = (ExpressionArgument)arguments[0];
            Assert.AreEqual(typeof(ListExpression), exprArgument.GetExpression().GetType());

            //Test list expression
            ListExpression listExpression = (ListExpression) exprArgument.GetExpression();
            Assert.AreEqual(4, listExpression.GetExpressions().Count);
        }

        /// <summary>
        /// Test for ParseAttrArgument
        /// </summary>
        [TestMethod]
        public void ParseAttrArgumentTest()
        {
            //Parse tokens
            MarkupParser markupParser = new MarkupParser(Init("(i = 1)"));
            Markup markup = new Markup();
            markupParser.ParseArguments(markup);

            //Test arguments
            Assert.AreEqual(1, markup.GetArguments().Count);
            Argument[] arguments = markup.GetArguments().ToArray();
            Assert.AreEqual(typeof(AttrArgument), arguments[0].GetType());
            
            //Test specific argument
            AttrArgument attrArgument = (AttrArgument)arguments[0];
            Assert.AreEqual("i", attrArgument.GetIdentifier());
            Assert.AreEqual(typeof(NumExpression), attrArgument.GetExpression().GetType());
        }
    }
}
