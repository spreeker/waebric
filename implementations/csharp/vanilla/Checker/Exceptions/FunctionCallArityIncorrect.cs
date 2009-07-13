using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Checker.Exceptions
{
    /// <summary>
    /// Exception when specified arguments at a function call
    /// are not ok.
    /// </summary>
    public class FunctionCallArityIncorrect : Exception
    {
        #region Public Methods

        public FunctionCallArityIncorrect(String identifier)
        {
            Console.WriteLine("Function " + identifier + " called with wrong number of arguments");
        }

        #endregion
    }
}
