using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Predicates
{
    /// <summary>
    /// Node which contains an RecordType
    /// </summary>
    public class RecordType : Type
    {
        #region Public Methods

        public override String ToString()
        {
            return "record";
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
