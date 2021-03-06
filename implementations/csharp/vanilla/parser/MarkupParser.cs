﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Lexer.Tokenizer;
using Parser.Ast.Site;
using Parser.Ast.Markup;
using Attribute = Parser.Ast.Markup.Attribute;
using Parser.Exceptions;

namespace Parser
{
    /// <summary>
    /// Parser for markup
    /// </summary>
    public class MarkupParser : AbstractParser
    {
        #region Private Members

        private ExpressionParser expressionParser;

        #endregion

        #region Public Methods

        public MarkupParser(TokenIterator iterator)
            : base(iterator)
        {
            //Create parsers here
            expressionParser = new ExpressionParser(iterator);
        }

        /// <summary>
        /// Parser for markup
        /// </summary>
        /// <returns>Parsed markup</returns>
        public Markup ParseMarkup()
        {
            //First parse mandatory designator
            Markup markup = new Markup();
            markup.SetDesignator(ParseDesignator());

            //Determine if arguments are after designator
            if (TokenStream.Peek(1).GetType() == TokenType.SYMBOL && TokenStream.Peek(1).GetValue().ToString() == "(")
            {
                //We are dealing with an MarkupCall, so set Markup as Call
                markup.SetCall();
                ParseArguments(markup);
            }

            return markup;
        }

        /// <summary>
        /// Parser for Designator
        /// </summary>
        /// <returns>Parsed Designator</returns>
        public Designator ParseDesignator()
        {
            Designator tag = new Designator();
                        
            //Parse identifier of tag
            NextToken("identifier", "identifier");
            tag.SetIdentifier(CurrentToken.GetValue().ToString());

            while (TokenStream.HasNext())
            {   //Parse attributes
                if (TokenStream.Peek(1).GetType() == TokenType.SYMBOL && IsAttribute(TokenStream.Peek(1).GetValue().ToString()))
                {
                    tag.AddAttribute(ParseAttribute());
                }
                else
                {
                    break; //No attributes left, so break
                }
            }

            return tag;
        }

        /// <summary>
        /// Parser for Attribute
        /// </summary>
        /// <returns>Parsed Attribute</returns>
        public Attribute ParseAttribute()
        {
            CurrentToken = TokenStream.NextToken();
            Attribute attribute = null;

            switch(CurrentToken.GetValue().ToString())
            { //Determine different types of attributes
                case "#": //Id attribute
                    attribute = ParseIdAttribute();
                    break;
                case ".": //Class attribute
                    attribute = ParseClassAttribute();
                    break;
                case "$": //Name attribute
                    attribute = ParseNameAttribute();
                    break;
                case ":": //Type attribute
                    attribute = ParseTypeAttribute();
                    break;
                case "@": //Width_(Height) attribute
                    if(TokenStream.Peek(2).GetValue().ToString() == "%")
                    {   //Width-Height attribute
                        attribute = ParseWidth_HeightAttribute();
                    }
                    else
                    {   //Only width attribute
                        attribute = ParseWidthAttribute();
                    }
                    break;
                default:
                    throw new UnexpectedToken("Expected attribute, but found:", CurrentToken.GetValue().ToString(), CurrentToken.GetLine());
            }

            return attribute;
        }

        /// <summary>
        /// Parser for IdAttribute
        /// </summary>
        /// <returns>Parsed IdAttribute</returns>
        public IdAttribute ParseIdAttribute()
        {
            IdAttribute idAttribute = new IdAttribute();

            //Get id token
            CurrentToken = TokenStream.NextToken();
            idAttribute.SetId(CurrentToken.GetValue().ToString());

            return idAttribute;
        }
        
        /// <summary>
        /// Parser for ClassAttribute
        /// </summary>
        /// <returns>Parsed ClassAttribute</returns>
        public ClassAttribute ParseClassAttribute()
        {
            ClassAttribute classAttribute = new ClassAttribute();

            //Get class token
            CurrentToken = TokenStream.NextToken();
            classAttribute.SetClass(CurrentToken.GetValue().ToString());

            return classAttribute;
        }

        /// <summary>
        /// Parser for NameAttribute
        /// </summary>
        /// <returns>Parsed NameAttribute</returns>
        public NameAttribute ParseNameAttribute()
        {
            NameAttribute nameAttribute = new NameAttribute();

            //Get name token
            CurrentToken = TokenStream.NextToken();
            nameAttribute.SetName(CurrentToken.GetValue().ToString());

            return nameAttribute;
        }

        /// <summary>
        /// Parser for TypeAttribute
        /// </summary>
        /// <returns>Parsed TypeAttribute</returns>
        public TypeAttribute ParseTypeAttribute()
        {
            TypeAttribute typeAttribute = new TypeAttribute();

            //Get type token
            CurrentToken = TokenStream.NextToken();
            typeAttribute.SetType(CurrentToken.GetValue().ToString());

            return typeAttribute;
        }

        /// <summary>
        /// Parser for Width_HeightAttribute
        /// </summary>
        /// <returns>Parsed Width_HeightAttribute</returns>
        public Width_HeightAttribute ParseWidth_HeightAttribute()
        {
            Width_HeightAttribute widthHeightAttribute = new Width_HeightAttribute();

            //Get width
            CurrentToken = TokenStream.NextToken();
            widthHeightAttribute.SetWidth(ObjectToInt(CurrentToken.GetValue()));

            //Skip % token
            NextToken("%", "@width %height", '%');

            //Get height
            CurrentToken = TokenStream.NextToken();
            widthHeightAttribute.SetHeight(ObjectToInt(CurrentToken.GetValue()));

            return widthHeightAttribute;
        }

        /// <summary>
        /// Parser for WidthAttribute
        /// </summary>
        /// <returns>Parsed HeightAttribute</returns>
        public WidthAttribute ParseWidthAttribute()
        {
            WidthAttribute widthAttribute = new WidthAttribute();

            //Get height
            CurrentToken = TokenStream.NextToken();
            widthAttribute.SetWidth(ObjectToInt(CurrentToken.GetValue()));

            return widthAttribute;
        }
        
        /// <summary>
        /// Parser for Arguments
        /// </summary>
        /// <param name="markup">Markup to add arguments to</param>
        /// <returns>Parsed arguments</returns>
        public void ParseArguments(Markup markup)
        {
            //Skip ( character
            NextToken("(", "(Argument, Argument)", '(');

            while (TokenStream.HasNext())
            { //Parse argument(s)
                if (TokenStream.Peek(1).GetValue().ToString() == ")")
                { //No arguments left, so break
                    break;
                }
                else if (TokenStream.Peek(1).GetValue().ToString() == ",")
                { //Second argument
                    //Skip comma
                    NextToken(",", "Argument, Argument", ',');
                }
                
                //Parse argument
                markup.AddArgument(ParseArgument());
            }

            //Skip ) character
            NextToken(")", "(Argument, Argument)", ')');
        }

        /// <summary>
        /// Parser for an argument
        /// </summary>
        /// <returns>Parsed Argument</returns>
        public Argument ParseArgument()
        {
            Argument argument = null;
            //Determine type of argument
            if (TokenStream.Peek(2).GetValue().ToString() == "=")
            {   //IdCon = Expression
                argument = ParseAttrArgument();
            }
            else
            {   //Expression
                argument = ParseExpressionArgument();
            }

            return argument;
        }

        /// <summary>
        /// Parser for ExpressionArgument
        /// </summary>
        /// <returns>ExpressionArgument</returns>
        public Argument ParseExpressionArgument()
        {
            ExpressionArgument argument = new ExpressionArgument();

            //Parse expression
            argument.SetExpression(expressionParser.ParseExpression());

            return argument;
        }

        /// <summary>
        /// Parser for AttrArgument
        /// </summary>
        /// <returns>Parsed AttrArgument</returns>
        public AttrArgument ParseAttrArgument()
        {
            AttrArgument argument = new AttrArgument();

            //Parse identifier
            CurrentToken = TokenStream.NextToken();
            argument.SetIdentifier(CurrentToken.GetValue().ToString());

            //Skip = token
            NextToken("=", "identifier = expression", '=');

            //Parse expression
            argument.SetExpression(expressionParser.ParseExpression());

            return argument;
        }

        #endregion

        #region Private Methods

        /// <summary>
        /// Method to check if symbol is attribute symbol
        /// Attribute symbols are: #.$:@
        /// </summary>
        /// <param name="c">String to check</param>
        /// <returns>IsAttribute</returns>
        private bool IsAttribute(String c)
        {
            return c == ":" || c == "#" || c == "$" || c == "." || c == ":" || c == "@";
        }

        #endregion
    }
}
