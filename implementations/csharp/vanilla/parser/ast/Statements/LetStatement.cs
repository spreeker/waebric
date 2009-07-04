using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Statements
{
    /// <summary>
    /// Node which contains an LetStatement
    /// </summary>
    public class LetStatement : Statement
    {
        #region Private Members

        private NodeList AssignmentList;
        private NodeList StatementList;

        #endregion

        #region Public Methods

        public LetStatement()
        {
            //Create members
            AssignmentList = new NodeList();
            StatementList = new NodeList();
        }
        
        /// <summary>
        /// Add assignment to LetStatement
        /// </summary>
        /// <param name="assignment">Assignment to add</param>
        public void AddAssignment(Assignment assignment)
        {
            AssignmentList.Add(assignment);
        }

        /// <summary>
        /// Get assignments of LetStatement
        /// </summary>
        /// <returns>AssignmentList</returns>
        public NodeList GetAssignments()
        {
            return AssignmentList;
        }

        /// <summary>
        /// Add statement to LetStatement
        /// </summary>
        /// <param name="statement">Statement to add</param>
        public void AddStatement(Statement statement)
        {
            StatementList.Add(statement);
        }

        /// <summary>
        /// Get statements of LetStatement
        /// </summary>
        /// <returns>StatementList</returns>
        public NodeList GetStatements()
        {
            return StatementList;
        }

        /// <summary>
        /// Get string representation of StatementList
        /// </summary>
        /// <returns></returns>
        public override String ToString()
        {
            //TODO: Implement this method
            return null;
        }

        public override ISyntaxNode[] GetSubNodes()
        {
            return new ISyntaxNode[] { 
                AssignmentList,
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
