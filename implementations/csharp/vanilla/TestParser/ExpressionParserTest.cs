using Parser;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Lexer.Tokenizer;
using System;
using System.Collections.Generic;
using Parser.Ast.Expressions;
using Lexer;
using System.IO;
using Parser.Ast;

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
            ExpressionParser expressionParser = new ExpressionParser(Init("home"));
            VarExpression expression = expressionParser.ParseVarExpression();

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
            ExpressionParser expressionParser = new ExpressionParser(Init("'symbol"));
            SymExpression expression = expressionParser.ParseSymExpression();

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
            ExpressionParser expressionParser = new ExpressionParser(Init("{token1:\"token1\", token2:10, token3:'symbol}"));
            RecordExpression expression = expressionParser.ParseRecordExpression();
            
            //Test Record contents
            Assert.AreEqual(3, expression.GetRecords().Count);
            List<ISyntaxNode>.Enumerator recordEnumerator = expression.GetRecords().GetEnumerator();

            recordEnumerator.MoveNext();
            KeyValuePair current = (KeyValuePair) recordEnumerator.Current;

            Assert.AreEqual("token1", current.GetKey());
            Assert.AreEqual(typeof(TextExpression), current.GetValue().GetType());
            Assert.AreEqual("\"token1\"", current.GetValue().ToString());

            recordEnumerator.MoveNext();
            current = (KeyValuePair)recordEnumerator.Current;

            Assert.AreEqual("token2", current.GetKey());
            Assert.AreEqual(typeof(NumExpression), current.GetValue().GetType());
            Assert.AreEqual(10, ((NumExpression)current.GetValue()).GetNum());

            recordEnumerator.MoveNext();
            current = (KeyValuePair)recordEnumerator.Current;

            Assert.AreEqual("token3", current.GetKey());
            Assert.AreEqual(typeof(SymExpression), current.GetValue().GetType());
            Assert.AreEqual("'symbol", current.GetValue().ToString());
        }

        /// <summary>
        ///A test for ParseNumExpression
        ///</summary>
        [TestMethod()]
        public void ParseNumExpressionTest()
        {
            //Create parser and parse tokens
            ExpressionParser expressionParser = new ExpressionParser(Init("1230"));
            NumExpression expression = expressionParser.ParseNumExpression();

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
            ExpressionParser expressionParser = new ExpressionParser(Init("[var1, var2, var3, var4]"));
            ListExpression listExpression = expressionParser.ParseListExpression();
            
            //Test items of list
            Assert.AreEqual(4, listExpression.GetExpressions().Count);
            Expression[] expressions = (Expression[]) listExpression.GetExpressions().ToArray();
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
            ExpressionParser expressionParser = new ExpressionParser(Init("token1:\"value1\""));
            KeyValuePair keyValuePair = expressionParser.ParseKeyValuePair();

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
            ExpressionParser expressionParser = new ExpressionParser(Init("expressie.identifier"));
            Expression expression = expressionParser.ParseExpression();

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
            ExpressionParser expressionParser = new ExpressionParser(Init("expressie.identifier"));
            Expression expression = expressionParser.ParseExpression();

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
            ExpressionParser expressionParser = new ExpressionParser(Init("expression+'symbol"));
            Expression expression = expressionParser.ParseExpression();

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
            ExpressionParser expressionParser = new ExpressionParser(Init("text"));
            TextExpression expression = expressionParser.ParseTextExpression();

            //Test variable identifier
            Assert.AreEqual("text", expression.GetText());
        }
    }
}
