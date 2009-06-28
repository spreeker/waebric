using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Lexer.Tokenizer;
using Parser.Ast.Functions;
using Parser.Ast.Statements;

namespace Parser
{
    /// <summary>
    /// Parser for functions
    /// </summary>
    public class FunctionParser : AbstractParser
    {
        #region Private Members

        private StatementParser statementParser;

        #endregion

        #region Public Methods

        public FunctionParser(TokenIterator tokenStream) : base(tokenStream)
        {
            //Create subparser
            statementParser = new StatementParser(tokenStream);
        }

        /// <summary>
        /// Parser for FunctionDefinition
        /// </summary>
        /// <returns>Parsed FunctionDefinition</returns>
        public FunctionDefinition ParseFunctionDefinition()
        {
            FunctionDefinition functionDefinition = new FunctionDefinition();

            //Parse FunctionId
            CurrentToken = TokenStream.NextToken();
            functionDefinition.SetIdentifier(CurrentToken.GetValue().ToString());

            //Parse Formals
            ParseFormals(functionDefinition);
           
            //Parse Statements
            while (TokenStream.HasNext() && TokenStream.Peek(1).GetValue().ToString() != "end")
            {
                functionDefinition.AddStatement(statementParser.ParseStatement());
            }

            //Skip end token
            NextToken("end", "def function() statements end", "end");

            return functionDefinition;
        }

        /// <summary>
        /// Parser for Formals
        /// </summary>
        /// <param name="functionDefinition">functionDefinition to add formals to</param>
        /// <returns>Parsed Formals</returns>
        public void ParseFormals(FunctionDefinition functionDefinition)
        {
            if (TokenStream.Peek(1).GetValue().ToString() != "(")
            {
                return; //No formals, so return empty formals
            }

            //Skip ( token
            NextToken("(","(formal1, formal2)",'(');

            while (TokenStream.HasNext())
            {   //Parse formals
                if (TokenStream.Peek(1).GetValue().ToString() == ")")
                {
                    break; //End of formals
                }
                else if(TokenStream.Peek(1).GetValue().ToString() == ",")
                {
                    //Skip , token
                    NextToken(",", "(formal1, formal2)", ',');
                }
                functionDefinition.AddFormal(ParseFormal());
            }

            //Skip ) token
            NextToken(")", "(formal1, formal2)", ')');
        }

        /// <summary>
        /// Parser for Formal
        /// </summary>
        /// <returns>Parsed Formal</returns>
        public Formal ParseFormal()
        {
            Formal formal = new Formal();

            CurrentToken = TokenStream.NextToken();
            formal.SetIdentifier(CurrentToken.GetValue().ToString());

            return formal;
        }

        #endregion
    }
}
