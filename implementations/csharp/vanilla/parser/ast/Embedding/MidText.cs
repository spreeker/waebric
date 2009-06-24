using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Embedding
{
    /// <summary>
    /// Node which contains an MidText
    /// </summary>
    public class MidText : ISyntaxNode
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
            return ">" + Text + "<";
        }

        #endregion
    }
}
