using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace Lexer
{
    class TestLexer
    {
        public const string TEST_PATH = "../../test/helloworld.wae";

        /**
         * Method to test Lexer
         */
        static void Main(string[] args)
        {
            StreamReader inputStream = new StreamReader(TEST_PATH);


            System.Console.WriteLine("Lexer Test Tool");
        }


    }
}
