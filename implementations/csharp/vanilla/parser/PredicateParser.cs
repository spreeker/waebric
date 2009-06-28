using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Lexer.Tokenizer;
using Parser.Ast.Predicates;
using Parser.Ast.Expressions;
using Parser.Exceptions;
using Type = Parser.Ast.Predicates.Type;

namespace Parser
{
    /// <summary>
    /// Parser for predicates
    /// </summary>
    public class PredicateParser : AbstractParser
    {
        #region Private Members

        private ExpressionParser expressionParser;

        #endregion

        #region Public Methods

        public PredicateParser(TokenIterator iterator)
            : base(iterator)
        {
            //Create subparser
            expressionParser = new ExpressionParser(iterator);
        }

        /// <summary>
        /// Parser for Predicate
        /// </summary>
        /// <returns>Parsed Predicate</returns>
        public Predicate ParsePredicate()
        {
            Predicate predicate = null; //Empty predicate

            if (TokenStream.HasNext() && TokenStream.Peek(1).GetValue().ToString() == "!")
            {   //Not Predicate
                NotPredicate notPredicate = new NotPredicate();

                //Skip ! token
                NextToken("!", "! predicate", '!');

                //Parse predicate after !
                notPredicate.SetPredicate(ParsePredicate());

                predicate = notPredicate;
            }
            else
            {   //Other predicates exists out of an expression
                Expression expression = expressionParser.ParseExpression();

                if (TokenStream.HasNext() && TokenStream.Peek(1).GetValue().ToString() == ".")
                {   //IsPredicate
                    predicate = ParseIsPredicate(expression);
                }
                else
                {   //ExpressionPredicate
                    predicate = ParseExpressionPredicate(expression);
                }
            }

            //Check for && or || predicates
            if (TokenStream.HasNext() && TokenStream.Peek(1).GetValue().ToString() == "&" && TokenStream.Peek(2).GetValue().ToString() == "&")
            {   //AndPredicate
                return ParseAndPredicate(predicate);   
            }
            else if (TokenStream.HasNext() && TokenStream.Peek(1).GetValue().ToString() == "|" && TokenStream.Peek(2).GetValue().ToString() == "|")
            {   //OrPredicate
                return ParseOrPredicate(predicate);
            }
            return predicate;
        }

        /// <summary>
        /// Parser for IsPredicate
        /// </summary>
        /// <returns>Parsed IsPredicate</returns>
        public IsPredicate ParseIsPredicate(Expression parsedExpression)
        {
            IsPredicate isPredicate = new IsPredicate();
            
            //Set already parsed expression
            isPredicate.SetExpression(parsedExpression);

            //Skip . token
            NextToken(".", "Expression.Type?", '.');

            //Parse type
            isPredicate.SetType(ParseType());

            //Skip ? token
            NextToken("?", "Expression.Type?", '?');

            return isPredicate;
        }

        /// <summary>
        /// Parser for AndPredicate
        /// </summary>
        /// <returns>Parsed AndPredicate</returns>
        public AndPredicate ParseAndPredicate(Predicate parsedLeftPredicate)
        {
            AndPredicate andPredicate = new AndPredicate();
            
            //Add left predicate
            andPredicate.SetLeftPredicate(parsedLeftPredicate);

            //Skip && tokens
            NextToken("&", "predicate && predicate", '&');
            NextToken("&", "predicate && predicate", '&');

            //Parse right predicate
            andPredicate.SetRightPredicate(ParsePredicate());

            return andPredicate;
        }

        /// <summary>
        /// Parser for OrPredicate
        /// </summary>
        /// <returns>Parsed OrPredicate</returns>
        public OrPredicate ParseOrPredicate(Predicate parsedLeftPredicate)
        {
            OrPredicate orPredicate = new OrPredicate();

            //Add left predicate
            orPredicate.SetLeftPredicate(parsedLeftPredicate);

            //Skip || tokens
            NextToken("|", "predicate || predicate", '|');
            NextToken("|", "predicate || predicate", '|');

            //Parse right predicate
            orPredicate.SetRightPredicate(ParsePredicate());

            return orPredicate;
        }

        /// <summary>
        /// Parser for ExpressionPredicate
        /// </summary>
        /// <param name="parsedExpression">Expression which is already parsed</param>
        /// <returns>Parsed ExpressionPredicate</returns>
        public ExpressionPredicate ParseExpressionPredicate(Expression parsedExpression)
        {
            ExpressionPredicate expressionPredicate = new ExpressionPredicate();

            expressionPredicate.SetExpression(parsedExpression);

            return expressionPredicate;
        }

        /// <summary>
        /// Parser for Type
        /// </summary>
        /// <returns>Parsed Type</returns>
        public Type ParseType()
        {
            //return specific type
            CurrentToken = TokenStream.NextToken();

            if (CurrentToken.GetValue().ToString() == "list")
            {   //ListType
                return new ListType();
            }
            else if (CurrentToken.GetValue().ToString() == "record")
            {   //RecordType
                return new RecordType();
            }
            else if (CurrentToken.GetValue().ToString() == "string")
            {   //StringType
                return new StringType();
            }
            else
            {   //Unexpected type, throw exception
                throw new UnexpectedToken("Type expected, but found:", CurrentToken.GetValue().ToString(), CurrentToken.GetLine());
            }
        }

        #endregion
    }
}
