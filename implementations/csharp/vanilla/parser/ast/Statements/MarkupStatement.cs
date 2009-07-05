using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Statements
{
    /// <summary>
    /// Node which contains an MarkupStatement
    /// </summary>
    public class MarkupStatement : Statement
    {
        #region Private Members

        private Markup.Markup Markup;

        #endregion

        #region Public Methods

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
            return Markup.ToString() + ";";
        }

        public override ISyntaxNode[] GetSubNodes()
        {
            return new ISyntaxNode[] {
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
