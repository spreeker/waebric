using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Parser.Ast.Expressions;
using Lexer.Tokenizer;

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

        public ExpressionParser(TokenIterator iterator, List<Exception> exceptionList)
            : base(iterator, exceptionList)
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

            //TODO: ADD HERE CASES WHERE AN FIELD OR CAT EXPRESSION IS THE CASE

            return expression;
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
        /// Parse an FieldExpression
        /// </summary>
        /// <returns>Parsed Fiel Expression</returns>
        public FieldExpression ParseFieldExpression()
        {
            return null;
        }

        /// <summary>
        /// Parse an CatenationExpression
        /// </summary>
        /// <returns>Parsed Catenation Expression</returns>
        public CatExpression ParseCatExpression()
        {
            return null;
        }

        /// <summary>
        /// Parse an ListExpression
        /// </summary>
        /// <returns>Parsed List Expression</returns>
        public ListExpression ParseListExpression()
        {
            return null;
        }

        /// <summary>
        /// Parse an RecordExpression
        /// </summary>
        /// <returns>Parsed Record Expression</returns>
        public RecordExpression ParseRecordExpression()
        {
            return null;
        }

        /// <summary>
        /// Parse an KeyValuePair
        /// </summary>
        /// <returns>Parsed KeyValuePair</returns>
        public KeyValuePair ParseKeyValuePair()
        {
            return null;
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
