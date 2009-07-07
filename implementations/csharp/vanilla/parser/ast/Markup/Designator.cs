using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Markup
{
    /// <summary>
    /// Node wich holds designator
    /// </summary>
    public class Designator : ISyntaxNode
    {
        #region Private Members

        private String Identifier;
        private NodeList AttributeList;

        #endregion

        #region Public Methods

        /// <summary>
        /// Empty constructor
        /// </summary>
        public Designator()
        {
            //Create containers
            AttributeList = new NodeList();
        }

        /// <summary>
        /// Constructor with identifier specification
        /// </summary>
        /// <param name="identifier">Identifier</param>
        public Designator(String identifier)
        {
            Identifier = identifier;
        }

        /// <summary>
        /// Get Identifier of Designator
        /// </summary>
        /// <returns>Identifier</returns>
        public String GetIdentifier()
        {
            return Identifier;
        }

        /// <summary>
        /// Set Identifier of Designator
        /// </summary>
        /// <param name="identifier">Identifier to set</param>
        public void SetIdentifier(String identifier)
        {
            Identifier = identifier;
        }

        /// <summary>
        /// Add attribute to designator
        /// </summary>
        /// <param name="attribute">Attribute to add</param>
        public void AddAttribute(Attribute attribute)
        {
            AttributeList.Add(attribute);
        }

        /// <summary>
        /// Get list of attributes
        /// </summary>
        /// <returns>AttributeList</returns>
        public NodeList GetAttributes()
        {
            return AttributeList;
        }

        public override String ToString()
        {
            String buffer = "";
            foreach (Attribute node in AttributeList)
            {
                buffer += node.ToString();
            }

            if (buffer != "")
            {
                return Identifier + " " + buffer;
            }
            else
            {
                return Identifier + buffer;
            }
            
        }

        public void AcceptVisitor(ISyntaxNodeVisitor visitor)
        {
            visitor.Visit(this);
        }

        public ISyntaxNode[] GetSubNodes()
        {
            return new ISyntaxNode[] {
                AttributeList
            };
        }

        #endregion
    }
}
