using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Embedding
{
    /// <summary>
    /// Node which contains an MarkupEmbed
    /// </summary>
    public class MarkupEmbed : Embed
    {
        #region Private Members

        private List<Markup.Markup> MarkupList;
        private Markup.Markup Markup;

        #endregion

        #region Public Methods

        public MarkupEmbed()
        {
        }

        public void SetMarkups(List<Markup.Markup> markupList)
        {
            MarkupList = markupList;
        }

        public List<Markup.Markup> GetMarkups()
        {
            return MarkupList;
        }

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
            StringBuilder stringBuilder = new StringBuilder();
            Markup.Markup[] markupArray = MarkupList.ToArray();

            //build markup string
            for (int i = 0; i <= (markupArray.Length - 1); i++)
            {
                stringBuilder.Append(markupArray[i].ToString());
            }

            return stringBuilder.ToString() + Markup.ToString();
        }

        #endregion
    }
}
