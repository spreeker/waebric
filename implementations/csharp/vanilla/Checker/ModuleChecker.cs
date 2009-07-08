using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using Parser.Ast.Statements;
using Parser.Ast.Module;
using Common;
using Checker.Exceptions;
using Parser.Ast;


namespace Checker
{
    /// <summary>
    /// Checker which checks module references
    /// </summary>
    public class ModuleChecker : SyntaxNodeVisitor
    {
        #region Private Members

        private List<Exception> ExceptionList;

        #endregion

        #region Public Methods

        public ModuleChecker(List<Exception> exceptionList)
        {
            ExceptionList = exceptionList;
        }

        public new void Visit(Module module)
        {
            //Check filename
            module.GetModuleId().AcceptVisitor(this);

            //Get dependencies and check their existence
            List<Module> DependencyList = ModuleCache.Instance.GetDependencies(module);
            foreach (Module moduleDependency in DependencyList)
            {
                foreach (Import import in moduleDependency.GetImports())
                {   //Check imports of dependency's
                    import.AcceptVisitor(this);
                }
            }
        }

        public new void Visit(Import import)
        {
            //Check if import exists
            import.GetModuleId().AcceptVisitor(this);
        }

        public new void Visit(ModuleId moduleId)
        {
            //Check if path exists
            String path = ModuleCache.Instance.GetPath(moduleId);
            if (!File.Exists(path))
            {
                ExceptionList.Add(new NonExistingModule(moduleId));
            }
        }

        #endregion
    }
}
