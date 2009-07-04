using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Predicates
{
    /// <summary>
    /// Node which contains an ListType
    /// </summary>
    public class ListType : Type
    {
        #region Public Methods

        public override String ToString()
        {
            return "list";
        }

        public override void AcceptVisitor(ISyntaxNodeVisitor visitor)
        {
            visitor.Visit(this);
        }

        public override ISyntaxNode[] GetSubNodes()
        {
            return new ISyntaxNode[] {};
        }

        #endregion
    }
}
