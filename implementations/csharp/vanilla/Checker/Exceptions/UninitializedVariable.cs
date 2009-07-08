using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Checker.Exceptions
{
    /// <summary>
    /// Exception when variable is not initialized
    /// </summary>
    public class UninitializedVariable : Exception
    {
        #region Public Methods

        public UninitializedVariable()
        {

        }

        #endregion
    }
}
