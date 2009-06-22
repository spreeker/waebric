using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Predicates
{
    /// <summary>
    /// Node which contains an NotPredicate
    /// </summary>
    public class NotPredicate : Predicate
    {
        #region Private Members

        private Predicate Predicate;

        #endregion

        #region Public Method

        /// <summary>
        /// Set predicate of NotPredicate
        /// </summary>
        /// <param name="predicate">Predicate to set</param>
        public void SetPredicate(Predicate predicate)
        {
            Predicate = predicate;
        }

        /// <summary>
        /// Get predicate of NotPredicate
        /// </summary>
        /// <returns>Predicate</returns>
        public Predicate GetPredicate()
        {
            return Predicate;
        }

        /// <summary>
        /// Get string representation of NotPredicate
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            return "!" + Predicate.ToString();
        }

        #endregion
    }
}
