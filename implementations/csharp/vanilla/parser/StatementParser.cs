using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Lexer.Tokenizer;
using Parser.Ast.Statements;
using Parser.Ast.Predicates;
using Parser.Exceptions;
using Parser.Ast.Markup;
using Parser.Ast;

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
        private MarkupParser markupParser;

        #endregion

        #region Public Methods


        public StatementParser(TokenIterator iterator)
            : base(iterator)
        {
            //Create subparsers
            predicateParser = new PredicateParser(iterator);
            expressionParser = new ExpressionParser(iterator);
            embeddingParser = new EmbeddingParser(iterator);
            markupParser = new MarkupParser(iterator);
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
                else if (TokenStream.Peek(1).GetType() == TokenType.IDENTIFIER)
                {   //Markup statements, starts always with an identifier
                    return ParseMarkupStatement();
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
                throw new UnexpectedToken("LetStatement doesn't contain assignments.", "Expected at least one assignment ", CurrentToken.GetLine());
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
            commentStatement.SetCommentString(CurrentToken.GetValue().ToString());

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
        /// <returns>Parsed Assignment</returns>
        public Assignment ParseAssignment()
        {
            //Determine type
            if (TokenStream.HasNext(2) && TokenStream.Peek(2).GetValue().ToString() == "(")
            {   //FuncBindAssignment
                return ParseFuncBindAssignment();
            }
            else if (TokenStream.HasNext(2) && TokenStream.Peek(2).GetValue().ToString() == "=")
            {   //VarBindAssignment
                return ParseVarBindAssignment();
            }
            else
            {
                throw new UnexpectedToken("Assignment expected, found:", CurrentToken.GetValue().ToString(), CurrentToken.GetLine());
            }
        }

        /// <summary>
        /// Parser for FuncBindAssignment
        /// </summary>
        /// <returns>Parsed FuncBindAssignment</returns>
        public FuncBindAssignment ParseFuncBindAssignment()
        {
            FuncBindAssignment funcBindAssignment = new FuncBindAssignment();

            //Parse identifier
            CurrentToken = TokenStream.NextToken();
            funcBindAssignment.SetIdentifier(CurrentToken.GetValue().ToString());

            //Skip ( token
            NextToken("(", "identifier(identifier1, identifier2) = statement;", '(');

            //Parse identifiers
            while (TokenStream.HasNext())
            {
                if (TokenStream.HasNext() && TokenStream.Peek(1).GetValue().ToString() == ")")
                {   //No more identifiers
                    break;
                }
                else if (TokenStream.Peek(1).GetValue().ToString() == ",")
                {
                    //Skip , token
                    NextToken(",", "(identifier1, identifier2)", ',');
                }
                CurrentToken = TokenStream.NextToken();
                funcBindAssignment.AddIdentifier(CurrentToken.GetValue().ToString());
                
            }

            //Skip ) token
            NextToken(")", "identifier(identifier1, identifier2) = statement;", ')');

            //Skip = token
            NextToken("=", "identifier(identifier1, identifier2) = statement;", '=');

            //Parse statement
            funcBindAssignment.SetStatement(ParseStatement());

            return funcBindAssignment;
        }

        /// <summary>
        /// Parser for VarBindAssignment
        /// </summary>
        /// <returns>Parsed VarBindAssignment</returns>
        public VarBindAssignment ParseVarBindAssignment()
        {
            VarBindAssignment varBindAssignment = new VarBindAssignment();

            //Parse identifier
            CurrentToken = TokenStream.NextToken();
            varBindAssignment.SetIdentifier(CurrentToken.GetValue().ToString());

            //Skip = token
            NextToken("=", "identifier = expression;", '=');

            //Parse expression
            varBindAssignment.SetExpression(expressionParser.ParseExpression());

            //Skip ; token
            NextToken(";", "identifier = expression;", ';');

            return varBindAssignment;
        }

        /// <summary>
        /// Parser for MarkupStatement types
        /// </summary>
        /// <returns>Parsed Statement</returns>
        public Statement ParseMarkupStatement()
        {
            //Start parsing first markup part
            Markup firstMarkup = markupParser.ParseMarkup();

            if (TokenStream.HasNext() && TokenStream.Peek(1).GetType() == TokenType.SYMBOL && TokenStream.Peek(1).GetValue().ToString() == ";")
            {   //Just a single markup statement
                MarkupStatement markupStatement = new MarkupStatement();
                markupStatement.SetMarkup(firstMarkup);

                //Skip ; token
                NextToken(";", "markup;", ';');

                return markupStatement;
            }
            else
            {   
                //Get other markups
                NodeList markups = new NodeList();

                markups.Add(firstMarkup);

                while(DetectNextIsMarkup())
                {
                    markups.Add(markupParser.ParseMarkup());
                }

                //Determine statement type
                if (TokenStream.HasNext())
                {
                    if (TokenStream.Peek(1).GetType() == TokenType.EMBEDDING)
                    {   //Markup Embedding Statement
                        MarkupEmbeddingStatement markupEmbedding = new MarkupEmbeddingStatement();
                        markupEmbedding.SetMarkups(markups);
                        markupEmbedding.SetEmbedding(embeddingParser.ParseEmbedding());
                        
                        //Skip ; token
                        NextToken(";", "Markup+ Embedding;", ';');

                        return markupEmbedding;
                    }
                    else if(TokenStream.Peek(1).GetValue().ToString() == ";")
                    {   //MarkupStatement
                        MarkupMarkupStatement markupStatement = new MarkupMarkupStatement();

                        //Get last parsed markup from list and remove it from list
                        Markup last = (Markup) markups.Get(markups.Count - 1);
                        markups.Remove(markups.Count-1);
                        
                        markupStatement.SetMarkup(last);
                        markupStatement.SetMarkups(markups);

                        //Skip ; token
                        NextToken(";", "Markup+ Markup;", ';');
                        
                        return markupStatement;
                    }
                    else if (IsMarkupStatStatement())
                    {   //MarkupStatStatement
                        MarkupStatStatement markupStatStatement = new MarkupStatStatement();

                        markupStatStatement.SetMarkups(markups);
                        markupStatStatement.SetStatement(ParseStatement());

                        return markupStatStatement;
                    }
                    else if (IsMarkupExpressionStatement())
                    {   //MarkupExpressionStatement
                        MarkupExpressionStatement markupExpressionStatement = new MarkupExpressionStatement();

                        //Parse MarkupExpressionStatement
                        markupExpressionStatement.SetMarkups(markups);
                        markupExpressionStatement.SetExpression(expressionParser.ParseExpression());

                        //Skip ; token
                        NextToken(";", "Markup+ Expression;", ';');

                        return markupExpressionStatement;
                    }
                    else
                    {   //Unexpected token
                        throw new UnexpectedToken("Markup Statement expected, but found:", CurrentToken.GetValue().ToString(), CurrentToken.GetLine());
                    }
                }
                else
                {
                    throw new UnexpectedToken("Expected MarkupStatement type, but found:", CurrentToken.GetValue().ToString(), CurrentToken.GetLine());
                }
            }
        }

        #endregion

        #region Private Members

        /// <summary>
        /// Detects if next tokens are markup (in a Markup+ form)
        /// </summary>
        /// <returns>True if markup otherwise false</returns>
        private bool DetectNextIsMarkup()
        {
            int j = 1; 
            if (TokenStream.HasNext(1) && TokenStream.Peek(1).GetType() == TokenType.IDENTIFIER)
            {
                //Determine if arguments exists
                while (TokenStream.HasNext(j + 1))
                {
                    if(IsNextMarkupAttribute(j+1))
                    {   
                        //Attribute is max 2 tokens wide
                        if(TokenStream.HasNext(j + 2))
                        {
                            j+= 2;
                        }
                        else
                        {   //No markup;
                            return false;
                        }
                    }
                    else
                    {   //End of attributes
                        break;
                    }
                }

                //Determine if it is a call
                if(TokenStream.HasNext(j + 1) && TokenStream.Peek(j + 1).GetType() == TokenType.SYMBOL && TokenStream.Peek(j + 1).GetValue().ToString() == "(")
                {   //Call markup
                    return true;
                }
                else if (TokenStream.HasNext(j + 1) && TokenStream.Peek(j + 1).GetType() == TokenType.SYMBOL && TokenStream.Peek(j + 1).GetValue().ToString() == ";")
                {   //No markup in a list
                    return false;
                }
                else
                {   //Everything else is just markup
                    return true;
                }
            }
            else
            {   //No identifier at start, so no markup
                return false; 
            }
        }

        private bool IsNextMarkupAttribute(int index)
        {
            if (TokenStream.Peek(index).GetType() != TokenType.SYMBOL)
            {
                return false;
            }
            String tokenText = TokenStream.Peek(index).GetValue().ToString();
            return tokenText == "#" || tokenText == "." || tokenText == "$" || tokenText == ":" || tokenText == "@" || tokenText == "%";
        }

        private bool IsMarkupStatStatement()
        {
            String value = TokenStream.Peek(1).GetValue().ToString();
            bool lookahead = TokenStream.HasNext(3);
            if (lookahead)
            {
                String second = TokenStream.Peek(2).GetValue().ToString();
                String look = TokenStream.Peek(3).GetValue().ToString();
                return value == "if" || value == "each" || value == "let" || (value == "{" && !(second == "}" && look == ";")) || value == "comment"
                                    || value == "echo" || value == "cdata" || value == "yield";
            }
            else
            {
                return value == "if" || value == "each" || value == "let" || value == "{" || value == "comment"
                    || value == "echo" || value == "cdata" || value == "yield";
            }
        }

        private bool IsMarkupExpressionStatement()
        {
            if(TokenStream.Peek(1).GetType() == TokenType.SYMBOL)
            {   //Expression characters are only [ and {
                return TokenStream.Peek(1).GetValue().ToString() == "[" || TokenStream.Peek(1).GetValue().ToString() == "{";
            }
            else
            {
                return TokenStream.Peek(1).GetType() != TokenType.KEYWORD;
            }
        }

        #endregion
    }
}
