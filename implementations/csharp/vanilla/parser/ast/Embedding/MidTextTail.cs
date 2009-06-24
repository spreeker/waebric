using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Embedding
{
    /// <summary>
    /// Node which contains an MidTextTail
    /// </summary>
    public class MidTextTail : TextTail
    {
        #region Private Members

        private MidText MidText;
        private Embed Embed;
        private TextTail TextTail;

        #endregion

        #region Public Methods

        public void SetMidText(MidText midText)
        {
            MidText = midText;
        }

        public MidText GetMidText()
        {
            return MidText;
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
            return MidText.ToString() + Embed.ToString() + TextTail.ToString();
        }

        #endregion
    }
}
