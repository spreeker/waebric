﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Embedding
{
    /// <summary>
    /// Node which contains an PreText
    /// \"TextChar*>
    /// </summary>
    public class PreText : ISyntaxNode
    {
        #region Private Members

        private String Text;

        #endregion
        #region Public Methods

        public void SetText(String text)
        {
            Text = text;
        }

        public String GetText()
        {
            return Text;
        }

        public override String ToString()
        {
            return "\"" + Text + "<";
        }

        public ISyntaxNode[] GetSubNodes()
        {
            return new ISyntaxNode[] { };
        }

        public void AcceptVisitor(ISyntaxNodeVisitor visitor)
        {
            visitor.Visit(this);
        }

        #endregion
    }
}
