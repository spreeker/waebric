using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Module
{
    /// <summary>
    /// ModuleIdentifier
    /// </summary>
    public class ModuleId : ISyntaxNode
    {
        #region Private Members

        private List<String> IdentifierList;

        #endregion

        #region Public Methods

        /// <summary>
        /// Create ModuleId without setting identifier
        /// </summary>
        public ModuleId()
        {
            IdentifierList = new List<String>();
        }

        /// <summary>
        /// Get identifiers of ModuleId
        /// </summary>
        /// <returns>IdentifierList</returns>
        public List<String> GetIdentifiers()
        {
            return IdentifierList;
        }

        /// <summary>
        /// Add identifier to ModuleId
        /// </summary>
        /// <param name="identifier">Identifier to add</param>
        public void AddIdentifier(String identifier)
        {
            IdentifierList.Add(identifier);   
        }

        public override String ToString()
        {
            String[] identifierArray = IdentifierList.ToArray();
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i <= (identifierArray.Length - 1); i++)
            {
                stringBuilder.Append(identifierArray[i]);
                if (i != (identifierArray.Length - 1))
                {
                    stringBuilder.Append(".");
                }
            }

            return stringBuilder.ToString();
        }

        public ISyntaxNode[] GetSubNodes()
        {
            return new ISyntaxNode[] { };
        }

        public void AcceptVisitor(ISyntaxNodeVisitor visitor)
        {
            visitor.Visit(this);
        }

        #endregion
    }
}
