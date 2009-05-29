using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using Lexer;
using Lexer.Tokenizer;

namespace Parser
{
    /// <summary>
    /// Temporary start of Parser program to test things out, etc
    /// </summary>
    public class Program
    {
        static void Main(string[] args)
        {
            System.Console.WriteLine("Waebric Parser V 0.1");

            //Make an lexer and analyze the stream
            StreamReader waebricStream = new StreamReader(args[0]);
            WaebricLexer lexer = new WaebricLexer(waebricStream);

            lexer.LexicalizeStream();
            TokenIterator tokens = lexer.GetTokenIterator();


        }
    }
}
