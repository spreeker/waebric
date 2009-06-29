using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Markup
{
    /// <summary>
    /// Node which contains TypeAttribute
    /// </summary>
    public class TypeAttribute : Attribute
    {
        #region Private Members

        private String Type;

        #endregion

        #region Public Methods

        /// <summary>
        /// Empty constructor
        /// </summary>
        public TypeAttribute()
        {
        }

        /// <summary>
        /// Constructor with type specification
        /// </summary>
        /// <param name="classname">Type</param>
        public TypeAttribute(String type)
        {
            Type = type;
        }

        /// <summary>
        /// Set Type
        /// </summary>
        /// <param name="classname">Type</param>
        public void SetType(String type)
        {
            Type = type;
        }

        /// <summary>
        /// Get Type
        /// </summary>
        /// <returns>Type</returns>
        public new String GetType()
        {
            return Type;
        }

        /// <summary>
        /// Get String representation of Type
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            return ":" + Type;
        }

        #endregion

    }
}
