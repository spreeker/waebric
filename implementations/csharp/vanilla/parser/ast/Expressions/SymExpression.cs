using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Expressions
{
    /// <summary>
    /// Node which contains a SymExpression
    /// SymbolCon
    /// </summary>
    public class SymExpression : Expression
    {
        #region Private Members

        private String Sym;

        #endregion

        #region Public Methods

        /// <summary>
        /// Set Sym
        /// </summary>
        /// <param name="sym">Sym to set</param>
        public void SetSym(String sym)
        {
            Sym = sym;
        }

        /// <summary>
        /// Get Sym
        /// </summary>
        /// <returns>Sym</returns>
        public String GetSym()
        {
            return Sym;
        }

        /// <summary>
        /// Get string representation of Sym
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            return Sym;
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
