using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Predicates
{
    /// <summary>
    /// Node which contains an ListType
    /// </summary>
    public class ListType : Type
    {
        #region Public Methods

        public override String ToString()
        {
            return "list";
        }

        #endregion
    }
}
