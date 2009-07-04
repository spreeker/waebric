using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Expressions
{
    /// <summary>
    /// Node which contains RecordExpression
    /// { IdCon : Expression, IdCon: Expression }
    /// </summary>
    public class RecordExpression : Expression
    {
        #region Private Members

        private NodeList RecordList;

        #endregion

        #region Public Methods

        public RecordExpression()
        {
            RecordList = new NodeList();
        }

        /// <summary>
        /// Add KeyValuePair to RecordExpression
        /// </summary>
        /// <param name="pair">KeyValuePair to add</param>
        public void AddKeyValuePair(KeyValuePair pair)
        {
            RecordList.Add(pair);
        }

        /// <summary>
        /// Get list of records
        /// </summary>
        /// <returns>RecordList</returns>
        public NodeList GetRecords()
        {
            return RecordList;
        }

        /// <summary>
        /// Get string representation of RecordExpression
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            //Build string with all items separated by comma
            StringBuilder records = new StringBuilder();
            KeyValuePair[] recordArray = (KeyValuePair[]) RecordList.ToArray();
            for (int i = 0; i <= (recordArray.Length - 1); i++)
            {
                records.Append(recordArray[i].ToString());
                if (i != (recordArray.Length - 1))
                {
                    records.Append(",");
                }
            }
            return "{" + records.ToString() + "}";
        }

        public override ISyntaxNode[] GetSubNodes()
        {
            return new ISyntaxNode[] { 
                RecordList
            };
        }

        public override void AcceptVisitor(ISyntaxNodeVisitor visitor)
        {
            visitor.Visit(this);
        }

        #endregion
    }
}
