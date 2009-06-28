using Parser;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Lexer.Tokenizer;
using System;
using System.Collections.Generic;
using Parser.Ast.Statements;
using Lexer;
using System.IO;
using Parser.Ast.Expressions;

namespace TestParser
{
    
    
    /// <summary>
    ///This is a test class for StatementParserTest and is intended
    ///to contain all StatementParserTest Unit Tests
    ///</summary>
    [TestClass()]
    public class StatementParserTest
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
        ///A test for ParseYieldStatement
        ///</summary>
        [TestMethod()]
        public void ParseYieldStatementTest()
        {
            //Create parser
            List<Exception> exceptions = new List<Exception>();
            StatementParser statementParser = new StatementParser(Init("yield;"));
            YieldStatement parsedYield = statementParser.ParseYieldStatement();

            //Test output
            Assert.AreEqual(0, exceptions.Count);

            //Test yield instance
            Assert.AreEqual(typeof(YieldStatement), parsedYield.GetType());
            Assert.AreEqual("yield;", parsedYield.ToString());
        }

        /// <summary>
        ///A test for ParseStatement
        ///</summary>
        [TestMethod()]
        public void ParseStatementTest()
        {
            TokenIterator iterator = null; // TODO: Initialize to an appropriate value
            StatementParser target = new StatementParser(iterator); // TODO: Initialize to an appropriate value
            Statement expected = null; // TODO: Initialize to an appropriate value
            Statement actual;
            actual = target.ParseStatement();
            Assert.AreEqual(expected, actual);
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for ParseLetStatement
        ///</summary>
        [TestMethod()]
        public void ParseLetStatementTest()
        {
            TokenIterator iterator = null; // TODO: Initialize to an appropriate value
            StatementParser target = new StatementParser(iterator); // TODO: Initialize to an appropriate value
            LetStatement expected = null; // TODO: Initialize to an appropriate value
            LetStatement actual;
            actual = target.ParseLetStatement();
            Assert.AreEqual(expected, actual);
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for ParseIfStatement
        ///</summary>
        [TestMethod()]
        public void ParseIfStatementTest()
        {
            TokenIterator iterator = null; // TODO: Initialize to an appropriate value
            StatementParser target = new StatementParser(iterator); // TODO: Initialize to an appropriate value
            Statement expected = null; // TODO: Initialize to an appropriate value
            Statement actual;
            actual = target.ParseIfStatement();
            Assert.AreEqual(expected, actual);
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for ParseEchoStatement
        ///</summary>
        [TestMethod()]
        public void ParseEchoStatementTest()
        {
            TokenIterator iterator = null; // TODO: Initialize to an appropriate value
            StatementParser target = new StatementParser(iterator); // TODO: Initialize to an appropriate value
            EchoStatement expected = null; // TODO: Initialize to an appropriate value
            EchoStatement actual;
            actual = target.ParseEchoStatement();
            Assert.AreEqual(expected, actual);
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for ParseEchoEmbeddingStatement
        ///</summary>
        [TestMethod()]
        public void ParseEchoEmbeddingStatementTest()
        {
        }

        /// <summary>
        ///A test for ParseEachStatement
        ///</summary>
        [TestMethod()]
        public void ParseEachStatementTest()
        {
            TokenIterator iterator = null; // TODO: Initialize to an appropriate value
            StatementParser target = new StatementParser(iterator); // TODO: Initialize to an appropriate value
            EachStatement expected = null; // TODO: Initialize to an appropriate value
            EachStatement actual;
            actual = target.ParseEachStatement();
            Assert.AreEqual(expected, actual);
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for ParseCommentStatement
        ///</summary>
        [TestMethod()]
        public void ParseCommentStatementTest()
        {
            TokenIterator iterator = null; // TODO: Initialize to an appropriate value
            StatementParser target = new StatementParser(iterator); // TODO: Initialize to an appropriate value
            CommentStatement expected = null; // TODO: Initialize to an appropriate value
            CommentStatement actual;
            actual = target.ParseCommentStatement();
            Assert.AreEqual(expected, actual);
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for ParseCdataStatement
        ///</summary>
        [TestMethod()]
        public void ParseCdataStatementTest()
        {
            TokenIterator iterator = null; // TODO: Initialize to an appropriate value
            StatementParser target = new StatementParser(iterator); // TODO: Initialize to an appropriate value
            CdataStatement expected = null; // TODO: Initialize to an appropriate value
            CdataStatement actual;
            actual = target.ParseCdataStatement();
            Assert.AreEqual(expected, actual);
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for ParseBlockStatement
        ///</summary>
        [TestMethod()]
        public void ParseBlockStatementTest()
        {
            TokenIterator iterator = null; // TODO: Initialize to an appropriate value
            StatementParser target = new StatementParser(iterator); // TODO: Initialize to an appropriate value
            BlockStatement expected = null; // TODO: Initialize to an appropriate value
            BlockStatement actual;
            actual = target.ParseBlockStatement();
            Assert.AreEqual(expected, actual);
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        /// A test for ParseAssignment
        /// </summary>
        [TestMethod()]
        public void ParseAssignmentTest()
        {
            //Create parser
            StatementParser statementParser = new StatementParser(Init("var1 = \"test\";"));
            Assignment parsedAssignment = statementParser.ParseAssignment();

            //Test assignment
            Assert.AreEqual("var1", parsedAssignment.GetIdentifier());
            Assert.AreEqual(typeof(TextExpression), parsedAssignment.GetExpression().GetType());
            Assert.AreEqual("test", parsedAssignment.GetExpression().ToString());
            
        }
    }
}
