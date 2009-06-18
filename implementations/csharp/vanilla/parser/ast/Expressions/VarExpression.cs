using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Expressions
{
    /// <summary>
    /// Node which contains VarExpression
    /// IdCon
    /// </summary>
    public class VarExpression : Expression
    {
        #region Private Members

        private String VariableIdentifier;
        
        #endregion

        #region Public Methods

        /// <summary>
        /// Empty constructor
        /// </summary>
        public VarExpression()
        {
        }
        
        /// <summary>
        /// Constructor with variable identifier specification
        /// </summary>
        /// <param name="variableIdentifier">Identifier of variable</param>
        public VarExpression(String variableIdentifier)
        {
            VariableIdentifier = variableIdentifier;
        }

        /// <summary>
        /// Get identifier of variable
        /// </summary>
        /// <returns>Identifier</returns>
        public String GetVariableIdentifier()
        {
            return VariableIdentifier;
        }

        /// <summary>
        /// Set identifier of variable
        /// </summary>
        /// <param name="variableIdentifier">Identifier to set</param>
        public void SetVariableIdentifier(String variableIdentifier)
        {
            VariableIdentifier = variableIdentifier;
        }

        /// <summary>
        /// Get string representation of VarExpression
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            return VariableIdentifier;
        }

        #endregion
    }
}
