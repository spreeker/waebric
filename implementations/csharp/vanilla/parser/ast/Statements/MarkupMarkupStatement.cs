using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Statements
{
    /// <summary>
    /// Node which contains an MarkupMarkupStatement
    /// </summary>
    public class MarkupMarkupStatement : Statement
    {
        #region Private Members

        private NodeList MarkupList;
        private Markup.Markup Markup;

        #endregion

        #region Public Methods

        public MarkupMarkupStatement()
        {
            //Initialize member
            MarkupList = new NodeList();
        }

        public void AddMarkup(Markup.Markup markup)
        {
            MarkupList.Add(markup);
        }

        public void SetMarkups(NodeList markups)
        {
            MarkupList = markups;
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
            //Get Markups
            String markups = "";
            foreach (ISyntaxNode markup in MarkupList)
            {
                markups += ((Markup.Markup)markup).ToString();
                markups += " ";
            }

            return markups + Markup.ToString() + ";";
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
