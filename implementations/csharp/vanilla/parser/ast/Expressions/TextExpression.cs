using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Expressions
{
    /// <summary>
    /// Node which contains TextExpression
    /// QUOTE
    /// </summary>
    public class TextExpression : Expression
    {
        #region Private Members

        private String Text;

        #endregion

        #region Public Methods

        /// <summary>
        /// Set text
        /// </summary>
        /// <param name="text">Text</param>
        public void SetText(String text)
        {
            Text = text;
        }

        /// <summary>
        /// Get text 
        /// </summary>
        /// <returns>Text</returns>
        public String GetText()
        {
            return Text;
        }

        /// <summary>
        /// Get string representation of text
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            return Text;
        }

        public override ISyntaxNode[] GetSubNodes()
        {
            return new ISyntaxNode[] { };
        }

        public override void AcceptVisitor(ISyntaxNodeVisitor visitor)
        {
            visitor.Visit(this);
        }

        #endregion
    }
}
