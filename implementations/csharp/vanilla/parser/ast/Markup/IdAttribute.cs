using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Markup
{
    /// <summary>
    /// Node which contains an IdAttribute
    /// </summary>
    public class IdAttribute : Attribute
    {
        #region Private Members

        private String Id;

        #endregion

        #region Public Members

        /// <summary>
        /// Constructor with Id specification
        /// </summary>
        /// <param name="id">Id</param>
        public IdAttribute(String id)
        {
            Id = id;
        }

        /// <summary>
        /// Empty constructor
        /// </summary>
        public IdAttribute()
        {
        }

        /// <summary>
        /// Get Id
        /// </summary>
        /// <returns>Id</returns>
        public String GetId()
        {
            return Id;
        }

        /// <summary>
        /// Set Id
        /// </summary>
        /// <param name="id">Id</param>
        public void SetId(String id)
        {
            Id = id;
        }

        /// <summary>
        /// Return Id as string
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            return Id;
        }

        #endregion
    }
}
