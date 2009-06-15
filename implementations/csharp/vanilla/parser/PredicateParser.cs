using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser
{
    /// <summary>
    /// Parser for predicates
    /// </summary>
    public class PredicateParser : AbstractParser
    {
        #region Private Members

        #endregion

        #region Public Methods

        public PredicateParser(TokenIterator iterator, List<Exception> exceptions)
            : base(iterator, exceptions)
        {

        }

        #endregion
    }
}
