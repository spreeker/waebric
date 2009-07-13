using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Checker.Exceptions
{
    /// <summary>
    /// Exception when non existing variable is called
    /// </summary>
    public class UndefinedVariable : Exception
    {
        #region Public Methods

        public UndefinedVariable(String identifier)
        {
            Console.WriteLine("Variable " + identifier + " used but not declared");
        }

        #endregion
    }
}
