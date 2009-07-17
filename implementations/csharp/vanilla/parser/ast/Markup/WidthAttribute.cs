using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Markup
{
    /// <summary>
    /// Node which contains WidthAttribute
    /// </summary>
    public class WidthAttribute : Attribute
    {
        #region Private Members

        private int Width;

        #endregion

        #region Public Methods

        /// <summary>
        /// Empty constructor
        /// </summary>
        public WidthAttribute()
        {
        }

        /// <summary>
        /// Constructor with width specification
        /// </summary>
        /// <param name="height">Width</param>
        public WidthAttribute(int width)
        {
            Width = width;
        }

        /// <summary>
        /// Get width of attribute
        /// </summary>
        /// <returns>Height</returns>
        public int GetWidth()
        {
            return Width;
        }

        /// <summary>
        /// Set width of attribute
        /// </summary>
        /// <param name="height">Width</param>
        public void SetWidth(int width)
        {
            Width = width;
        }

        /// <summary>
        /// String representation of Width_HeightAttribute
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            return "@" + Width.ToString();
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
