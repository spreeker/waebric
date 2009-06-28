using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Lexer.Tokenizer;
using Parser.Ast.Statements;
using Parser.Ast.Predicates;
using Parser.Exceptions;

namespace Parser
{
    /// <summary>
    /// Parser for statements
    /// </summary>
    public class StatementParser : AbstractParser
    {
        #region Private Members

        private PredicateParser predicateParser;
        private ExpressionParser expressionParser;
        private EmbeddingParser embeddingParser;

        #endregion

        #region Public Methods


        public StatementParser(TokenIterator iterator)
            : base(iterator)
        {
            //Create subparsers
            predicateParser = new PredicateParser(iterator);
            expressionParser = new ExpressionParser(iterator);
            embeddingParser = new EmbeddingParser(iterator);
        }

        /// <summary>
        /// Parser for Statements
        /// </summary>
        /// <returns>Parsed Statements</returns>
        public Statement ParseStatement()
        {
            if (TokenStream.HasNext())
            {
                //Determine type of statement
                if (TokenStream.Peek(1).GetValue().ToString() == "if")
                {   //If (ThenElse) statement
                    return ParseIfStatement();
                }
                else if (TokenStream.Peek(1).GetValue().ToString() == "each")
                {   //Each statement
                    return ParseEachStatement();
                }
                else if (TokenStream.Peek(1).GetValue().ToString() == "let")
                {   //Let statement
                    return ParseLetStatement();
                }
                else if (TokenStream.Peek(1).GetValue().ToString() == "{")
                {   //Block statement
                    return ParseBlockStatement();
                }
                else if (TokenStream.Peek(1).GetValue().ToString() == "comment")
                {   //Comment statement
                    return ParseCommentStatement();
                }
                else if (TokenStream.Peek(1).GetValue().ToString() == "echo")
                {   //Echo statement
                    return ParseEchoStatement();
                }
                else if (TokenStream.Peek(1).GetValue().ToString() == "cdata")
                {   //Cdata statement
                    return ParseCdataStatement();
                }
                else if (TokenStream.Peek(1).GetValue().ToString() == "yield")
                {   //Yield statement
                    return ParseYieldStatement();
                }
                else
                {   //Unexpected token, throw exception
                    throw new UnexpectedToken("Statement expected, but found:", TokenStream.Peek(1).GetValue().ToString(), TokenStream.Peek(1).GetLine());
                }
            }

            return null;
        }

        /// <summary>
        /// Parser for If(Else)Statement
        /// </summary>
        /// <returns>Parsed If(Else)Statement</returns>
        public Statement ParseIfStatement()
        {
            Statement statement = null;

            //Skip if keyword
            NextToken("if", "if ( Predicate ) Statement else Statement", "if");

            //Skip ( token
            NextToken("(", "if ( Predicate ) Statement else Statement", '(');

            //Parse predicate
            Predicate predicate = predicateParser.ParsePredicate();

            //Skip ) token
            NextToken(")", "if ( Predicate ) Statement else Statement", ')');

            //Parse if part of statement
            Statement ifStatementPart = ParseStatement();

            if (TokenStream.HasNext() && TokenStream.Peek(1).GetValue().ToString() == "else")
            {   //Else part detected
                //Skip else token
                NextToken("else", "if ( Predicate ) Statement else Statement", "else");

                //Parse else statement part
                Statement elseStatementPart = ParseStatement();


                IfElseStatement ifElseStatement = new IfElseStatement();
                ifElseStatement.SetPredicate(predicate);
                ifElseStatement.SetTrueStatement(ifStatementPart);
                ifElseStatement.SetFalseStatement(elseStatementPart);
                statement = ifElseStatement;
            }
            else
            {   //If statement
                IfStatement ifStatement = new IfStatement();
                ifStatement.SetPredicate(predicate);
                ifStatement.SetTrueStatement(ifStatementPart);
                statement = ifStatement;
            }

            return statement;
        }

        /// <summary>
        /// Parser for EachStatement
        /// </summary>
        /// <returns>Parsed EachStatement</returns>
        public EachStatement ParseEachStatement()
        {
            EachStatement eachStatement = new EachStatement();

            //Skip each token
            NextToken("each", "each (identifier : expression) Statement", "each");

            //Skip ( token
            NextToken("(", "each (identifier : expression) Statement", '(');

            //Parse identifier
            CurrentToken = TokenStream.NextToken();
            eachStatement.SetIdentifier(CurrentToken.GetValue().ToString());

            //Skip : token
            NextToken(":", "each (identifier : expression) Statement", ':');

            //Parse expression
            eachStatement.SetExpression(expressionParser.ParseExpression());

            //Skip ) token
            NextToken(")", "each (identifier : expression) Statement", ')');

            //Parse statement
            eachStatement.SetStatement(ParseStatement());

            return eachStatement;
        }

        /// <summary>
        /// Parser for LetStatement
        /// </summary>
        /// <returns>Parsed LetStatement</returns>
        public LetStatement ParseLetStatement()
        {
            LetStatement letStatement = new LetStatement();

            //Skip let token
            NextToken("let", "let assignment+ in statement* end", "let");
            
            //Parse arguments
            while(TokenStream.HasNext())
            {
                if(TokenStream.Peek(1).GetValue().ToString() == "in")
                {
                    break; //no more assignments left
                }
                letStatement.AddAssignment(ParseAssignment());
            }
            if (letStatement.GetAssignments().Count == 0)
            {   //No assignments is not allowed
                //Todo specify error message
                //throw new UnexpectedToken();
            }

            //Skip in token
            NextToken("in", "let assignment+ in statement* end", "in");

            //Parse statements
            while (TokenStream.HasNext())
            {
                if (TokenStream.Peek(1).GetValue().ToString() == "end")
                {   //No more statements left
                    break; 
                }
                letStatement.AddStatement(ParseStatement());
            }

            //Skip end token
            NextToken("end", "let assignment+ in statement* end", "end");

            return letStatement;
        }

        /// <summary>
        /// Parser for BlockStatement
        /// </summary>
        /// <returns>Parsed BlockStatement</returns>
        public BlockStatement ParseBlockStatement()
        {
            BlockStatement blockStatement = new BlockStatement();

            //Skip { token
            NextToken("{", "{ statements* }", '{');

            //Parse statements
            while (TokenStream.HasNext())
            {
                if (TokenStream.Peek(1).GetValue().ToString() == "}")
                {   //End of blockstatement
                    break;
                }

                blockStatement.AddStatement(ParseStatement());
            }

            //Skip } token
            NextToken("}", "{ statements* }", '}');

            return blockStatement;
        }

        /// <summary>
        /// Parser for CommentStatement
        /// </summary>
        /// <returns>Parsed CommentStatement</returns>
        public CommentStatement ParseCommentStatement()
        {
            CommentStatement commentStatement = new CommentStatement();

            //Skip comment token
            NextToken("comment", "comment thisisacomment;", "comment");

            //Parse comment
            CurrentToken = TokenStream.NextToken();
            commentStatement.SetCommentString(CurrentToken.ToString());

            //Skip ; token
            NextToken(";", "comment thisisacomment;", ';');
            
            return commentStatement;
        }

        /// <summary>
        /// Parser for EchoStatement
        /// </summary>
        /// <returns>Parsed EchoStatement</returns>
        public EchoStatement ParseEchoStatement()
        {
            //Skip echo
            NextToken("echo", "echo Expression/Embedding ;", "echo");

            //Determine echo type
            if (TokenStream.HasNext() && TokenStream.Peek(1).GetType() == TokenType.EMBEDDING)
            {   //EchoEmbeddingStatement
                EchoEmbeddingStatement echoEmbedding = new EchoEmbeddingStatement();
                
                //Parse embedding
                echoEmbedding.SetEmbedding(embeddingParser.ParseEmbedding());

                //Skip ; token
                NextToken(";", "echo Expression/Embedding ;", ';');

                return echoEmbedding;
            }
            else
            {   //EchoExpressionStatement
                EchoExpressionStatement echoExpression = new EchoExpressionStatement();

                //Parse expression
                echoExpression.SetExpression(expressionParser.ParseExpression());

                //Skip ; token
                NextToken(";", "echo Expression/Embedding ;", ';');

                return echoExpression;
            }

        }

        /// <summary>
        /// Parser for CdataStatement
        /// </summary>
        /// <returns>Parsed CdataStatement</returns>
        public CdataStatement ParseCdataStatement()
        {
            CdataStatement cdataStatement = new CdataStatement();

            //Skip cdata token
            NextToken("cdata", "cdata expression;", "cdata");

            //Parse expression
            cdataStatement.SetExpression(expressionParser.ParseExpression());

            //Skip ; token
            NextToken(";", "cdata expression;", ';');

            return cdataStatement;
        }

        /// <summary>
        /// Parser for YieldStatement
        /// </summary>
        /// <returns>Parsed YieldStatement</returns>
        public YieldStatement ParseYieldStatement()
        {
            //Skip yield token
            NextToken("yield", "yield;", "yield");

            //Skip ; token
            NextToken(";", "yield;", ';');

            return new YieldStatement();
        }

        /// <summary>
        /// Parser for Assignment
        /// </summary>
        /// <returns>ParsedAssignment</returns>
        public Assignment ParseAssignment()
        {
            Assignment assignment = new Assignment();

            //Parse identifier
            CurrentToken = TokenStream.NextToken();
            assignment.SetIdentifier(CurrentToken.GetValue().ToString());

            //Skip = token
            NextToken("=", "identifier = expression;", '=');

            //Parse expression
            assignment.SetExpression(expressionParser.ParseExpression());

            //Skip ; token
            NextToken(";", "identifier = expression;", ';');

            return assignment;
        }


        #endregion
    }
}
