using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Parser.Ast.Markup;

namespace Parser.Ast.Site
{
    /// <summary>
    /// Node which contains a mapping
    /// </summary>
    public class Mapping
    {
        #region Private Members

        private Path Path; //Path of mapping
        private Markup.Markup Markup; //Markup of mapping

        #endregion

        #region Public Methods

        public Mapping()
        {
            //Create containers
            Path = new Path();
            Markup = new Markup.Markup();
        }

        /// <summary>
        /// Path to set
        /// </summary>
        /// <param name="path">Path of mapping</param>
        public void SetPath(Path path)
        {
            Path = path;
        }

        /// <summary>
        /// Get path of mapping
        /// </summary>
        /// <returns>Path</returns>
        public Path GetPath()
        {
            return Path;
        }

        /// <summary>
        /// Set markup of mapping
        /// </summary>
        /// <param name="markup">Markup of mapping</param>
        public void SetMarkup(Markup.Markup markup)
        {
            Markup = markup;
        }

        /// <summary>
        /// Get markup of mapping
        /// </summary>
        /// <returns>Markup</returns>
        public Markup.Markup GetMarkup()
        {
            return Markup;
        }

        #endregion
    }
}
