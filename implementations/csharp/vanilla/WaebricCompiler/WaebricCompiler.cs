using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Lexer;
using System.IO;
using Lexer.Tokenizer;
using Parser;
using Parser.Ast;
using Checker;

namespace Compiler
{
    /// <summary>
    /// Class which contains the main program
    /// </summary>
    public class WaebricCompiler
    {
        #region Private Members

        private static String Path; //Path of file to compile

        #endregion

        public static void Main(string[] args)
        {
            Console.WriteLine("Waebric Compiler/Interpreter v1.0");
            Console.WriteLine("---------------------------------");
            

            if (args.Length == 1)
            {   //There is one file specified. 
                Path = args[0];
            }
            else
            {
                Console.WriteLine("WeabricCompiler: no input file specified.");
                Console.Read(); //Testing purposes only
                return;
            }

            //Let's lexicalize the file
            StreamReader sourceStream = new StreamReader(Path);
            WaebricLexer lexer = new WaebricLexer(sourceStream);

            lexer.LexicalizeStream();
            TokenIterator tokens = lexer.GetTokenIterator();

            if (tokens.GetSize() == 0)
            {   //Not tokens parsed
                Console.WriteLine("WaebricCompiler: Empty file or comments only.");
                return; //Nothing to compile so end program
            }

            //Lets parse the file
            WaebricParser parser = new WaebricParser(tokens);
            parser.Parse();

            SyntaxTree parsedTree = parser.GetTree();

            //Lets check the tree
            WaebricChecker checker = new WaebricChecker();
        }
    }
}
