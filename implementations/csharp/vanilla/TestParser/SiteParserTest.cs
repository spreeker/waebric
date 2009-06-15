using Parser;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Lexer.Tokenizer;
using System;
using System.Collections.Generic;
using Parser.Ast.Site;
using Dir = Parser.Ast.Site.Directory;
using Pth = Parser.Ast.Site.Path;
using Lexer;
using System.IO;

namespace TestParser
{
    
    
    /// <summary>
    ///This is a test class for SiteParserTest and is intended
    ///to contain all SiteParserTest Unit Tests
    ///</summary>
    [TestClass()]
    public class SiteParserTest
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
        ///A test for ParseSite
        ///</summary>
        [TestMethod()]
        public void ParseSiteTest()
        {
 
        }

        /// <summary>
        ///A test for ParsePath
        ///</summary>
        [TestMethod()]
        public void ParsePathTest()
        {
            //Create lexer and lexicalizeStream
            WaebricLexer lexer = new WaebricLexer(new StringReader("site/home.html"));
            lexer.LexicalizeStream();

            //Get tokens and parse it
            TokenIterator tokens = lexer.GetTokenIterator();
            List<Exception> exceptions = new List<Exception>();
            SiteParser siteParser = new SiteParser(tokens, exceptions);
            Pth path = siteParser.ParsePath();

            //Get structures of path
            Dir directory = path.GetDirectoryName().GetDirectory();
            PathElement[] directoryElements = directory.GetDirectoryElements().ToArray();
            FileName filename = path.GetFilename();

            //Check output
            Assert.AreEqual(0, exceptions.Count);

            //Check directory
            Assert.AreEqual(1, directory.GetDirectoryElements().Count);
            Assert.AreEqual("site", directoryElements[0].GetPathElement());

            //Check filename
            Assert.AreEqual("home", filename.GetName().GetPathElement());
            //TODO: fix strange API
            Assert.AreEqual("html", filename.GetFileExtension().GetFileExtension());
        }

        /// <summary>
        ///A test for ParseMappings
        ///</summary>
        [TestMethod()]
        public void ParseMappingsTest()
        {

        }

        /// <summary>
        ///A test for ParseMapping
        ///</summary>
        [TestMethod()]
        public void ParseMappingTest()
        {

        }

        /// <summary>
        ///A test for ParseFileName
        ///</summary>
        [TestMethod()]
        public void ParseFileNameTest()
        {
            //Create lexer and lexicalizeStream
            WaebricLexer lexer = new WaebricLexer(new StringReader("filename.ext"));   
            lexer.LexicalizeStream();
            
            //Get tokens and parse it
            TokenIterator tokens = lexer.GetTokenIterator();
            List<Exception> exceptions = new List<Exception>();
            SiteParser siteParser = new SiteParser(tokens, exceptions);
            FileName output = siteParser.ParseFileName();

            //Check output
            Assert.AreEqual(0, exceptions.Count);
            Assert.AreEqual("filename", output.GetName().GetPathElement());
            //TODO: fix strange API
            Assert.AreEqual("ext", output.GetFileExtension().GetFileExtension());

        }

        /// <summary>
        ///A test for ParseDirectoryName
        ///</summary>
        [TestMethod()]
        public void ParseDirectoryNameTest()
        {
            //Create lexer and lexicalizeStream
            WaebricLexer lexer = new WaebricLexer(new StringReader("home\\site\\test.wae"));
            lexer.LexicalizeStream();

            //Get tokens and parse it
            TokenIterator tokens = lexer.GetTokenIterator();
            List<Exception> exceptions = new List<Exception>();
            SiteParser siteParser = new SiteParser(tokens, exceptions);
            DirName output = siteParser.ParseDirectoryName();
            Dir directory = output.GetDirectory();

            //Check output
            Assert.AreEqual(0, exceptions.Count);
            Assert.AreEqual(2, directory.GetDirectoryElements().Count);

            //Get directory's and transfer to array to provide walking
            PathElement[] directoryElements = directory.GetDirectoryElements().ToArray();

            //Check directory's
            Assert.AreEqual("home", directoryElements[0].GetPathElement());
            Assert.AreEqual("site", directoryElements[1].GetPathElement());
        }

        /// <summary>
        ///A test for ParseDirectory
        ///</summary>
        [TestMethod()]
        public void ParseDirectoryTest()
        {
            //Create lexer and lexicalizeStream
            WaebricLexer lexer = new WaebricLexer(new StringReader("directory1\\directory2\\filename.ext"));
            lexer.LexicalizeStream();

            //Get tokens and parse it
            TokenIterator tokens = lexer.GetTokenIterator();
            List<Exception> exceptions = new List<Exception>();
            SiteParser siteParser = new SiteParser(tokens, exceptions);
            Dir output = siteParser.ParseDirectory();

            //Check output
            Assert.AreEqual(0, exceptions.Count);
            Assert.AreEqual(2, output.GetDirectoryElements().Count);

            //Get directory's and transfer to array to provide walking
            PathElement[] directoryElements = output.GetDirectoryElements().ToArray();

            //Check directory's
            Assert.AreEqual("directory1", directoryElements[0].GetPathElement());
            Assert.AreEqual("directory2", directoryElements[1].GetPathElement());
        }

        /// <summary>
        ///A test for SiteParser Constructor
        ///</summary>
        [TestMethod()]
        public void SiteParserConstructorTest()
        {

        }
    }
}
