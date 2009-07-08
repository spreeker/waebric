using Checker;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Parser.Ast;
using System;
using System.Collections.Generic;
using System.Collections;
using System.IO;
using Lexer;
using Lexer.Tokenizer;
using Parser;
using Common;
using Parser.Ast.Module;

namespace TestChecker
{
    
    
    /// <summary>
    ///This is a test class for WaebricCheckerTest and is intended
    ///to contain all WaebricCheckerTest Unit Tests
    ///</summary>
    [TestClass()]
    public class WaebricCheckerTest
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
        /// Test for WaebricChecker with imports
        /// </summary>
        [TestMethod()]
        public void WaebricCheckerImportTest()
        {
            //First parse the initial file and then request the rest
            //Let's lexicalize the file
            StreamReader sourceStream = new StreamReader("../../../../TestChecker/ImportTest/home.wae");
            WaebricLexer lexer = new WaebricLexer(sourceStream);

            lexer.LexicalizeStream();
            TokenIterator tokens = lexer.GetTokenIterator();

            //Lets parse the file
            WaebricParser parser = new WaebricParser(tokens);
            parser.Parse();

            SyntaxTree parsedTree = parser.GetTree();

            //Initialize ModuleCache with correct DirectoryPath
            ModuleCache.Instance.SetDirectoryPath("../../../../TestChecker/ImportTest/");

            //Lets check the tree
            WaebricChecker checker = new WaebricChecker();
            List<Exception> checkerExceptions = checker.CheckSyntaxTree(parsedTree);

            //Test output
            Assert.AreEqual(0, checkerExceptions.Count);

            //Test if all modules except tree root are in cache
            Assert.IsTrue(ModuleCache.Instance.ContainsModule("first"));
            Assert.IsTrue(ModuleCache.Instance.ContainsModule("second"));
            Assert.IsTrue(ModuleCache.Instance.ContainsModule("common"));
        }
    }
}
