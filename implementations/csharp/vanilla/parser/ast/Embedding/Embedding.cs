using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Embedding
{
    /// <summary>
    /// Node which contains an Embedding
    /// </summary>
    public class Embedding : ISyntaxNode
    {
        #region Private Members

        private PreText PreText;
        private Embed Embed;
        private TextTail TextTail;

        #endregion

        #region Public Methods

        public void SetPreText(PreText preText)
        {
            PreText = preText;
        }

        public PreText GetPreText()
        {
            return PreText;
        }

        public void SetEmbed(Embed embed)
        {
            Embed = embed;
        }

        public Embed GetEmbed()
        {
            return Embed;
        }

        public void SetTextTail(TextTail textTail)
        {
            TextTail = textTail;
        }

        public TextTail GetTextTail()
        {
            return TextTail;
        }

        public override String ToString()
        {
            return PreText.ToString() + Embed.ToString() + TextTail.ToString();
        }
        #endregion
    }
}
