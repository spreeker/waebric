using Parser;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Lexer.Tokenizer;
using System;
using System.Collections.Generic;
using Parser.Ast.Markup;
using Attribute = Parser.Ast.Markup.Attribute;
using System.IO;
using Lexer;

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


        }

        /// <summary>
        /// Test ParseAttributeTest
        /// </summary>
        [TestMethod]
        public void ParseAttributeTest()
        {
            //Parse tokens
            List<Exception> exceptions = new List<Exception>();
            MarkupParser markupParser = new MarkupParser(Init("#id1"), exceptions);
            Attribute parsedAttribute = markupParser.ParseAttribute();

            //Check output
            Assert.AreEqual(0, exceptions.Count);

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
            List<Exception> exceptions = new List<Exception>();
            MarkupParser markupParser = new MarkupParser(Init("120"), exceptions);
            HeightAttribute parsedHeightAttribute = markupParser.ParseHeightAttribute();

            //Check output
            Assert.AreEqual(0, exceptions.Count);

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
            List<Exception> exceptions = new List<Exception>();
            MarkupParser markupParser = new MarkupParser(Init("80%20"), exceptions);
            Width_HeightAttribute parsedWidth_HeightAttribute = markupParser.ParseWidth_HeightAttribute();

            //Check output
            Assert.AreEqual(0, exceptions.Count);

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
            List<Exception> exceptions = new List<Exception>();
            MarkupParser markupParser = new MarkupParser(Init("type1"), exceptions);
            TypeAttribute parsedTypeAttribute = markupParser.ParseTypeAttribute();

            //Check output
            Assert.AreEqual(0, exceptions.Count);

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
            List<Exception> exceptions = new List<Exception>();
            MarkupParser markupParser = new MarkupParser(Init("nametest"), exceptions);
            NameAttribute parsedNameAttribute = markupParser.ParseNameAttribute();

            //Check output
            Assert.AreEqual(0, exceptions.Count);

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
            List<Exception> exceptions = new List<Exception>();
            MarkupParser markupParser = new MarkupParser(Init("classname"), exceptions);
            ClassAttribute parsedClassAttribute = markupParser.ParseClassAttribute();

            //Check output
            Assert.AreEqual(0, exceptions.Count);

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
            List<Exception> exceptions = new List<Exception>();
            MarkupParser markupParser = new MarkupParser(Init("testid"), exceptions);
            IdAttribute parsedIdAttribute = markupParser.ParseIdAttribute();

            //Check output
            Assert.AreEqual(0, exceptions.Count);

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
            List<Exception> exceptions = new List<Exception>();
            MarkupParser markupParser = new MarkupParser(Init("img@100%50"), exceptions);
            Designator parsedDesignator = markupParser.ParseDesignator();

            //Check output
            Assert.AreEqual(0, exceptions.Count);

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
            List<Exception> exceptions = new List<Exception>();
            MarkupParser markupParser = new MarkupParser(Init("home()"), exceptions);
            Markup parsedMarkup = markupParser.ParseMarkup();

            //Check output
            Assert.AreEqual(0, exceptions.Count);

            //Check markup
            Assert.AreEqual("home", parsedMarkup.GetDesignator().GetIdentifier()); //identifier check
            Assert.AreEqual(0, parsedMarkup.GetDesignator().GetAttributes().Count); //no attributes
            Assert.AreEqual(0, parsedMarkup.GetArguments().GetArguments().Count); //no arguments
        }

        /// <summary>
        ///Test ArgumentsParser without arguments to parse
        ///</summary>
        [TestMethod()]
        public void ParseNoArgumentsTest()
        {
            //Parse tokens
            List<Exception> exceptions = new List<Exception>();
            MarkupParser markupParser = new MarkupParser(Init("()"), exceptions);
            Arguments args = markupParser.ParseArguments();

            //Test no exceptions
            Assert.AreEqual(0, exceptions.Count);

            //Test arguments
            Assert.AreEqual(2, args.GetArguments().Count);
        }

        /// <summary>
        ///A test for ParseArgument
        ///</summary>
        [TestMethod()]
        public void ParseArgumentTest()
        {

        }

        /// <summary>
        ///A test for MarkupParser Constructor
        ///</summary>
        [TestMethod()]
        public void MarkupParserConstructorTest()
        {

        }
    }
}
