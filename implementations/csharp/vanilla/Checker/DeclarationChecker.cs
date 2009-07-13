using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Parser.Ast;
using Parser.Ast.Functions;
using Common;
using Parser.Ast.Module;
using Checker.Exceptions;
using Parser.Ast.Site;
using Parser.Ast.Statements;
using Parser.Ast.Expressions;
using Parser.Ast.Markup;

namespace Checker
{
    /// <summary>
    /// Class which contains an DeclarationChecker.
    /// This checker checks for undefined function,
    /// incorrect declarations/calls and undefined/uninitialized variables
    /// </summary>
    public class DeclarationChecker : SyntaxNodeVisitor
    {
        #region Private Members

        private List<Exception> ExceptionList;
        private SymbolTable SymbolTable;

        #endregion

        #region Public Methods

        public DeclarationChecker(List<Exception> exceptionList)
        {
            ExceptionList = exceptionList;
            SymbolTable = new SymbolTable();
        }

        /// <summary>
        /// Visit module and check declarations in module and dependency's
        /// </summary>
        /// <param name="module">Module to visit</param>
        public override void Visit(Module module)
        {
            //Get dependency for module
            List<Module> dependencyList = ModuleCache.Instance.RequestDependencies(module);

            //Store all functiondefinitions
            foreach (Module dependency in dependencyList)
            {
                foreach (FunctionDefinition functionDef in dependency.GetFunctionDefinitions())
                {
                    //Check if function is not already defined
                    if (SymbolTable.ContainsFunction(functionDef.GetIdentifier()))
                    {
                        ExceptionList.Add(new FunctionAlreadyDefined(functionDef.GetIdentifier()));
                    }
                    else
                    {
                        SymbolTable.AddFunctionDefinition(functionDef);
                    }
                }
            }

            //Check all definitions
            foreach (Module dependency in dependencyList)
            {
                //Check FunctionDefinitions
                foreach (FunctionDefinition function in dependency.GetFunctionDefinitions())
                {
                    function.AcceptVisitor(this);
                }

                //Check markups in sites
                foreach (Site site in dependency.GetSites())
                {
                    foreach (Mapping mapping in site.GetMappings())
                    {
                        mapping.AcceptVisitor(this);
                    }
                }
            }
        }

        /// <summary>
        /// Visit FunctionDefinition to check declarations
        /// </summary>
        /// <param name="functionDefinition">FunctionDefinition to check</param>
        public override void Visit(FunctionDefinition functionDefinition)
        {
            CreateChildSymbolTable();

            //Check Formals
            foreach(Formal formal in functionDefinition.GetFormals())
            {   
                //Add variable, but with nullvalue, because value is undefined in definition
                SymbolTable.AddVariableDefinition(formal.GetIdentifier(), null);
            }

            //Check Statements of function
            foreach (Statement statement in functionDefinition.GetStatements())
            {
                statement.AcceptVisitor(this);
            }

            MoveToParentSymbolTable();
        }

        /// <summary>
        /// Visit EachStatement 
        /// </summary>
        /// <param name="statement">EachStatement to check</param>
        public override void Visit(EachStatement statement)
        {
            CreateChildSymbolTable();

            //Store variable of Each = IdCon : Expression 
            SymbolTable.AddVariableDefinition(statement.GetIdentifier(), statement.GetExpression());
            
            //Visit statement
            statement.GetStatement().AcceptVisitor(this);

            MoveToParentSymbolTable();
        }

        /// <summary>
        /// Visit LetStatement
        /// </summary>
        /// <param name="statement">LetStatement to check</param>
        public override void Visit(LetStatement statement)
        {
            foreach (Assignment assignment in statement.GetAssignments())
            {
                //Go level deeper in SymbolTable, to check using variables in other assignments
                CreateChildSymbolTable();

                //Visit Assignment
                assignment.AcceptVisitor(this);
            }


            //Visit statements
            foreach(Statement stmt in statement.GetStatements())
            {
                stmt.AcceptVisitor(this);
            }

            //Go back to level of statement in SymbolTable
            foreach (Assignment assignment in statement.GetAssignments())
            {
                MoveToParentSymbolTable();
            }
        }

        /// <summary>
        /// Visit VarBindAssignment
        /// </summary>
        /// <param name="assignment">VarBindAssignment to check</param>
        public override void Visit(VarBindAssignment assignment)
        {
            assignment.GetExpression().AcceptVisitor(this);
            SymbolTable.AddVariableDefinition(assignment.GetIdentifier(), assignment.GetExpression());
        }

        /// <summary>
        /// Visit FuncBindAssignment
        /// </summary>
        /// <param name="assignment">FuncBindAssignment to check</param>
        public override void Visit(FuncBindAssignment assignment)
        {
            //FuncBind is a function, so let checking do by FunctionDefinition visitor
            FunctionDefinition function = new FunctionDefinition();
            function.SetIdentifier(assignment.GetIdentifier());

            foreach (String identifier in assignment.GetIdentifiers())
            {
                Formal formal = new Formal();
                formal.SetIdentifier(identifier);
                function.AddFormal(formal);
            }

            function.AddStatement(assignment.GetStatement());

            //Check this new function
            function.AcceptVisitor(this);
            //Add to SymbolTable
            SymbolTable.AddFunctionDefinition(function);
        }

        /// <summary>
        /// Visit VarExpression
        /// </summary>
        /// <param name="expression">VarExpression to check</param>
        public override void Visit(VarExpression expression)
        {
            //Check if expression is assigned to existing variable
            if(!SymbolTable.ContainsVariable(expression.GetVariableIdentifier()))
            {   //Add undefined variable exception
                ExceptionList.Add(new UndefinedVariable(expression.GetVariableIdentifier()));
            }
        }

        /// <summary>
        /// Visit Markup
        /// </summary>
        /// <param name="markup">Markup to check</param>
        public override void Visit(Markup markup)
        {
            //Check identifier in designator, it should be an XHTML or an user defined function
            String identifier = markup.GetDesignator().GetIdentifier();

            if(SymbolTable.ContainsFunction(identifier))
            {
                //Check arguments
                FunctionDefinition referencedDefinition = SymbolTable.GetFunctionDefinition(identifier);

                if(referencedDefinition.GetFormals().Count != markup.GetArguments().Count)
                {   //Arity mismatch
                    ExceptionList.Add(new FunctionCallArityIncorrect(identifier));
                }
            }
            else
            {
                //Check if it is XHTML, if not its undefined
                if (!IdentifierIsXHTML(identifier))
                {
                    ExceptionList.Add(new UndefinedFunction(identifier));
                }
            }
        }

        #endregion

        #region Private Methods

        /// <summary>
        /// Method which creates a new child SymbolTable with current SymbolTable as parent
        /// </summary>
        private void CreateChildSymbolTable()
        {
            SymbolTable = new SymbolTable(SymbolTable);
        }

        /// <summary>
        /// Method which moves the current SymbolTable to his parent
        /// </summary>
        private void MoveToParentSymbolTable()
        {
            SymbolTable = SymbolTable.GetParentSymbolTable();
        }

        /// <summary>
        /// Checks if an identifier is an XHTML tag
        /// </summary>
        /// <param name="identifier">Identifier to check</param>
        /// <returns>True if identifier is XHTML, otherwhise false</returns>
        private bool IdentifierIsXHTML(String identifier)
        {
            String[] xhtmlTags = Enum.GetNames(typeof(XHTMLTags));
            foreach (String item in xhtmlTags)
            {
                if (item.Equals(identifier.ToUpper()))
                {
                    return true;
                }
            }
            return false;
        }

        #endregion
    }
}
