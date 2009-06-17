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
using Parser.Ast.Markup;

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
            //Get tokens and parse it
            TokenIterator tokens = Init("site/home.html");
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
            //Set up parser
            TokenIterator tokens = Init("site/home.html : home(); site2/home.html : home2()");
            List<Exception> exceptions = new List<Exception>();
            SiteParser siteParser = new SiteParser(tokens, exceptions);
            Mapping[] parsedMappings = siteParser.ParseMappings().ToArray();

            //Test output
            Assert.AreEqual(0, exceptions.Count);

            //Test mappings
            Assert.AreEqual(2, parsedMappings.Length);
        }

        /// <summary>
        ///A test for ParseMapping
        ///</summary>
        [TestMethod()]
        public void ParseSingleMappingTest()
        {
            //Set up parser
            TokenIterator tokens = Init("site/home.html : home()");
            List<Exception> exceptions = new List<Exception>();
            SiteParser siteParser = new SiteParser(tokens, exceptions);
            Mapping mapping = siteParser.ParseMapping();

            //Test output
            Assert.AreEqual(0, exceptions.Count);

            //Test path of site
            Assert.AreEqual("site/home.html", mapping.GetPath().ToString());
            
            //Test markup of site
            Markup parsedMarkup = mapping.GetMarkup();
            Assert.AreEqual("home", parsedMarkup.GetDesignator().GetIdentifier());
            Assert.AreEqual(0, parsedMarkup.GetArguments().GetArguments().Count);
        }

        /// <summary>
        ///A test for ParseFileName
        ///</summary>
        [TestMethod]
        public void ParseFileNameTest()
        {
            //Get tokens and parse it
            TokenIterator tokens = Init("filename.ext");
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
            //Get tokens and parse it
            TokenIterator tokens = Init("home\\site\\test.wae");
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
            //Get tokens and parse it
            TokenIterator tokens = Init("directory1\\directory2\\filename.ext");
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
