using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Predicates
{
    /// <summary>
    /// Node which contains an StringType
    /// </summary>
    public class StringType : Type
    {
        #region Public Methods

        public override String ToString()
        {
            return "string";
        }

        #endregion
    }
}
