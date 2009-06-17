using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Site
{
    /// <summary>
    /// Node which holds a PathElement
    /// </summary>
    public class PathElement : ISyntaxNode
    {
        #region Private Members

        String Element;

        #endregion

        #region Public Methods

        /// <summary>
        /// Empty constructor
        /// </summary>
        public PathElement()
        {
        }

        /// <summary>
        /// Constructor with pathelement
        /// </summary>
        /// <param name="pathElement">Pathelement</param>
        public PathElement(String pathElement)
        {
            Element = pathElement;
        }

        /// <summary>
        /// Set path element
        /// </summary>
        /// <param name="pathElement">Path element to set</param>
        public void SetPathElement(String pathElement)
        {
            Element = pathElement;
        }

        /// <summary>
        /// Get path element
        /// </summary>
        /// <returns>Path element</returns>
        public String GetPathElement()
        {
            return Element;
        }

        /// <summary>
        /// Get path element as string
        /// </summary>
        /// <returns>String</returns>
        public String ToString()
        {
            return Element;
        }

        #endregion
    }
}
