using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;
using System.IO;

namespace Lexer.Tokenizer
{
    /// <summary>
    /// Class tot test StreamTokenizer
    /// </summary>
    [TestFixture]
    public class TestStreamTokenizer
    {
        private StreamTokenizer Tokenizer;
        private StringReader Reader;

        [Test]
        public void TestWord()
        {
            String testString = "test";
            Reader = new StringReader(testString);
            Tokenizer = new StreamTokenizer(Reader);
            int type = Tokenizer.NextToken();

            //Test type and token value for correctness
            Assert.IsTrue(StreamTokenizer.WORD.Equals(type));
            Assert.IsTrue(testString.Equals(Tokenizer.GetTextValue()));
        }

        [Test]
        public void TestNumeric()
        {
            double number = 10.0;
            Reader = new StringReader(number.ToString());
            Tokenizer = new StreamTokenizer(Reader);
            int type = Tokenizer.NextToken();

            Assert.IsTrue(StreamTokenizer.NUMBER.Equals(type));
            Assert.IsTrue(number.Equals(Tokenizer.GetNumericValue()));
        }

        [Test]
        public void TestSymbol()
        {
            String testString = "}";
            Reader = new StringReader(testString);
            Tokenizer = new StreamTokenizer(Reader);
            int type = Tokenizer.NextToken();
            //get char of testString
            char[] charArray = testString.ToCharArray();

            Assert.IsTrue(charArray[0].Equals((char)type));
        }

        [Test]
        public void TestWhitespace()
        {
            String testString = "test test2 test3 test4";
            Reader = new StringReader(testString);
            Tokenizer = new StreamTokenizer(Reader);

            int type = Tokenizer.NextToken();
            String[] tokens = testString.Split(' ');
            int nr = 0;
            while (type != StreamTokenizer.EOF)
            {
                //Check if the string is tokenized on a right way
                Assert.IsTrue(StreamTokenizer.WORD.Equals(type));
                Assert.IsTrue(tokens[nr].Equals(Tokenizer.GetTextValue()));
                nr++;
                type = Tokenizer.NextToken();
            }
        }

        [Test]
        public void TestQuote()
        {
            String testString = "\"quoted text\"";
            Reader = new StringReader(testString);
            Tokenizer = new StreamTokenizer(Reader);

            int type = Tokenizer.NextToken();
            String value = Tokenizer.GetTextValue();

            Assert.IsTrue(type == '\"');
            Assert.IsTrue(testString.Equals(value));
            
        }

        [Test]
        public void TestScannedLines()
        {
            String testString = "\n\n\n\n";
            Reader = new StringReader(testString);
            Tokenizer = new StreamTokenizer(Reader);
            int type = Tokenizer.NextToken();
            while (type != StreamTokenizer.EOF)
            {
                type = Tokenizer.NextToken();
            }

            int lines = Tokenizer.GetScannedLines();
            Assert.IsTrue(lines == 5);
        }
       
        [TearDown]
        public void Cleanup()
        {
            Reader = null;
            Tokenizer = null;
        }
    }
}
