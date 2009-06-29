using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Markup
{
    /// <summary>
    /// Node which contains Width_HeightAttribute
    /// </summary>
    public class Width_HeightAttribute : Attribute
    {
        #region Private Members

        private int Width;
        private int Height;

        #endregion

        #region Public Methods

        /// <summary>
        /// Empty constructor
        /// </summary>
        public Width_HeightAttribute()
        {
        }

        /// <summary>
        /// Constructor with width and height specification
        /// </summary>
        /// <param name="width">Width</param>
        /// <param name="height">Height</param>
        public Width_HeightAttribute(int width, int height)
        {
            Width = width;
            Height = height;
        }

        /// <summary>
        /// Get width of attribute
        /// </summary>
        /// <returns>Width</returns>
        public int GetWidth()
        {
            return Width;
        }

        /// <summary>
        /// Get height of attribute
        /// </summary>
        /// <returns>Height</returns>
        public int GetHeight()
        {
            return Height;
        }

        /// <summary>
        /// Set width of attribute
        /// </summary>
        /// <param name="width">Width</param>
        public void SetWidth(int width)
        {
            Width = width;
        }

        /// <summary>
        /// Set height of attribute
        /// </summary>
        /// <param name="height">Height</param>
        public void SetHeight(int height)
        {
            Height = height;
        }

        /// <summary>
        /// String representation of Width_HeightAttribute
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            return "@" + Width.ToString() + "%" + Height.ToString();
        }

        #endregion
    }
}
