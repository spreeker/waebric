using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Markup
{
    /// <summary>
    /// Node which contains NameAttribute
    /// </summary>
    public class NameAttribute : Attribute
    {
        #region Private Members

        private String Name;

        #endregion

        #region Public Methods

        /// <summary>
        /// Empty constructor
        /// </summary>
        public NameAttribute()
        {
        }

        /// <summary>
        /// Constructor with name specification
        /// </summary>
        /// <param name="classname">Class</param>
        public NameAttribute(String name)
        {
            Name = name;
        }

        /// <summary>
        /// Set Name
        /// </summary>
        /// <param name="classname">Name</param>
        public void SetName(String name)
        {
            Name = name;
        }

        /// <summary>
        /// Get Name
        /// </summary>
        /// <returns>Name</returns>
        public String GetName()
        {
            return Name;
        }

        /// <summary>
        /// Get String representation of Name
        /// </summary>
        /// <returns>Name</returns>
        public override String ToString()
        {
            return "$" + Name;
        }

        public override void AcceptVisitor(ISyntaxNodeVisitor visitor)
        {
            visitor.Visit(this);
        }

        public override ISyntaxNode[] GetSubNodes()
        {
            return new ISyntaxNode[] { };
        }

        #endregion

    }
}
