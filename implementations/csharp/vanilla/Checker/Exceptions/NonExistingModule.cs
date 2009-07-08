using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Parser.Ast.Module;

namespace Checker.Exceptions
{
    /// <summary>
    /// Exception when module not found
    /// </summary>
    public class NonExistingModule : Exception
    {
        #region Public Methods

        public NonExistingModule(ModuleId moduleId)
        {
            Console.WriteLine("Error: Module " + moduleId.ToString() + "not found.");
        }

        #endregion
    }
}
