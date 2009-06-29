using Parser;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Lexer.Tokenizer;
using System;
using System.Collections.Generic;
using Parser.Ast.Statements;
using Lexer;
using System.IO;
using Parser.Ast.Expressions;
using Parser.Ast.Predicates;

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
            StatementParser statementParser = new StatementParser(Init("yield;"));
            YieldStatement parsedYield = statementParser.ParseYieldStatement();

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
            //Create parser
            StatementParser statementParser = new StatementParser(Init("if (condition1 || condition2) echo \"test\";"));
            Statement parsedStatement = statementParser.ParseIfStatement();

            //Check IfStatement
            Assert.AreEqual(typeof(IfStatement), parsedStatement.GetType());
            
            IfStatement parsedIfStatement = (IfStatement)parsedStatement;
            Assert.AreEqual(typeof(OrPredicate), parsedIfStatement.GetPredicate().GetType());
            Assert.AreEqual("condition1||condition2", parsedIfStatement.GetPredicate().ToString());
            Assert.AreEqual(typeof(EchoExpressionStatement), parsedIfStatement.GetTrueStatement().GetType());
            Assert.AreEqual("echo test", parsedIfStatement.GetTrueStatement().ToString());
        }

        /// <summary>
        ///A test for ParseIfElseStatement
        ///</summary>
        [TestMethod()]
        public void ParseIfElseStatementTest()
        {
            //Create parser
            StatementParser statementParser = new StatementParser(Init("if (condition1 || condition2) echo \"test\"; else echo \"test2\";"));
            Statement parsedStatement = statementParser.ParseIfStatement();

            //Check IfStatement
            Assert.AreEqual(typeof(IfElseStatement), parsedStatement.GetType());

            IfElseStatement parsedIfElseStatement = (IfElseStatement)parsedStatement;
            Assert.AreEqual(typeof(OrPredicate), parsedIfElseStatement.GetPredicate().GetType());
            Assert.AreEqual("condition1||condition2", parsedIfElseStatement.GetPredicate().ToString());
            Assert.AreEqual(typeof(EchoExpressionStatement), parsedIfElseStatement.GetTrueStatement().GetType());
            Assert.AreEqual("echo test", parsedIfElseStatement.GetTrueStatement().ToString());
            Assert.AreEqual(typeof(EchoExpressionStatement), parsedIfElseStatement.GetFalseStatement().GetType());
            Assert.AreEqual("echo test2", parsedIfElseStatement.GetFalseStatement().ToString());
        }

        /// <summary>
        ///A test for ParseEchoExpressionStatement
        ///</summary>
        [TestMethod()]
        public void ParseEchoExpressionStatementTest()
        {
            //Create parser
            StatementParser statementParser = new StatementParser(Init("echo \"test\";"));
            EchoStatement parsedEchoStatement = statementParser.ParseEchoStatement();

            //Check echo statement
            Assert.AreEqual(typeof(EchoExpressionStatement), parsedEchoStatement.GetType());
            Assert.AreEqual("echo test;", parsedEchoStatement.ToString());
        }

        /// <summary>
        ///A test for ParseEchoEmbeddingStatement
        ///</summary>
        [TestMethod()]
        public void ParseEchoEmbeddingStatementTest()
        {
            //Create parser
            StatementParser statementParser = new StatementParser(Init("echo \"left<func1() \"text\">right\";"));
            EchoStatement parsedEchoStatement = statementParser.ParseEchoStatement();

            //Check echo statement
            Assert.AreEqual(typeof(EchoEmbeddingStatement), parsedEchoStatement.GetType());
            Assert.AreEqual("echo \"left<func1() \"text\">right\";", parsedEchoStatement.ToString());
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
