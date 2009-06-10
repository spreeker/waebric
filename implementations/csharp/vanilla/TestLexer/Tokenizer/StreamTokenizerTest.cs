using Microsoft.VisualStudio.TestTools.UnitTesting;
using Lexer.Tokenizer;
using System.IO;
using System;

namespace TestLexer
{
    
    
    /// <summary>
    ///This is a test class for StreamTokenizerTest and is intended
    ///to contain all StreamTokenizerTest Unit Tests
    ///</summary>
    [TestClass()]
    public class StreamTokenizerTest
    {

        private StreamTokenizer Tokenizer;
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

        #endregion

        /// <summary>
        /// This test tests an specific part of waebric
        /// with all types of tokens
        /// </summary>
        [TestMethod]
        public void TestWaebricInput()
        {
            //Set up tokenizer and input for tokenizer
            Tokenizer = new StreamTokenizer(new StringReader("module test\n\nsite site/index.html : home()\nend"));

            //Tokenize stream and do some test with it
            int current = Tokenizer.NextToken();
            int position = 1;
            while(current != StreamTokenizer.EOF)
            {
                if (current == StreamTokenizer.LAYOUT)
                {
                    current = Tokenizer.NextToken();
                    continue; //ignore layout
                }
                switch (position)
                {
                    case 1: //module
                        Assert.AreEqual("module", Tokenizer.GetTextValue());
                        break;
                    case 2: //test
                        Assert.AreEqual("test", Tokenizer.GetTextValue());
                        break;
                    case 3: //site
                        Assert.AreEqual("site", Tokenizer.GetTextValue());
                        break;
                    case 4: //site
                        Assert.AreEqual("site", Tokenizer.GetTextValue());
                        break;
                    case 5: // /
                        Assert.AreEqual('/', Tokenizer.GetCharacterValue());
                        break;
                    case 6: //index
                        Assert.AreEqual("index", Tokenizer.GetTextValue());
                        break;
                    case 7: //.
                        Assert.AreEqual('.', Tokenizer.GetCharacterValue());
                        break;
                    case 8: //html
                        Assert.AreEqual("html", Tokenizer.GetTextValue());
                        break;
                    case 9: //:
                        Assert.AreEqual(':', Tokenizer.GetCharacterValue());
                        break;
                    case 10: //home
                        Assert.AreEqual("home", Tokenizer.GetTextValue());
                        break;
                    case 11: //(
                        Assert.AreEqual('(', Tokenizer.GetCharacterValue());
                        break;
                    case 12: //)
                        Assert.AreEqual(')', Tokenizer.GetCharacterValue());
                        break;
                    case 13: //end
                        Assert.AreEqual("end", Tokenizer.GetTextValue());
                        break;
                }
                position++;
                current = Tokenizer.NextToken();
            }
        }
        
    }
}
