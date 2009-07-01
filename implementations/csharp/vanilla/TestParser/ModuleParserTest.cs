using Parser;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Lexer.Tokenizer;
using System;
using System.Collections.Generic;
using System.IO;
using Lexer;
using Parser.Ast;
using Parser.Ast.Module;
using Parser.Ast.Site;

namespace TestParser
{
    
    
    /// <summary>
    ///This is a test class for ModuleParserTest and is intended
    ///to contain all ModuleParserTest Unit Tests
    ///</summary>
    [TestClass()]
    public class ModuleParserTest
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
        ///Test Module Parser with single module definition
        ///</summary>
        [TestMethod()]
        public void ModuleParserSingleModuleTest()
        {
            SyntaxTree tree;

            //Create lexer to tokenize stream
            WaebricLexer lexer = new WaebricLexer(new StringReader("module test"));
            lexer.LexicalizeStream();
            
            //Retrieve tokenIterator from lexer and lets parse it
            WaebricParser parser = new WaebricParser(lexer.GetTokenIterator());
            parser.Parse();
            
            //Test if root is modulelist and it contains the right module
            tree = parser.GetTree();

            Module module = tree.GetRoot();
            String[] identifiers = module.GetModuleId().GetIdentifiers().ToArray();
            Assert.AreEqual(1, identifiers.Length);
            Assert.AreEqual("test", identifiers[0]);
        }

        [TestMethod]
        public void ModuleParserComplexModuleNameTest()
        {
            SyntaxTree tree;

            //Create lexer to tokenize stream
            WaebricLexer lexer = new WaebricLexer(new StringReader("module test.test2.test3"));
            lexer.LexicalizeStream();

            //Retrieve tokenIterator from lexer and lets parse it
            WaebricParser parser = new WaebricParser(lexer.GetTokenIterator());
            parser.Parse();

            //Test if root is modulelist and it contains the right module
            tree = parser.GetTree();

            Module module = tree.GetRoot();
            Assert.AreEqual(3, module.GetModuleId().GetIdentifiers().Count);
            Assert.AreEqual("test.test2.test3", module.GetModuleId().ToString());
        }

        /// <summary>
        /// Test Module with import
        /// </summary>
        [TestMethod]
        public void ModuleParserImportTest()
        {
            SyntaxTree tree;

            //Create lexer to tokenize stream
            WaebricLexer lexer = new WaebricLexer(new StringReader("module test\n\nimport importtest"));
            lexer.LexicalizeStream();

            //Test if stream is lexicalized into 4 tokens
            Assert.IsTrue(lexer.GetTokenIterator().GetSize() == 4);

            //Retrieve tokenIterator from lexer and lets parse it
            WaebricParser parser = new WaebricParser(lexer.GetTokenIterator());
            parser.Parse();

            //Test tree structure
            tree = parser.GetTree();

            Module module = tree.GetRoot();

            //ISyntaxNode[] moduleElements = module.GetElements();

  //          Assert.IsTrue(moduleElements[0].GetType() == typeof(Import));

    //        Import import = (Import) moduleElements[0];
            //TODO: As shown here it takes to much methods to get real identifier
      //      Assert.IsTrue(import.GetIdentifier().GetIdentifier().ToString().ToString() == "importtest");
        }

        /// <summary>
        /// Test module with site 
        /// </summary>
        [TestMethod]
        public void ModuleParserSiteTest()
        {
            SyntaxTree tree = new SyntaxTree();

            //Create lexer to tokenize stream
            WaebricLexer lexer = new WaebricLexer(new StringReader("module test\n\nsite\n  site/index.html : home() ; site/index2.html : home()\nend"));
            lexer.LexicalizeStream();

            //Parse tokenized stream
            ModuleParser parser = new ModuleParser(lexer.GetTokenIterator());
            tree.SetRoot(parser.ParseModule());

            //Check module
            Module module = tree.GetRoot();
            Assert.IsTrue(module.GetModuleId().ToString() == "test");
            Assert.AreEqual(0, module.GetImports().Count); //No imports
            Assert.AreEqual(0, module.GetFunctionDefinitions().Count); //No function definitions
            Assert.AreEqual(1, module.GetSites().Count); //One site

            //Check site
            Site[] sites = module.GetSites().ToArray();
            Assert.AreEqual(2, sites[0].GetMappings().Count);
        }
    }
}
