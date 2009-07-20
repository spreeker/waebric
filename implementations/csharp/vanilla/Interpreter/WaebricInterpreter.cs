using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Web.UI;
using System.IO;
using Parser.Ast;
using Parser.Ast.Module;
using Common;
using Parser.Ast.Functions;
using Parser.Ast.Site;
using Parser.Ast.Markup;

namespace Interpreter
{
    public class WaebricInterpreter
    {
        #region Private Members

        private StreamWriter Writer = null;
        private SymbolTable SymbolTable;

        #endregion

        #region Public Methods

        public WaebricInterpreter()
        {
            Writer = null;
        }

        /// <summary>
        /// Interprets root and generates XHTML files
        /// </summary>
        /// <param name="tree">Tree to interpret</param>
        public void InterpretAST(SyntaxTree tree)
        {
            Module root = tree.GetRoot();

            //Get dependency's and add functions to SymbolTable
            List<Module> dependencyList = ModuleCache.Instance.RequestDependencies(root);
            SymbolTable = new SymbolTable();
            foreach (Module module in dependencyList)
            {
                foreach (FunctionDefinition function in module.GetFunctionDefinitions())
                {
                    SymbolTable.AddFunctionDefinition(function);
                }
            }

            //Interpret the main function
            if (ContainsMainFunction(root))
            {
                //TODO!!! DETERMINE SITE TO WRITE 
                XHTMLVisitor xhtmlVisitor = new XHTMLVisitor(SymbolTable);
                SymbolTable.GetFunctionDefinition("main").AcceptVisitor(xhtmlVisitor);

                //Write xhtml output
                XHTMLStreamWriter writer = new XHTMLStreamWriter(Writer, XHTMLStreamWriter.DocType.TRANSITIONAL, xhtmlVisitor.GetTree());
                writer.WriteStream();
            }

            //Interpret all sites an write to files
            foreach (Module module in dependencyList)
            {
                foreach (Site site in module.GetSites())
                {
                    foreach (Mapping mapping in site.GetMappings())
                    {
                        //Get function which should be called
                        Markup markup = mapping.GetMarkup();

                        //Determine site path and open writer and lets interpret it
                        Writer = CreateOutputStream(mapping.GetPath().ToString());
                        XHTMLVisitor xhtmlVisitor = new XHTMLVisitor(SymbolTable);
                        markup.AcceptVisitor(xhtmlVisitor);

                        //Write xhtml output
                        XHTMLStreamWriter writer = new XHTMLStreamWriter(Writer, XHTMLStreamWriter.DocType.TRANSITIONAL, xhtmlVisitor.GetTree());
                        writer.WriteStream();
                    }
                }
            }
            
            

        }

        #endregion

        #region Private Methods

        /// <summary>
        /// Method which opens an streamwriter on specified path and creates directory's if needed
        /// </summary>
        /// <returns>StreamWriter</returns>
        private StreamWriter CreateOutputStream(String path)
        {
            int directoryIndex = path.LastIndexOf("/");

            if (directoryIndex != -1)
            {   //Directory's found, so create them
                System.IO.Directory.CreateDirectory(path.Substring(0, directoryIndex));
            }

            //Create file and return StreamWriter which writes to new file
            FileStream stream = File.Create(path);
            return new StreamWriter(stream);
        }

        /// <summary>
        /// Method which checks if the specified module contains an main function
        /// </summary>
        /// <param name="module">Module to check</param>
        /// <returns>True if main found, otherwise false</returns>
        private bool ContainsMainFunction(Module module)
        {
            foreach (FunctionDefinition def in module.GetFunctionDefinitions())
            {
                if (def.GetIdentifier() == "main")
                {
                    return true;
                }
            }
            return false;
        }

        #endregion
    }
}
