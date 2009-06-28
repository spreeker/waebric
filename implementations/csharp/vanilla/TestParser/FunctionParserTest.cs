using Parser;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Lexer.Tokenizer;
using System;
using System.Collections.Generic;
using Parser.Ast.Functions;
using Lexer;
using System.IO;

namespace TestParser
{
    
    
    /// <summary>
    ///This is a test class for FunctionParserTest and is intended
    ///to contain all FunctionParserTest Unit Tests
    ///</summary>
    [TestClass()]
    public class FunctionParserTest
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
        ///A test for ParseFormal
        ///</summary>
        [TestMethod()]
        public void ParseFormalTest()
        {
            FunctionParser functionParser = new FunctionParser(Init("param1"));
            Formal parsedFormal = functionParser.ParseFormal();

            //Test identifier
            Assert.AreEqual("param1", parsedFormal.ToString());
        }

        /// <summary>
        ///A test for ParseEmptyFunctionDefinition
        ///</summary>
        [TestMethod()]
        public void ParseEmptyFunctionDefinitionTest()
        {
            FunctionParser functionParser = new FunctionParser(Init("home(param1, param2) \n end"));
            FunctionDefinition parsedFunctionDefinition = functionParser.ParseFunctionDefinition();

            //Test FunctionDefinition
            Assert.AreEqual("home", parsedFunctionDefinition.GetIdentifier());
            Assert.AreEqual(2, parsedFunctionDefinition.GetFormals().Count);

            //Check formals
            Formal[] formalArray = parsedFunctionDefinition.GetFormals().ToArray();
            Assert.AreEqual("param1", formalArray[0].ToString());
            Assert.AreEqual("param2", formalArray[1].ToString());
        }



        /// <summary>
        ///A test for ParseFormals
        ///</summary>
        [TestMethod()]
        public void ParseFormalsTest()
        {
            FunctionParser functionParser = new FunctionParser(Init("(param1, param2)"));
            FunctionDefinition functionDefinition = new FunctionDefinition();
            functionParser.ParseFormals(functionDefinition);

            //Test formals
            Assert.AreEqual(2, functionDefinition.GetFormals().Count);
            Formal[] formalArray = functionDefinition.GetFormals().ToArray();

            Assert.AreEqual("param1", formalArray[0].ToString());
            Assert.AreEqual("param2", formalArray[1].ToString());
        }
    }
}
