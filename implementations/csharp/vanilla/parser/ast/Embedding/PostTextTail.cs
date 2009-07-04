using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Embedding
{
    /// <summary>
    /// Node which contains an PostTextTail
    /// </summary>
    public class PostTextTail : TextTail
    {
        #region Private Members

        private PostText PostText;

        #endregion

        #region Public Methods

        public void SetPostText(PostText postText)
        {
            PostText = postText;
        }

        public PostText GetPostText()
        {
            return PostText;
        }

        public override String ToString()
        {
            return PostText.ToString();
        }

        public override ISyntaxNode[] GetSubNodes()
        {
            return new ISyntaxNode[] { PostText };
        }

        public override void AcceptVisitor(ISyntaxNodeVisitor visitor)
        {
            visitor.Visit(this);
        }

        #endregion
    }
}
