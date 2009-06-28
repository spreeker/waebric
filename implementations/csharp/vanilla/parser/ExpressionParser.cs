using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Parser.Ast.Expressions;
using Lexer.Tokenizer;
using Parser.Exceptions;

namespace Parser
{
    /// <summary>
    /// Parser for Expressions
    /// </summary>
    public class ExpressionParser : AbstractParser
    {
        #region Private Members

        #endregion

        #region Public Members

        public ExpressionParser(TokenIterator iterator)
            : base(iterator)
        {
        }

        /// <summary>
        /// Parse an expression
        /// </summary>
        /// <returns>Parsed Expression</returns>
        public Expression ParseExpression()
        {
            Expression expression = null;
            
            //Determine subtype and then parse that type
            if (TokenStream.Peek(1).GetType() == TokenType.TEXT)
            {   //Text expression
                expression = ParseTextExpression();
            }
            else if (TokenStream.Peek(1).GetType() == TokenType.WAEBRICSYMBOL)
            {   //Symbol expression
                expression = ParseSymExpression();               
            }
            else if (TokenStream.Peek(1).GetType() == TokenType.NUMBER)
            {   //Numeric expression
                expression = ParseNumExpression();
            }
            else if (TokenStream.Peek(1).GetType() == TokenType.IDENTIFIER)
            {   //Variable expression
                expression = ParseVarExpression();
            }
            else if (TokenStream.Peek(1).GetValue().ToString() == "[")
            {   //List expression
                expression = ParseListExpression();
            }
            else if (TokenStream.Peek(1).GetValue().ToString() == "{")
            {   //Record expression
                expression = ParseRecordExpression();
            }
            
            //Check if it is maybe an catenation or field
            if(TokenStream.HasNext(2) && TokenStream.Peek(1).GetValue().ToString() == "." && TokenStream.Peek(2).GetType()== TokenType.IDENTIFIER && 
                TokenStream.Peek(3).GetValue().ToString() != "?")
            {   //Field
                return ParseFieldExpression(expression);
            }
            else if (TokenStream.HasNext() && TokenStream.Peek(1).GetValue().ToString() == "+")
            {   //Catenation
                return ParseCatExpression(expression);
            }

            if (expression == null)
            {   //No expression found, raise exception
                throw new UnexpectedToken("Expression expected, but found:", CurrentToken.GetValue().ToString(), CurrentToken.GetLine());
            }

            return expression;
        }

        /// <summary>
        /// Parse an FieldExpression
        /// </summary>
        /// <param name="expression">Expression which is already parsed</param>
        /// <returns></returns>
        public Expression ParseFieldExpression(Expression expression)
        {
            FieldExpression fieldExpression = new FieldExpression();

            //Add already parsed expression to field
            fieldExpression.SetExpression(expression);

            //Skip . token
            NextToken(".", "expression.identifier", '.');

            //Parse identifier
            CurrentToken = TokenStream.NextToken();
            fieldExpression.SetIdentifier(CurrentToken.GetValue().ToString());

            return fieldExpression;
        }

        /// <summary>
        /// Parse an CatExpression
        /// </summary>
        /// <param name="expression">Expression which is already parsed</param>
        /// <returns></returns>
        public Expression ParseCatExpression(Expression expression)
        {
            CatExpression catExpression = new CatExpression();

            //Left part of catenation expression
            catExpression.SetLeftExpression(expression);

            //Skip + token
            NextToken("+", "expression + expression", '+');

            //Parse right part of token
            catExpression.SetRightExpression(ParseExpression());

            return catExpression;
        }

        /// <summary>
        /// Parse an VariableExpression
        /// </summary>
        /// <returns>Parsed VariableExpression</returns>
        public VarExpression ParseVarExpression()
        {
            VarExpression varExpression = new VarExpression();

            CurrentToken = TokenStream.NextToken();
            varExpression.SetVariableIdentifier(CurrentToken.GetValue().ToString());

            return varExpression;
        }

        /// <summary>
        /// Parse an SymbolExpression
        /// </summary>
        /// <returns>Parsed SymbolExpression</returns>
        public SymExpression ParseSymExpression()
        {
            SymExpression symExpression = new SymExpression();

            CurrentToken = TokenStream.NextToken();
            symExpression.SetSym(CurrentToken.GetValue().ToString());

            return symExpression;
        }

        /// <summary>
        /// Parse an NumericExpression
        /// </summary>
        /// <returns>Parsed Numeric Expression</returns>
        public NumExpression ParseNumExpression()
        {
            NumExpression numExpression = new NumExpression();

            CurrentToken = TokenStream.NextToken();
            numExpression.SetNum(ObjectToInt(CurrentToken.GetValue()));

            return numExpression;
        }

        /// <summary>
        /// Parse an ListExpression
        /// </summary>
        /// <returns>Parsed List Expression</returns>
        public ListExpression ParseListExpression()
        {
            ListExpression listExpression = new ListExpression();

            //Skip [ token
            NextToken("[", "[ expression, expression ]", '[');

            while (TokenStream.HasNext())
            {   //Scan for expressions
                if (TokenStream.Peek(1).GetValue().ToString() == "]")
                {
                    break; //empty list found
                }

                listExpression.AddExpression(ParseExpression());

                if (TokenStream.HasNext() && TokenStream.Peek(1).GetValue().ToString() == ",")
                {   //separator
                    NextToken(",", "[ expression, expression ]", ',');
                }
            }

            //Skip ] token
            NextToken("]", "[ expression, expression ]", ']');

            return listExpression;
        }

        /// <summary>
        /// Parse an RecordExpression
        /// </summary>
        /// <returns>Parsed Record Expression</returns>
        public RecordExpression ParseRecordExpression()
        {
            RecordExpression recordExpression = new RecordExpression();

            //Skip { token
            NextToken("{", "{key:value, key:value}", '{');

            while (TokenStream.HasNext())
            {   //Scan for key value pairs
                if(TokenStream.Peek(1).GetValue().ToString() == "}")
                {
                    break; //} marks end of stream
                }

                recordExpression.AddKeyValuePair(ParseKeyValuePair());

                if (TokenStream.HasNext() && TokenStream.Peek(1).GetValue().ToString() == ",")
                {
                    //Skip , token
                    NextToken(",", "{key:value, key:value}", ',');
                }
            }

            //Skip } token
            NextToken("}", "{key:value, key:value}", '}');

            return recordExpression;
        }

        /// <summary>
        /// Parse an KeyValuePair
        /// </summary>
        /// <returns>Parsed KeyValuePair</returns>
        public KeyValuePair ParseKeyValuePair()
        {
            KeyValuePair keyValuePair = new KeyValuePair();

            //Get key
            CurrentToken = TokenStream.NextToken();
            keyValuePair.SetKey(CurrentToken.GetValue().ToString());

            //Skip :
            NextToken(":", "key : value", ':');

            //Get value
            keyValuePair.SetValue(ParseExpression());

            return keyValuePair;
        }

        /// <summary>
        /// Parse an TextExpression
        /// </summary>
        /// <returns>Parsed TextExpression</returns>
        public TextExpression ParseTextExpression()
        {
            TextExpression textExpression = new TextExpression();

            CurrentToken = TokenStream.NextToken();
            textExpression.SetText(CurrentToken.GetValue().ToString());

            return textExpression;
        }

        #endregion

    }
}
