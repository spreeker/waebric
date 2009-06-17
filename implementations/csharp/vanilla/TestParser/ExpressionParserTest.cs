using Parser;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Lexer.Tokenizer;
using System;
using System.Collections.Generic;
using Parser.Ast.Expressions;
using Lexer;
using System.IO;

namespace TestParser
{
    
    
    /// <summary>
    ///This is a test class for ExpressionParserTest and is intended
    ///to contain all ExpressionParserTest Unit Tests
    ///</summary>
    [TestClass()]
    public class ExpressionParserTest
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
        ///A test for ParseVarExpression
        ///</summary>
        [TestMethod()]
        public void ParseVarExpressionTest()
        {
            //Create parser and parse tokens
            List<Exception> exceptions = new List<Exception>();
            ExpressionParser expressionParser = new ExpressionParser(Init("home"), exceptions);
            VarExpression expression = expressionParser.ParseVarExpression();

            //Test output
            Assert.AreEqual(0, exceptions.Count);

            //Test variable identifier
            Assert.AreEqual("home", expression.GetVariableIdentifier());

        }

        /// <summary>
        ///A test for ParseSymExpression
        ///</summary>
        [TestMethod()]
        public void ParseSymExpressionTest()
        {
            //Create parser and parse tokens
            List<Exception> exceptions = new List<Exception>();
            ExpressionParser expressionParser = new ExpressionParser(Init("symbol"), exceptions);
            SymExpression expression = expressionParser.ParseSymExpression();

            //Test output
            Assert.AreEqual(0, exceptions.Count);

            //Test variable identifier
            Assert.AreEqual("symbol", expression.GetSym());
        }

        /// <summary>
        ///A test for ParseRecordExpression
        ///</summary>
        [TestMethod()]
        public void ParseRecordExpressionTest()
        {
   
        }

        /// <summary>
        ///A test for ParseNumExpression
        ///</summary>
        [TestMethod()]
        public void ParseNumExpressionTest()
        {
            //Create parser and parse tokens
            List<Exception> exceptions = new List<Exception>();
            ExpressionParser expressionParser = new ExpressionParser(Init("1230"), exceptions);
            NumExpression expression = expressionParser.ParseNumExpression();

            //Test output
            Assert.AreEqual(0, exceptions.Count);

            //Test variable identifier
            Assert.AreEqual(1230, expression.GetNum());
        }

        /// <summary>
        ///A test for ParseListExpression
        ///</summary>
        [TestMethod()]
        public void ParseListExpressionTest()
        {

        }

        /// <summary>
        ///A test for ParseKeyValuePair
        ///</summary>
        [TestMethod()]
        public void ParseKeyValuePairTest()
        {

        }

        /// <summary>
        ///A test for ParseFieldExpression
        ///</summary>
        [TestMethod()]
        public void ParseFieldExpressionTest()
        {

        }

        /// <summary>
        ///A test for ParseExpression
        ///</summary>
        [TestMethod()]
        public void ParseExpressionTest()
        {

        }

        /// <summary>
        ///A test for ParseCatExpression
        ///</summary>
        [TestMethod()]
        public void ParseCatExpressionTest()
        {

        }


        [TestMethod]
        public void ParseTextExpressionTest()
        {
            //Create parser and parse tokens
            List<Exception> exceptions = new List<Exception>();
            ExpressionParser expressionParser = new ExpressionParser(Init("text"), exceptions);
            TextExpression expression = expressionParser.ParseTextExpression();

            //Test output
            Assert.AreEqual(0, exceptions.Count);

            //Test variable identifier
            Assert.AreEqual("text", expression.GetText());
        }

        /// <summary>
        ///A test for ExpressionParser Constructor
        ///</summary>
        [TestMethod()]
        public void ExpressionParserConstructorTest()
        {

        }
    }
}
