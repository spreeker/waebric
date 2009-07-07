using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Embedding
{
    /// <summary>
    /// Node which contains an MarkupEmbed
    /// </summary>
    public class MarkupEmbed : Embed
    {
        #region Private Members

        private NodeList MarkupList;
        private Markup.Markup Markup;

        #endregion

        #region Public Methods

        public MarkupEmbed()
        {
        }

        public void SetMarkups(NodeList markupList)
        {
            MarkupList = markupList;
        }

        public NodeList GetMarkups()
        {
            return MarkupList;
        }

        public void SetMarkup(Markup.Markup markup)
        {
            Markup = markup;
        }

        public Markup.Markup GetMarkup()
        {
            return Markup;
        }

        public override String ToString()
        {
            string Buffer = "";

            foreach (Markup.Markup node in MarkupList)
            {
                Buffer += node.ToString();
            }
            return Buffer.ToString() + Markup.ToString();
        }

        public override ISyntaxNode[] GetSubNodes()
        {
            return new ISyntaxNode[] { 
                MarkupList,
                Markup
            };
        }

        public override void AcceptVisitor(ISyntaxNodeVisitor visitor)
        {
            visitor.Visit(this);
        }

        #endregion
    }
}
