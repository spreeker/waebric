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
using Parser.Ast.Markup;
using Parser.Ast;

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

            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for ParseLetStatement
        ///</summary>
        [TestMethod()]
        public void ParseLetStatementTest()
        {
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
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for ParseCommentStatement
        ///</summary>
        [TestMethod()]
        public void ParseCommentStatementTest()
        {
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for ParseCdataStatement
        ///</summary>
        [TestMethod()]
        public void ParseCdataStatementTest()
        {
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for ParseBlockStatement
        ///</summary>
        [TestMethod()]
        public void ParseBlockStatementTest()
        {
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
            Assert.AreEqual(typeof(VarBindAssignment), parsedAssignment.GetType());

            //Test VarBindAssignment
            VarBindAssignment parsedVarAssignment = (VarBindAssignment)parsedAssignment;
            Assert.AreEqual("var1", parsedVarAssignment.GetIdentifier());
            Assert.AreEqual(typeof(TextExpression), parsedVarAssignment.GetExpression().GetType());
            Assert.AreEqual("test", parsedVarAssignment.GetExpression().ToString());
        }

        /// <summary>
        /// A test for VarBindAssignment
        /// </summary>
        [TestMethod()]
        public void ParseVarBindAssignmentTest()
        {
            //Create parser
            StatementParser statementParser = new StatementParser(Init("var1 = \"test\";"));
            VarBindAssignment parsedVarAssignment = statementParser.ParseVarBindAssignment();

            //Test VarBindAssignment
            Assert.AreEqual("var1", parsedVarAssignment.GetIdentifier());
            Assert.AreEqual(typeof(TextExpression), parsedVarAssignment.GetExpression().GetType());
            Assert.AreEqual("test", parsedVarAssignment.GetExpression().ToString());
        }


        /// <summary>
        /// A test for FuncBindAssignment
        /// </summary>
        [TestMethod()]
        public void ParseFuncBindAssingmentTest()
        {
            //Create parser
            StatementParser statementParser = new StatementParser(Init("home(test1, test2) = {}"));
            FuncBindAssignment parsedFuncAssignment = statementParser.ParseFuncBindAssignment();

            //Test FuncBindAssignment
            Assert.AreEqual("home", parsedFuncAssignment.GetIdentifier());
            Assert.AreEqual(2, parsedFuncAssignment.GetIdentifiers().Count);

            String[] identifierArray = parsedFuncAssignment.GetIdentifiers().ToArray();
            Assert.AreEqual("test1", identifierArray[0]);
            Assert.AreEqual("test2", identifierArray[1]);

            Assert.AreEqual(typeof(BlockStatement), parsedFuncAssignment.GetStatement().GetType());
            BlockStatement statement = (BlockStatement) parsedFuncAssignment.GetStatement();
            Assert.AreEqual(0, statement.GetStatements().Count);
        }

        [TestMethod()]
        public void ParseMarkupStatStatementTest()
        {
            //Create parser
            StatementParser statementParser = new StatementParser(Init("p { echo \"test\"; }"));
            Statement parsedStatement = statementParser.ParseMarkupStatement();

            //Test Statement
            Assert.AreEqual(typeof(MarkupStatStatement), parsedStatement.GetType());

            //Test MarkupStatStatement
            MarkupStatStatement markupStatStatement = (MarkupStatStatement)parsedStatement;
            Assert.AreEqual(1, markupStatStatement.GetMarkups().Count);

            //Test markup
            Markup markup = (Markup) markupStatStatement.GetMarkups().Get(0);
            Assert.AreEqual("p", markup.GetDesignator().GetIdentifier());
            Assert.AreEqual(0, markup.GetArguments().Count);

            //Test statement
            Assert.AreEqual(typeof(BlockStatement), markupStatStatement.GetStatement().GetType());
            BlockStatement statement = (BlockStatement)markupStatStatement.GetStatement();
            Assert.AreEqual(1, statement.GetStatements().Count);

            Statement stmt = (Statement) statement.GetStatements().Get(0);
            Assert.AreEqual(typeof(EchoExpressionStatement), stmt.GetType());
            Assert.AreEqual("echo test;", stmt.ToString());
        }
    }
}
