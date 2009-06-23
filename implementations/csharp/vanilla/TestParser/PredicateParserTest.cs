using Parser;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Lexer.Tokenizer;
using System;
using System.Collections.Generic;
using Parser.Ast.Predicates;
using Lexer;
using System.IO;
using Parser.Ast.Expressions;

namespace TestParser
{
    
    
    /// <summary>
    ///This is a test class for PredicateParserTest and is intended
    ///to contain all PredicateParserTest Unit Tests
    ///</summary>
    [TestClass()]
    public class PredicateParserTest
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
        ///A test for ParsePredicate
        ///</summary>
        [TestMethod()]
        public void ParsePredicateTest()
        {
            TokenIterator iterator = null; // TODO: Initialize to an appropriate value
            List<Exception> exceptions = null; // TODO: Initialize to an appropriate value
            PredicateParser target = new PredicateParser(iterator, exceptions); // TODO: Initialize to an appropriate value
            Predicate expected = null; // TODO: Initialize to an appropriate value
            Predicate actual;
            actual = target.ParsePredicate();
            Assert.AreEqual(expected, actual);
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for ParseOrPredicate
        ///</summary>
        [TestMethod()]
        public void ParseOrPredicateTest()
        {
            //Create parser
            List<Exception> exceptions = new List<Exception>();
            PredicateParser predicateParser = new PredicateParser(Init("condition1 || condition2 || condition3"), exceptions);
            Predicate parsedPredicate = predicateParser.ParsePredicate();

            //Test output
            Assert.AreEqual(0, exceptions.Count);

            //Check Predicates
            Assert.AreEqual(typeof(OrPredicate), parsedPredicate.GetType());

            //Check OrPredicate
            OrPredicate parsedOrPredicate = (OrPredicate)parsedPredicate;
            Assert.AreEqual(typeof(ExpressionPredicate), parsedOrPredicate.GetLeftPredicate().GetType());
            Assert.AreEqual(typeof(OrPredicate), parsedOrPredicate.GetRightPredicate().GetType());

            //Check nested OrPredicate
            OrPredicate parsedNestedOrPredicate = (OrPredicate)parsedOrPredicate.GetRightPredicate();
            Assert.AreEqual(typeof(ExpressionPredicate), parsedNestedOrPredicate.GetLeftPredicate().GetType());
            Assert.AreEqual(typeof(ExpressionPredicate), parsedNestedOrPredicate.GetRightPredicate().GetType());
        }

        /// <summary>
        ///A test for ParseIsPredicate
        ///</summary>
        [TestMethod()]
        public void ParseIsPredicateTest()
        {
            //Create parser
            List<Exception> exceptions = new List<Exception>();
            PredicateParser predicateParser = new PredicateParser(Init(".string?"), exceptions);
            ExpressionParser expressionParser = new ExpressionParser(Init("test"), exceptions);
            Expression parsedExpression = expressionParser.ParseExpression();
            IsPredicate parsedIsPredicate = predicateParser.ParseIsPredicate(parsedExpression);

            //Test output
            Assert.AreEqual(0, exceptions.Count);

            //Check expression
            Assert.AreEqual(typeof(VarExpression), parsedIsPredicate.GetExpression().GetType());
            Assert.AreEqual("test", parsedIsPredicate.GetExpression().ToString());

            //Check type
            Assert.AreEqual(typeof(StringType), parsedIsPredicate.GetType().GetType());
        }

        /// <summary>
        ///A test for ParseAndPredicate
        ///</summary>
        [TestMethod()]
        public void ParseAndPredicateTest()
        {
            //Create parser
            List<Exception> exceptions = new List<Exception>();
            PredicateParser predicateParser = new PredicateParser(Init("condition1 && test.list? && condition2"), exceptions);
            Predicate parsedPredicate = predicateParser.ParsePredicate();

            //Test output
            Assert.AreEqual(0, exceptions.Count);

            //Check Predicates
            Assert.AreEqual(typeof(AndPredicate), parsedPredicate.GetType());

            //Check AndPredicate
            AndPredicate parsedAndPredicate = (AndPredicate) parsedPredicate;
            Assert.AreEqual(typeof(ExpressionPredicate), parsedAndPredicate.GetLeftPredicate().GetType());
            Assert.AreEqual(typeof(AndPredicate), parsedAndPredicate.GetRightPredicate().GetType());

            //Check nested AndPredicate
            AndPredicate parsedNestedAndPredicate = (AndPredicate)parsedAndPredicate.GetRightPredicate();
            Assert.AreEqual(typeof(IsPredicate), parsedNestedAndPredicate.GetLeftPredicate().GetType());
            Assert.AreEqual(typeof(ExpressionPredicate), parsedNestedAndPredicate.GetRightPredicate().GetType());
        }

        // <summary>
        ///A test for ParseNotPredicate
        ///</summary>
        [TestMethod]
        public void ParseNotPredicateTest()
        {
            //Create parser
            List<Exception> exceptions = new List<Exception>();
            PredicateParser predicateParser = new PredicateParser(Init("! condition1 && test.list?"), exceptions);
            Predicate parsedPredicate = predicateParser.ParsePredicate();

            //Test output
            Assert.AreEqual(0, exceptions.Count);

            //Check Predicates
            Assert.AreEqual(typeof(NotPredicate), parsedPredicate.GetType());

            //Check AndPredicate
            Predicate parsedInnerPredicate = ((NotPredicate)parsedPredicate).GetPredicate();
            Assert.AreEqual(typeof(AndPredicate), parsedInnerPredicate.GetType());
            AndPredicate parsedInnerAndPredicate = (AndPredicate)parsedInnerPredicate;
            Assert.AreEqual(typeof(ExpressionPredicate), parsedInnerAndPredicate.GetLeftPredicate().GetType());
            Assert.AreEqual(typeof(IsPredicate), parsedInnerAndPredicate.GetRightPredicate().GetType());
        }

    }
}
