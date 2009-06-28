using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Lexer.Tokenizer;
using Parser.Ast.Embedding;
using Parser.Exceptions;
using Parser.Ast.Markup;

namespace Parser
{
    public class EmbeddingParser : AbstractParser
    {
        #region Private Members

        private MarkupParser markupParser;
        private ExpressionParser expressionParser;
        private TokenIterator EmbeddingTokenStream;

        #endregion

        #region Public Methods

        public EmbeddingParser(TokenIterator iterator)
            : base(iterator)
        {
        }

        /// <summary>
        /// Parser for Embedding
        /// </summary>
        /// <returns>Parsed Embedding</returns>
        public Embedding ParseEmbedding()
        {
            //Get iterator for specific embedding
            if (TokenStream.Peek(1).GetType() == TokenType.EMBEDDING)
            {   //Store embedding in internal tokeniterator to parse internally
                CurrentToken = TokenStream.NextToken();
                EmbeddingTokenStream = ((EmbeddingToken)CurrentToken).GetTokenIterator();
            }
            else
            {   //Raise exception
                throw new UnexpectedToken("Embedding expected, but found:", CurrentToken.GetValue().ToString(), CurrentToken.GetLine());
            }
            
            //Let's parse embedding
            Embedding embedding = new Embedding();

            embedding.SetPreText(ParsePreText());
            embedding.SetEmbed(ParseEmbed());
            embedding.SetTextTail(ParseTextTail());

            return embedding;
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
            preText.SetText(ParseTextChars());

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
            midText.SetText(ParseTextChars());

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
            postText.SetText(ParseTextChars());

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
            Embed embed = null;
            List<Markup> markupList = new List<Markup>();

            //Set up MarkupParser
            markupParser = new MarkupParser(EmbeddingTokenStream);

            //Parse Markup*
            while(EmbeddingTokenStream.HasNext(2) && !(EmbeddingTokenStream.Peek(2).GetValue().ToString() == ">"))
            {
                markupList.Add(markupParser.ParseMarkup());
            }
            
            //Determine embed type
            if (NextTokenIsMarkup())
            {   //MarkupEmbedding
                MarkupEmbed markupEmbed = new MarkupEmbed();

                //Add already parsed markups to markupEmbed
                markupEmbed.SetMarkups(markupList);

                //Parse latest markup 
                markupEmbed.SetMarkup(markupParser.ParseMarkup());

                embed = markupEmbed;
            }
            else
            {   //ExpressionEmbedding
                ExpressionEmbed expressionEmbed = new ExpressionEmbed();

                //Add already parsed markups to expressionEmbed
                expressionEmbed.SetMarkups(markupList);

                //Set up expressionparser
                expressionParser = new ExpressionParser(EmbeddingTokenStream);

                //Parse expression
                expressionEmbed.SetExpression(expressionParser.ParseExpression());

                embed = expressionEmbed;
            }           

            return embed;
        }

        /// <summary>
        /// Parser for TextTail
        /// </summary>
        /// <returns>Parsed TextTail</returns>
        public TextTail ParseTextTail()
        {
            TextTail textTail = null;
            
            //Determine TextTail type
            if (TokenStream.HasNext() && TokenStream.Peek(1).GetValue().ToString() == ">" && TokenStream.Peek(3).GetValue().ToString() == "\"")
            {   //PostTextTail
                textTail = ParsePostTextTail();
            }
            else if (TokenStream.HasNext() && TokenStream.Peek(1).GetValue().ToString() == ">")
            {   //MidTextTail
                textTail = ParseMidTextTail();
            }

            return textTail;
        }

        /// <summary>
        /// Parser for PostTextTail
        /// </summary>
        /// <returns>Parsed PostTextTail</returns>
        public PostTextTail ParsePostTextTail()
        {
            PostTextTail postTextTail = new PostTextTail();

            //Skip > token
            NextToken(">", "> PostText \"", '>');

            //Parse text
            postTextTail.SetPostText(ParsePostText());

            //Skip " token
            NextToken("\"", "> PostText \"", '"');

            return postTextTail;
        }

        /// <summary>
        /// Parser for MidTextTail
        /// </summary>
        /// <returns>Parsed MidTextTail</returns>
        public MidTextTail ParseMidTextTail()
        {
            MidTextTail midTextTail = new MidTextTail();

            //Skip > token
            NextToken(">", "MidText Embed TextTail", '>');

            //Parse MidText
            midTextTail.SetMidText(ParseMidText());

            //Parse Embed
            midTextTail.SetEmbed(ParseEmbed());

            //Parse TextTail
            midTextTail.SetTextTail(ParseTextTail());

            return midTextTail;
        }

        /// <summary>
        /// Parser for TextChars
        /// </summary>
        /// <returns>Parsed TextChars as String</returns>
        public String ParseTextChars()
        {
            StringBuilder stringBuilder = new StringBuilder();
            while (EmbeddingTokenStream.HasNext() && EmbeddingTokenStream.Peek(1).GetType() == TokenType.TEXT)
            {   //Parse all text tokens until different type found
                CurrentToken = EmbeddingTokenStream.NextToken();
                stringBuilder.Append(CurrentToken.GetValue().ToString());
            }

            return stringBuilder.ToString();
        }

        /// <summary>
        /// Function to make testing possible
        /// </summary>
        /// <param name="stream">Stream to inject</param>
        public void SetEmbeddingTokenStream(TokenIterator stream)
        {
            EmbeddingTokenStream = stream;
        }

        #endregion

        #region Private Members

        /// <summary>
        /// Get NextToken and verify if it exists (EmbeddedTokenStream)
        /// </summary>
        /// <param name="name">Name of expected token</param>
        /// <param name="syntax">Syntax of expected token</param>
        /// <returns>True if new token found, otherwise false</returns>
        private new bool NextToken(String name, String syntax)
        {
            if (EmbeddingTokenStream.HasNext())
            {
                CurrentToken = EmbeddingTokenStream.NextToken();
                return true;
            }
            else
            {
                throw new UnexpectedToken("Unexpected token found:", CurrentToken.GetValue().ToString(), CurrentToken.GetLine());
            }
        }

        /// <summary>
        /// Get NextToken and verify type of token (EmbeddedTokenStream)
        /// </summary>
        /// <param name="name">Name of expected token</param>
        /// <param name="syntax">Syntax of expected token</param>
        /// <param name="type">Type of expected token</param>
        /// <returns>True if token found and type matches, otherwise false</returns>
        private new bool NextToken(String name, String syntax, TokenType type)
        {
            if (NextToken(name, syntax))
            {
                if (type.Equals(CurrentToken.GetType()))
                {
                    return true;
                }
                else
                {
                    throw new UnexpectedToken("Unexpected token found:", CurrentToken.GetValue().ToString(), CurrentToken.GetLine());
                }
            }
            return false;
        }


        /// <summary>
        /// Get NextToken and verify it maches matchObject (EmbeddedTokenStream)
        /// </summary>
        /// <param name="name">Name of expected token</param>
        /// <param name="syntax">Type of expected token</param>
        /// <param name="matchObject">Type of expected token</param>
        /// <returns>True if token matches, otherwise false</returns>
        private new bool NextToken(String name, String syntax, Object matchObject)
        {
            if (NextToken(name, syntax))
            {
                if (CurrentToken.GetValue().Equals(matchObject))
                {
                    return true;
                }
                else
                {
                    throw new UnexpectedToken("Unexpected token found:", CurrentToken.GetValue().ToString(), CurrentToken.GetLine());
                }
            }
            return false;
        }

        /// <summary>
        /// Method to check if next token is markup
        /// </summary>
        /// <returns>True if next token is markup, otherwise false</returns>
        private bool NextTokenIsMarkup()
        {
            if(EmbeddingTokenStream.HasNext() && EmbeddingTokenStream.Peek(1).GetType() == TokenType.IDENTIFIER)
            {   //
                if (EmbeddingTokenStream.HasNext(3) && EmbeddingTokenStream.Peek(2).GetValue().ToString() == "("
                   && EmbeddingTokenStream.Peek(4).GetValue().ToString() == ")")
                {   //CallMarkup
                    return true;
                }
                else if (EmbeddingTokenStream.HasNext(2) && EmbeddingTokenStream.Peek(2).GetValue().ToString() == ";")
                {   
                    //Statements are not Markup
                    return false;
                }
                else
                {
                    // Everything which is not tail is markup
                    return true;
                }
            }
            return false;
        }

        #endregion
    }
}
