using Parser;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Lexer.Tokenizer;
using System;
using System.Collections.Generic;
using System.IO;
using Lexer;
using Parser.Ast;
using Parser.Ast.Module;

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
        ///A test for ModuleParser Constructor
        ///</summary>
        [TestMethod()]
        public void ModuleParserSingleModuleTest()
        {
            List<Exception> exceptions;
            SyntaxTree tree;

            //Create lexer to tokenize stream
            WaebricLexer lexer = new WaebricLexer(new StringReader("module test"));
            lexer.LexicalizeStream();
            
            //Retrieve tokenIterator from lexer and lets parse it
            WaebricParser parser = new WaebricParser(lexer.GetTokenIterator());
            exceptions = parser.Parse();

            //Test no exceptions during parsing
            Assert.IsTrue(exceptions.Count == 0);
            
            //Test if root is modulelist and it contains the right module
            tree = parser.GetTree();

            Assert.IsTrue(tree.GetRoot().GetType().Equals(typeof(ModuleList)));
             
            ModuleList modules = (ModuleList) tree.GetRoot();
            Assert.IsTrue(modules.GetSize() == 1); //Contains only 1 module

            Module module = (Module) modules.Get(0);
            Assert.IsTrue(module.GetIdentifier().ToString() == "test");
        }
    }
}
