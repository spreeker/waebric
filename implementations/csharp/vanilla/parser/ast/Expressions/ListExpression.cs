﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Parser.Ast.Expressions
{
    /// <summary>
    /// Node which contains ListExpression
    /// [expression1,expression2]
    /// </summary>
    public class ListExpression : Expression
    {
        #region Private Members

        private NodeList ExpressionList;

        #endregion

        #region Public Methods

        public ListExpression()
        {
            //Create ExpressionList
            ExpressionList = new NodeList();
        }

        /// <summary>
        /// Add expression to ListExpression
        /// </summary>
        /// <param name="expression">Expression to add</param>
        public void AddExpression(Expression expression)
        {
            ExpressionList.Add(expression);
        }

        /// <summary>
        /// Get list of expressions
        /// </summary>
        /// <returns>ExpressionList</returns>
        public NodeList GetExpressions()
        {
            return ExpressionList;
        }

        /// <summary>
        /// Get string representation of ListExpression
        /// </summary>
        /// <returns>String</returns>
        public override String ToString()
        {
            //Build string with all items separated by comma
            StringBuilder expressions = new StringBuilder();
            Expression[] listArray = (Expression[]) ExpressionList.ToArray();
            for (int i = 0; i <= (listArray.Length - 1); i++)
            {
                expressions.Append(listArray[i].ToString());
                if (i != (listArray.Length - 1))
                {
                    expressions.Append(",");
                }
            }
            return "[" + expressions.ToString() + "]";
        }

        public override ISyntaxNode[] GetSubNodes()
        {
            return new ISyntaxNode[] { 
                ExpressionList
            };
        }

        public override void AcceptVisitor(ISyntaxNodeVisitor visitor)
        {
            visitor.Visit(this);
        }

        #endregion
    }
}
