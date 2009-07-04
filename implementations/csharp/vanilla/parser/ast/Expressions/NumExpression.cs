using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Expressions
{
    /// <summary>
    /// Node which contains an NumExpression
    /// NatCon
    /// </summary>
    public class NumExpression : Expression
    {
        #region Private Members

        private int Num;

        #endregion

        #region Public Methods
        
        /// <summary>
        /// Set num
        /// </summary>
        /// <param name="num">Num to set</param>
        public void SetNum(int num)
        {
            Num = num;
        }

        /// <summary>
        /// Get num
        /// </summary>
        /// <returns>Num</returns>
        public int GetNum()
        {
            return Num;
        }

        /// <summary>
        /// Get string representation of Num
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            return Num.ToString();
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
