using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Predicates
{
    /// <summary>
    /// Node which contains an OrPredicate
    /// </summary>
    public class OrPredicate : Predicate
    {
        #region Private Members

        private Predicate LeftPredicate;
        private Predicate RightPredicate;

        #endregion

        #region Public Methods

        /// <summary>
        /// Set rightpredicate of OrPredicate
        /// </summary>
        /// <param name="rightPredicate">RightPredicate to set</param>
        public void SetRightPredicate(Predicate rightPredicate)
        {
            RightPredicate = rightPredicate;
        }

        /// <summary>
        /// Get rightpredicate of OrPredicate
        /// </summary>
        /// <returns>RightPredicate</returns>
        public Predicate GetRightPredicate()
        {
            return RightPredicate;
        }

        /// <summary>
        /// Set leftpredicate of OrPredicate
        /// </summary>
        /// <param name="leftPredicate">LeftPredicate to set</param>
        public void SetLeftPredicate(Predicate leftPredicate)
        {
            LeftPredicate = leftPredicate;
        }

        /// <summary>
        /// Get leftpredicate of OrPredicate
        /// </summary>
        /// <returns>LeftPredicate</returns>
        public Predicate GetLeftPredicate()
        {
            return LeftPredicate;
        }

        /// <summary>
        /// Get string representation of OrPredicate
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            return LeftPredicate.ToString() + "||" + RightPredicate.ToString();
        }

        #endregion
    }
}
