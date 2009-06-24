using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Lexer.Tokenizer;
using Parser.Ast.Embedding;

namespace Parser
{
    public class EmbeddingParser : AbstractParser
    {
        #region Private Members

        MarkupParser markupParser;
        ExpressionParser expressionParser;

        #endregion

        #region Public Methods

        public EmbeddingParser(TokenIterator iterator, List<Exception> exceptionList)
            : base(iterator, exceptionList)
        {
            //Create subparsers
            markupParser = new MarkupParser(iterator, exceptionList);
            expressionParser = new ExpressionParser(iterator, exceptionList);
        }

        /// <summary>
        /// Parser for Embedding
        /// </summary>
        /// <returns>Parsed Embedding</returns>
        public Embedding ParseEmbedding()
        {
            Embedding embedding = new Embedding();
            return null;
        }

        /// <summary>
        /// Parser for PreText
        /// </summary>
        /// <returns>Parsed PreText</returns>
        public PreText ParsePreText()
        {
            PreText preText = new PreText();

            //Skip " token
            NextToken("\"", "\" TextChar* <", '"');

            //Parse text
            if (TokenStream.Peek(1).GetValue().ToString() != "<")
            {
                CurrentToken = TokenStream.NextToken();
                preText.SetText(CurrentToken.GetValue().ToString());
            }

            //Skip < token
            NextToken("<", "\" TextChar* <", '<');

            return preText;
        }
        
        /// <summary>
        /// Parser for MidText
        /// </summary>
        /// <returns>Parsed MidText</returns>
        public MidText ParseMidText()
        {
            MidText midText = new MidText();

            //Skip > token
            NextToken(">", "> TextChar* <", '>');

            //Parse text
            if (TokenStream.Peek(1).GetValue().ToString() != "<")
            {
                CurrentToken = TokenStream.NextToken();
                midText.SetText(CurrentToken.GetValue().ToString());
            }

            //Skip < token
            NextToken("<", "> TextChar* <", '<');

            return midText;
        }

        /// <summary>
        /// Parser for PostText
        /// </summary>
        /// <returns>Parsed PostText</returns>
        public PostText ParsePostText()
        {
            PostText postText = new PostText();

            //Skip > token
            NextToken(">", "> TextChar* \"", '>');

            //Parse text
            if (TokenStream.Peek(1).GetValue().ToString() != "\"")
            {
                CurrentToken = TokenStream.NextToken();
                postText.SetText(CurrentToken.GetValue().ToString());
            }

            //Skip " token
            NextToken("\"", "> TextChar* \"", '"');

            return postText;
        }

        /// <summary>
        /// Parser for Embed
        /// </summary>
        /// <returns>Parsed Embed</returns>
        public Embed ParseEmbed()
        {
            return null;
        }

        /// <summary>
        /// Parser for TextTail
        /// </summary>
        /// <returns>Parsed TextTail</returns>
        public TextTail ParseTextTail()
        {
            return null;
        }       

        #endregion
    }
}
