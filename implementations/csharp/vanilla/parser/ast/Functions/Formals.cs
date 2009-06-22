using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Functions
{
    /// <summary>
    /// Node which contains Formals
    /// </summary>
    public class Formals : ISyntaxNode
    {
        #region Private Members

        private List<Formal> FormalList;

        #endregion

        #region Public Methods

        public Formals()
        {
            FormalList = new List<Formal>();
        }

        /// <summary>
        /// Add formal to Formals
        /// </summary>
        /// <param name="formal">Formal to add</param>
        public void AddFormal(Formal formal)
        {
            FormalList.Add(formal);
        }

        /// <summary>
        /// Get Formals
        /// </summary>
        /// <returns>FormalList</returns>
        public List<Formal> GetFormals()
        {
            return FormalList;
        }

        /// <summary>
        /// Get string representation of formals
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            //Convert list to array
            Formal[] formalArray = FormalList.ToArray();

            //Build string with all formals separated by a comma
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i <= (formalArray.Length - 1); i++)
            {
                stringBuilder.Append(formalArray[i].ToString());
                if (i != (formalArray.Length - 1))
                {
                    stringBuilder.Append(",");
                }
            }

            return stringBuilder.ToString();
        }

        #endregion
    }
}
