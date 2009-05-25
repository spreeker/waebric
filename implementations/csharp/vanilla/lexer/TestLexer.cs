using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using Lexer;

namespace Waebric
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
            WaebricLexer lexer = new WaebricLexer(inputStream);
            System.Console.WriteLine("Lexer Test Tool");
            lexer.LexicalizeStream();
            //while (lexer.Scan() != "") ;
            
            //System.Console.WriteLine(inputStream.ReadLine());
            System.Console.Read(); // temporary to read console output
            // lexer.Scanner();
        }


    }
}
