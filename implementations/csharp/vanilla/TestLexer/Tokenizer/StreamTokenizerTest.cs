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
                    case 1:
                        Assert.AreEqual("module", Tokenizer.GetTextValue());
                        break;
                    case 2:
                        Assert.AreEqual("test", Tokenizer.GetTextValue());
                        break;
                    case 3:
                        Assert.AreEqual("site", Tokenizer.GetTextValue());
                        break;
                    case 4:
                        Assert.AreEqual("site", Tokenizer.GetTextValue());
                        break;

                }
                position++;
                current = Tokenizer.NextToken();
            }
        }
        
    }
}
