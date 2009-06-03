using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Lexer.Tokenizer;
using System.Collections;

namespace Parser
{
    /// <summary>
    /// This class contains the general parser. 
    /// From here the complete program is parsed and the parse tree is being build.
    /// </summary>
    public abstract class AbstractParser
    {
        #region Private/Protected Members
        
        protected List<Exception> ExceptionList;
        protected TokenIterator TokenStream;
        protected Token CurrentToken;

        #endregion

        #region Public Methods

        /// <summary>
        /// Creates an AbstractParser with tokenStream and exceptionList
        /// </summary>
        /// <param name="tokenStream">Tokenized Stream</param>
        /// <param name="exceptionList">List of Exceptions</param>
        public AbstractParser(TokenIterator tokenStream, List<Exception> exceptionList)
        {
            this.TokenStream = tokenStream;
            this.ExceptionList = exceptionList;
        }

        #endregion Public Methods

        #region Private/Protected Methods

        /// <summary>
        /// Get NextToken and verify if it exists
        /// </summary>
        /// <param name="name">Name of expected token</param>
        /// <param name="syntax">Syntax of expected token</param>
        /// <returns>True if new token found, otherwise false</returns>
        protected bool NextToken(String name, String syntax)
        {
            if (TokenStream.HasNext())
            {
                CurrentToken = TokenStream.NextToken();
                return true;
            }
            else
            {
                //Add exception handling here (add to exception list)
                return false;
            }
        }

        /// <summary>
        /// Get NextToken and verify type of token
        /// </summary>
        /// <param name="name">Name of expected token</param>
        /// <param name="syntax">Syntax of expected token</param>
        /// <param name="type">Type of expected token</param>
        /// <returns>True if token found and type matches, otherwise false</returns>
        protected bool NextToken(String name, String syntax, TokenType type)
        {
            if (NextToken(name, syntax))
            {
                if (type.Equals(CurrentToken.GetType()))
                {
                    return true; 
                }
                else
                {
                    //Add exception handling here
                    return false;
                }
            }
            //Add exception handling here
            return false;
        }


        /// <summary>
        /// Get NextToken and verify it maches matchObject
        /// </summary>
        /// <param name="name">Name of expected token</param>
        /// <param name="syntax">Type of expected token</param>
        /// <param name="matchObject">Type of expected token</param>
        /// <returns>True if token matches, otherwise false</returns>
        protected bool NextToken(String name, String syntax, Object matchObject)
        {
            if (NextToken(name, syntax))
            {
                if (CurrentToken.GetValue().Equals(matchObject))
                {
                    return true;
                }
                else
                {
                    //Add exception handling here
                    return false;
                }
            }
            //Add exception handling here
            return false;
        }

        /// <summary>
        /// Match two objects. Used for token matching during parsing.
        /// </summary>
        /// <param name="retrieved">Object retrieved from TokenStream</param>
        /// <param name="expected">Object which is expected</param>
        /// <returns></returns>
        protected bool Match(Object retrieved, Object expected)
        {
            return expected.Equals(retrieved);
        }

        #endregion
    }
}
