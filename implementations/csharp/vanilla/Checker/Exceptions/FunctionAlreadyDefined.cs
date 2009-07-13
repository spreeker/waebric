using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Checker.Exceptions
{
    /// <summary>
    /// Exception when function is defined more than once
    /// </summary>
    public class FunctionAlreadyDefined : Exception
    {
        #region Public Methods

        public FunctionAlreadyDefined(String identifier)
        {
            Console.WriteLine("Function already defined: " + identifier);
        }

        #endregion
    }
}
