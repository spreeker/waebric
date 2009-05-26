using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using Lexer;
using NUnit.Framework;
using Lexer.Tokenizer;

namespace Waebric
{

    /// <summary>
    /// This class is used for testing the Lexer
    /// </summary>
    [TestFixture]
    public class TestLexer
    {
        private const string TEST_PATH = "../../test/helloworld.wae";

        private WaebricLexer Lexer;
        private StreamReader Stream;


        /// <summary>
        /// Useless method for starting point of project
        /// Should being fixed!
        /// </summary>
        /// <param name="args"></param>
        static void Main(string[] args)
        {
        /*    StreamReader inputStream = new StreamReader(TEST_PATH);
            WaebricLexer lexer = new WaebricLexer(inputStream);
            System.Console.WriteLine("Lexer Test Tool");
            lexer.LexicalizeStream();
            //while (lexer.Scan() != "") ;
            
            //System.Console.WriteLine(inputStream.ReadLine());
            System.Console.Read(); // temporary to read console output
            // lexer.Scanner();*/
        }


        /// <summary>
        /// Initialize tests
        /// </summary>
        [SetUp]
        public void InitTests()
        {
            Stream = new StreamReader(TEST_PATH);
            Lexer = new WaebricLexer(Stream);
        }

        /// <summary>
        /// Test to test if lexer is capable to load
        /// an stream and 
        /// </summary>
        [Test]
        public void TestLexerStream()
        {
            //Test that there is no token iterator without lexilizing a stream
            TokenIterator tokenIterator = Lexer.GetTokenIterator();
            Assert.IsNull(tokenIterator);
            
            //Test that there is an iterator when the stream has been tokenized
            Lexer.LexicalizeStream();
            tokenIterator = null;
            tokenIterator = Lexer.GetTokenIterator();
            Assert.IsNotNull(tokenIterator);
        }


    }
}
