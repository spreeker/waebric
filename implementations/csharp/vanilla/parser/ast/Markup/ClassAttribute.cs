using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Markup
{
    /// <summary>
    /// Node which contains ClassAttribute
    /// </summary>
    public class ClassAttribute : Attribute
    {
        #region Private Members

        private String Class;

        #endregion

        #region Public Methods

        /// <summary>
        /// Empty constructor
        /// </summary>
        public ClassAttribute()
        {
        }

        /// <summary>
        /// Constructor with class specification
        /// </summary>
        /// <param name="classname">Class</param>
        public ClassAttribute(String classname)
        {
            Class = classname;
        }

        /// <summary>
        /// Set Class
        /// </summary>
        /// <param name="classname">Class</param>
        public void SetClass(String classname)
        {
            Class = classname;
        }

        /// <summary>
        /// Get Class
        /// </summary>
        /// <returns>Class</returns>
        public String GetClass()
        {
            return Class;
        }

        /// <summary>
        /// Get String representation of Class
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            return Class;
        }

        #endregion

    }
}
