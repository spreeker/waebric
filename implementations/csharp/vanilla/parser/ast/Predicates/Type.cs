﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Predicates
{
    /// <summary>
    /// Node which contains an Type
    /// </summary>
    public abstract class Type : ISyntaxNode
    {
        #region Public Methods

        public abstract void AcceptVisitor(ISyntaxNodeVisitor visitor);
        public abstract ISyntaxNode[] GetSubNodes();

        #endregion
    }
}
