using System;
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

        #endregion

        #region Public Methods

        public Markup()
        {
            //Intialize members
            ArgumentList = new NodeList();
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

        public override String ToString()
        {
            if(ArgumentList.Count == 0)
            {
                return Tag.ToString();
            }
            else
            {
                //Get arguments
                Argument[] argumentArray = (Argument[]) ArgumentList.ToArray();
                StringBuilder stringBuilder = new StringBuilder();

                for (int i = 0; i <= (argumentArray.Length - 1); i++)
                {
                    stringBuilder.Append(argumentArray[i].ToString());
                    if (i != (argumentArray.Length - 1))
                    {
                        stringBuilder.Append(",");
                    }
                }

                return Tag.ToString() + "(" + stringBuilder.ToString() + ")";
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
