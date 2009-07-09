using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Parser.Ast.Module;
using Parser.Ast;

namespace Checker
{
    /// <summary>
    /// Class which contains a checker for WaebricAst's
    /// </summary>
    public class WaebricChecker
    {
        #region Private Members

        #endregion

        #region Public Methods

        public List<Exception> CheckSyntaxTree(SyntaxTree tree)
        {
            List<Exception> exceptions = new List<Exception>(); 

            //Check module
            ModuleChecker moduleChecker = new ModuleChecker(exceptions);
            moduleChecker.Visit(tree.GetRoot());

            //Check function/variable declarations
            DeclarationChecker declarationChecker = new DeclarationChecker(exceptions);
            declarationChecker.Visit(tree.GetRoot());

            return exceptions;
        }

        #endregion

    }
}
