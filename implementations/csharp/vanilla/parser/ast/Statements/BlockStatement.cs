using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Statements
{
    /// <summary>
    /// Node which contains an BlockStatement
    /// </summary>
    public class BlockStatement : Statement
    {
        #region Private Members

        private NodeList StatementList;

        #endregion

        #region Public Methods

        public BlockStatement()
        {
            StatementList = new NodeList();
        }

        /// <summary>
        /// Add statement to BlockStatement
        /// </summary>
        /// <param name="statement">Statement to add</param>
        public void AddStatement(Statement statement)
        {
            StatementList.Add(statement);
        }

        /// <summary>
        /// Get statements of BlockStatement
        /// </summary>
        /// <returns>StatementList</returns>
        public NodeList GetStatements()
        {
            return StatementList;
        }

        /// <summary>
        /// Get string representation of BlockStatement
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            StringBuilder stringBuilder = new StringBuilder();
            Statement[] statementArray = (Statement[]) StatementList.ToArray();

            //Construct string
            for (int i = 0; i <= statementArray.Length; i++)
            {
                stringBuilder.Append(statementArray[i].ToString());
            }

            return "{" + stringBuilder.ToString() + "}";
        }

        public override ISyntaxNode[] GetSubNodes()
        {
            return new ISyntaxNode[] { 
                StatementList
            };
        }

        public override void AcceptVisitor(ISyntaxNodeVisitor visitor)
        {
            visitor.Visit(this);
        }

        #endregion
    }
}
