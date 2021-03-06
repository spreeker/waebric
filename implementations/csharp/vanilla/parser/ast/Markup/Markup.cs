﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Markup
{
    /// <summary>
    /// Node containing Markup
    /// </summary>
    public class Markup : ISyntaxNode
    {
        #region Private Members

        private NodeList ArgumentList;
        private Designator Tag;
        private bool IsCall;

        #endregion

        #region Public Methods

        public Markup()
        {
            //Intialize members
            ArgumentList = new NodeList();
            IsCall = false;
        }

        /// <summary>
        /// Set Designator tag
        /// </summary>
        /// <param name="tag">Tag to set</param>
        public void SetDesignator(Designator tag)
        {
            Tag = tag;
        }

        /// <summary>
        /// Get Designator tag
        /// </summary>
        /// <returns>Tag</returns>
        public Designator GetDesignator()
        {
            return Tag;
        }

        /// <summary>
        /// Add argument to markup
        /// </summary>
        /// <param name="argument">Argument to add</param>
        public void AddArgument(Argument argument)
        {
            ArgumentList.Add(argument);
        }

        /// <summary>
        /// Get arguments
        /// </summary>
        /// <returns>ArgumentList</returns>
        public NodeList GetArguments()
        {
            return ArgumentList;
        }

        /// <summary>
        /// Set Markup as call
        /// </summary>
        public void SetCall()
        {
            IsCall = true;
        }

        /// <summary>
        /// Get state of Call
        /// </summary>
        /// <returns></returns>
        public bool GetCallState()
        {
            return IsCall;
        }

        public override String ToString()
        {
            if(ArgumentList.Count == 0)
            {
                return Tag.ToString();
            }
            else
            {
                //Get arguments
                String buffer = "";
                int counter = 0;
                foreach (Argument node in ArgumentList)
                {
                    buffer += node.ToString();
                    if (counter != (ArgumentList.Count - 1))
                    {
                        buffer += ",";
                    }
                    counter++;
                }
                return Tag.ToString() + "(" + buffer + ")";
            }
        }

        public void AcceptVisitor(ISyntaxNodeVisitor visitor)
        {
            visitor.Visit(this);
        }

        public ISyntaxNode[] GetSubNodes()
        {
            return new ISyntaxNode[] { 
                Tag,
                ArgumentList
            };
        }

        #endregion

    }
}
