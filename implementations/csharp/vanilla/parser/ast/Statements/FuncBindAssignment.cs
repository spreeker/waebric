using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Statements
{
    public class FuncBindAssignment : Assignment
    {
        #region Private Members

        private String Identifier;
        private List<String> IdentifierList;
        private Statement Statement;

        #endregion

        #region Public Methods

        public FuncBindAssignment()
        {
            //Initialize identifierList
            IdentifierList = new List<String>();
        }

        public void SetIdentifier(String identifier)
        {
            Identifier = identifier;
        }

        public String GetIdentifier()
        {
            return Identifier;
        }

        public void AddIdentifier(String identifier)
        {
            IdentifierList.Add(identifier);
        }

        public List<String> GetIdentifiers()
        {
            return IdentifierList;
        }

        public void SetStatement(Statement statement)
        {
            Statement = statement;
        }

        public Statement GetStatement()
        {
            return Statement;
        }

        public override String ToString()
        {
            //Convert IdentifierList to string
            String identifiers = "";
            for(int i = 0; i <= (IdentifierList.Count - 1); i++)
            {
                identifiers += IdentifierList.ElementAt(i);
                if(i != (IdentifierList.Count - 1))
                {
                    identifiers += ",";
                }
            }
            return Identifier + "(" + identifiers + ")" + " = " + Statement.ToString();
        }

        public override ISyntaxNode[] GetSubNodes()
        {
            return new ISyntaxNode[] {};
        }

        public override void AcceptVisitor(ISyntaxNodeVisitor visitor)
        {
            visitor.Visit(this);
        }

        #endregion
    }
}
