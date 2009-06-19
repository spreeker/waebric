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
            ExpressionParser expressionParser = new ExpressionParser(Init("'symbol"), exceptions);
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
            //Create parser and parse tokens
            List<Exception> exceptions = new List<Exception>();
            ExpressionParser expressionParser = new ExpressionParser(Init("{token1:\"token1\", token2:10, token3:'symbol}"), exceptions);
            RecordExpression expression = expressionParser.ParseRecordExpression();

            //Test output
            Assert.AreEqual(0, exceptions.Count);
            
            //Test Record contents
            Assert.AreEqual(3, expression.GetRecords().Count);
            KeyValuePair[] recordList = expression.GetRecords().ToArray();
            
            Assert.AreEqual("token1", recordList[0].GetKey());
            Assert.AreEqual(typeof(TextExpression), recordList[0].GetValue().GetType());
            Assert.AreEqual("token1", recordList[0].GetValue().ToString());

            Assert.AreEqual("token2", recordList[1].GetKey());
            Assert.AreEqual(typeof(NumExpression), recordList[1].GetValue().GetType());
            Assert.AreEqual(10, ((NumExpression)recordList[1].GetValue()).GetNum());

            Assert.AreEqual("token3", recordList[2].GetKey());
            Assert.AreEqual(typeof(SymExpression), recordList[2].GetValue().GetType());
            Assert.AreEqual("symbol", recordList[2].GetValue().ToString());
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
            //Create parser and parse tokens
            List<Exception> exceptions = new List<Exception>();
            ExpressionParser expressionParser = new ExpressionParser(Init("[var1, var2, var3, var4]"), exceptions);
            ListExpression listExpression = expressionParser.ParseListExpression();

            //Test output
            Assert.AreEqual(0, exceptions.Count);
            
            //Test items of list
            Assert.AreEqual(4, listExpression.GetExpressions().Count);
            Expression[] expressions = listExpression.GetExpressions().ToArray();
            Assert.AreEqual("var1", expressions[0].ToString());
            Assert.AreEqual("var2", expressions[1].ToString());
            Assert.AreEqual("var3", expressions[2].ToString());
            Assert.AreEqual("var4", expressions[3].ToString());
        }

        /// <summary>
        ///A test for ParseKeyValuePair
        ///</summary>
        [TestMethod()]
        public void ParseKeyValuePairTest()
        {
            //Create parser and parse tokens
            List<Exception> exceptions = new List<Exception>();
            ExpressionParser expressionParser = new ExpressionParser(Init("token1:\"value1\""), exceptions);
            KeyValuePair keyValuePair = expressionParser.ParseKeyValuePair();

            //Test output
            Assert.AreEqual(0, exceptions.Count);

            //Check key
            Assert.AreEqual("token1", keyValuePair.GetKey());

            //Check value
            Assert.AreEqual(typeof(TextExpression), keyValuePair.GetValue().GetType());
            TextExpression textExpression = (TextExpression) keyValuePair.GetValue();
            Assert.AreEqual("value1", textExpression.GetText());
        }

        /// <summary>
        ///A test for ParseFieldExpression
        ///</summary>
        [TestMethod()]
        public void ParseFieldExpressionTest()
        {
            //Create parser and parse tokens
            List<Exception> exceptions = new List<Exception>();
            ExpressionParser expressionParser = new ExpressionParser(Init("expressie.identifier"), exceptions);
            Expression expression = expressionParser.ParseExpression();

            //Test output
            Assert.AreEqual(0, exceptions.Count);

            //Test type of expression
            Assert.AreEqual(typeof(FieldExpression), expression.GetType());
            FieldExpression parsedFieldExpression = (FieldExpression)expression;
            
            //Test field members
            Assert.AreEqual("expressie", parsedFieldExpression.GetExpression().ToString());
            Assert.AreEqual(typeof(VarExpression), parsedFieldExpression.GetExpression().GetType());
            Assert.AreEqual("identifier", parsedFieldExpression.GetIdentifier());
        }

        /// <summary>
        ///A test for ParseExpression
        ///</summary>
        [TestMethod()]
        public void ParseExpressionTest()
        {
            //Create parser and parse tokens
            List<Exception> exceptions = new List<Exception>();
            ExpressionParser expressionParser = new ExpressionParser(Init("expressie.identifier"), exceptions);
            Expression expression = expressionParser.ParseExpression();

            //Test output
            Assert.AreEqual(0, exceptions.Count);

            //Test type of expression
            Assert.AreEqual(typeof(FieldExpression), expression.GetType());
        }

        /// <summary>
        ///A test for ParseCatExpression
        ///</summary>
        [TestMethod()]
        public void ParseCatExpressionTest()
        {
            //Create parser and parse tokens
            List<Exception> exceptions = new List<Exception>();
            ExpressionParser expressionParser = new ExpressionParser(Init("expression+'symbol"), exceptions);
            Expression expression = expressionParser.ParseExpression();

            //Check output
            Assert.AreEqual(0, exceptions.Count);

            //Check type of expression
            Assert.AreEqual(typeof(CatExpression), expression.GetType());
            CatExpression parsedCatExpression = (CatExpression)expression;

            //Check expressions in catexpression
            Assert.AreEqual(typeof(VarExpression), parsedCatExpression.GetLeftExpression().GetType());
            Assert.AreEqual(typeof(SymExpression), parsedCatExpression.GetRightExpression().GetType());

            VarExpression left = (VarExpression) parsedCatExpression.GetLeftExpression();
            SymExpression right = (SymExpression)parsedCatExpression.GetRightExpression();
            Assert.AreEqual("expression", left.GetVariableIdentifier());
            Assert.AreEqual("symbol", right.GetSym());
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
    }
}
