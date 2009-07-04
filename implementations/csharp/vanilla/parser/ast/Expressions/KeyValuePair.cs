using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Expressions
{
    /// <summary>
    /// Node which contains KeyValuePair
    /// IdCon:Expression
    /// </summary>
    public class KeyValuePair : ISyntaxNode
    {
        #region Private Members

        private String Key;
        private Expression Value;

        #endregion

        #region Public Methods

        /// <summary>
        /// Set key of KeyValuePair
        /// </summary>
        /// <param name="key">Key to set</param>
        public void SetKey(String key)
        {
            Key = key;
        }
    
        /// <summary>
        /// Get key of KeyValuePair
        /// </summary>
        /// <returns>Key</returns>
        public String GetKey()
        {
            return Key;
        }

        /// <summary>
        /// Set value of KeyValuePair
        /// </summary>
        /// <param name="value">Value to set</param>
        public void SetValue(Expression value)
        {
            Value = value;
        }

        /// <summary>
        /// Get value of KeyValuePair
        /// </summary>
        /// <returns>Value</returns>
        public Expression GetValue()
        {
            return Value;
        }

        /// <summary>
        /// Get string representation of KeyValuePair
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            return Key + ":" + Value.ToString();
        }

        public ISyntaxNode[] GetSubNodes()
        {
            return new ISyntaxNode[] { 
                Value
            };
        }

        public void AcceptVisitor(ISyntaxNodeVisitor visitor)
        {
            visitor.Visit(this);
        }

        #endregion
    }
}
