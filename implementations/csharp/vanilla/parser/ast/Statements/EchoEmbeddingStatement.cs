using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Statements
{
    /// <summary>
    /// Node which contains an EchoEmbeddingStatement
    /// </summary>
    public class EchoEmbeddingStatement : EchoStatement
    {
        #region Private Members

        private Embedding.Embedding EchoEmbedding;

        #endregion

        #region Public Methods

        public void SetEmbedding(Embedding.Embedding embedding)
        {
            EchoEmbedding = embedding;
        }

        public Embedding.Embedding GetEmbedding()
        {
            return EchoEmbedding;
        }

        public override String ToString()
        {
            return "echo " + EchoEmbedding.ToString() + ";";
        }

        public override ISyntaxNode[] GetSubNodes()
        {
            return new ISyntaxNode[] { 
                EchoEmbedding
            };
        }

        public override void AcceptVisitor(ISyntaxNodeVisitor visitor)
        {
            visitor.Visit(this);
        }

        #endregion
    }
}
