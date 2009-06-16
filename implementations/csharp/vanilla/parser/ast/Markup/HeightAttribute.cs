using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Markup
{
    /// <summary>
    /// Node which contains HeightAttribute
    /// </summary>
    public class HeightAttribute : Attribute
    {
        #region Private Members

        private int Height;

        #endregion

        #region Public Methods

        /// <summary>
        /// Empty constructor
        /// </summary>
        public HeightAttribute()
        {
        }

        /// <summary>
        /// Constructor with width and height specification
        /// </summary>
        /// <param name="height">Height</param>
        public HeightAttribute(int height)
        {
            Height = height;
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
            return "height:" + Height.ToString();
        }

        #endregion
    }
}
