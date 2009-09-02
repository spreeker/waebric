using System;
using System.Collections.Generic;
using System.Collections;
using System.Linq;
using System.Text;
using Parser.Ast.Functions;
using Parser.Ast.Expressions;

namespace Common
{
    /// <summary>
    /// SymbolTable which holds functions and variables in an hashtable
    /// </summary>
    public class SymbolTable : ICloneable
    {
        #region Private Members

        private SymbolTable ParentSymbolTable;
        private Dictionary<String, FunctionDefinition> FunctionDefinitions;
        private Dictionary<String, Expression> VariableDefinitions;

        #endregion

        #region Public Methods

        /// <summary>
        /// Create empty SymbolTable
        /// </summary>
        public SymbolTable()
        {
            ParentSymbolTable = null;
            FunctionDefinitions = new Dictionary<string, FunctionDefinition>();
            VariableDefinitions = new Dictionary<string, Expression>();
        }

        /// <summary>
        /// Create empty SymbolTable with reference to parent SymbolTable
        /// </summary>
        /// <param name="parentSymbolTable"></param>
        public SymbolTable(SymbolTable parentSymbolTable)
        {
            ParentSymbolTable = parentSymbolTable;
            FunctionDefinitions = new Dictionary<string, FunctionDefinition>();
            VariableDefinitions = new Dictionary<string, Expression>();
        }

        /// <summary>
        /// Add FunctionDefinition to SymbolTable
        /// </summary>
        /// <param name="functionDefinition">FunctionDefinition to add</param>
        public void AddFunctionDefinition(FunctionDefinition functionDefinition)
        {
            FunctionDefinitions.Add(functionDefinition.GetIdentifier(), functionDefinition);
        }

        /// <summary>
        /// Retrieve list of FunctionDefinitions
        /// </summary>
        /// <returns>FunctionDefinitionList</returns>
        public List<FunctionDefinition> GetFunctionDefinitions()
        {
            return FunctionDefinitions.Values.ToList();
        }

        /// <summary>
        /// Get FunctionDefinition
        /// </summary>
        /// <param name="identifier">Identifier of FunctionDefinition</param>
        /// <returns>FunctionDefinition</returns>
        public FunctionDefinition GetFunctionDefinition(String identifier)
        {
            if (FunctionDefinitions.ContainsKey(identifier))
            {
                return FunctionDefinitions[identifier];
            }
            if (ParentSymbolTable != null)
            {
                return ParentSymbolTable.GetFunctionDefinition(identifier);
            }
            return null;
        }

        /// <summary>
        /// Retrieve list of names of FunctionDefinitions
        /// </summary>
        /// <returns>FunctionDefinitionNames</returns>
        public List<String> GetFunctionDefinitionNames()
        {
            return FunctionDefinitions.Keys.ToList();
        }

        /// <summary>
        /// Add VariableDefinition to SymbolTable
        /// </summary>
        /// <param name="variableDefinition">VariableDefinition to add</param>
        public void AddVariableDefinition(String identifier, Expression value)
        {
            VariableDefinitions.Add(identifier, value);
        }

        /// <summary>
        /// Get value of specified variable
        /// </summary>
        /// <param name="identifier">Identifier of variable</param>
        /// <returns>Expression of variable</returns>
        public Expression GetVariableDefinition(String identifier)
        {
            if(VariableDefinitions.ContainsKey(identifier))
            {
                return VariableDefinitions[identifier];
            }
            if (ParentSymbolTable != null)
            {
                return ParentSymbolTable.GetVariableDefinition(identifier);
            }
            return null;
        }

        /// <summary>
        /// Retrieve list of VariableNames
        /// </summary>
        /// <returns>VariableNameList</returns>
        public List<String> GetVariableNames()
        {
            return VariableDefinitions.Keys.ToList();
        }

        /// <summary>
        /// Check if an specified variable is already in SymbolTable
        /// </summary>
        /// <param name="identifier">Identifier of variable</param>
        /// <returns>True if in SymbolTable, otherwise false</returns>
        public bool ContainsVariable(String identifier)
        {
            bool contains = VariableDefinitions.ContainsKey(identifier);
            if (contains)
            {
                return VariableDefinitions[identifier] != null;
            }
            if(!contains && ParentSymbolTable != null)
            {
                return ParentSymbolTable.ContainsVariable(identifier);
            }
            return contains;
        }

        /// <summary>
        /// Check if an specified function is already in SymbolTable
        /// </summary>
        /// <param name="identifier">Identifier of Function</param>
        /// <returns>True if in SymbolTable, otherwise false</returns>
        public bool ContainsFunction(String identifier)
        {
            bool contains = FunctionDefinitions.ContainsKey(identifier);
            if (!contains && ParentSymbolTable != null)
            {
                return ParentSymbolTable.ContainsFunction(identifier);
            }
            return contains;
        }

        /// <summary>
        /// Retrieve parent SymbolTable
        /// </summary>
        /// <returns>SymbolTable which is parent of current SymbolTable</returns>
        public SymbolTable GetParentSymbolTable()
        {
            return ParentSymbolTable;
        }

        public Object Clone()
        {
            SymbolTable clonedSymbolTable = new SymbolTable(GetParentSymbolTable());
            //Copy functions
            foreach (KeyValuePair<String, FunctionDefinition> def in FunctionDefinitions)
            {
                clonedSymbolTable.AddFunctionDefinition(def.Value);
            }

            //Copy variables
            foreach (KeyValuePair<String, Expression> pair in VariableDefinitions)
            {
                clonedSymbolTable.AddVariableDefinition(pair.Key, pair.Value);
            }

            return clonedSymbolTable;
        }

        #endregion
    }
}
