using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;

namespace Lexer.Tokenizer
{
    /// <summary>
    /// Class to test Token Class
    /// </summary>
    [TestFixture]
    public class TestToken
    {
        private Token tkn;

        [SetUp]
        public void Init()
        {
            tkn = new Token();
        }


        /// <summary>
        /// Test Get and SetType
        /// </summary>
        [Test]
        public void TestGetSetType()
        {
            tkn.SetType(TokenType.IDENTIFIER);
            Assert.IsTrue(TokenType.IDENTIFIER == tkn.GetType());
        }
        
        /// <summary>
        /// Test Get and SetLine
        /// </summary>
        [Test]
        public void TestGetSetLine()
        {
            tkn.SetLine(10);
            Assert.IsTrue(tkn.GetLine() == 10);
        }

        /// <summary>
        /// Test Get and SetValue
        /// </summary>
        [Test]
        public void TestGetSetValue()
        {
            String test = "test";
            tkn.SetValue(test);
            Assert.AreSame(test, tkn.GetValue());
        }

        [Test]
        public void TestConstructor()
        {
            String value = "test";
            int line = 3;
            TokenType type = TokenType.TEXT;
            Token t = new Token(value, type, line);
            
            Assert.AreEqual(line, t.GetLine());
            Assert.AreEqual(type, t.GetType());
            Assert.AreEqual(value, t.GetValue());
        }

        [TearDown]
        public void Destruct()
        {
            tkn = null;
        }
    }
}
