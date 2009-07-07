using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Statements
{
    /// <summary>
    /// Node which contains an MarkupStatStatement
    /// </summary>
    public class MarkupStatStatement : Statement
    {
        #region Private Members

        private NodeList MarkupList;
        private Statement Statement;

        #endregion

        #region Public Methods

        public MarkupStatStatement()
        {
            //Initialize member
            MarkupList = new NodeList();
        }

        public void SetMarkups(NodeList markups)
        {
            MarkupList = markups;
        }

        public void AddMarkup(Markup.Markup markup)
        {
            MarkupList.Add(markup);
        }

        public NodeList GetMarkups()
        {
            return MarkupList;
        }

        public void SetStatement(Statement statement)
        {
            Statement = statement;
        }

        public Statement GetStatement()
        {
            return Statement;
        }

        public override String ToString()
        {   //To Implement
            return null;
        }

        public override ISyntaxNode[] GetSubNodes()
        {
            return new ISyntaxNode[] {
                MarkupList,
                Statement
            };
        }

        public override void AcceptVisitor(ISyntaxNodeVisitor visitor)
        {
            visitor.Visit(this);
        }

        #endregion

    }
}
