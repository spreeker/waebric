using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Parser.Ast.Expressions;

namespace Parser.Ast.Markup
{
    /// <summary>
    /// Node containing an argument
    /// </summary>
    public class Argument : ISyntaxNode
    {
        #region Private Members

        private Expression ArgumentExpression;
        private String Identifier;

        #endregion

        #region Public Methods

        #endregion
    }
}
