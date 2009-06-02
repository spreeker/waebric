using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections;

namespace Parser.Ast
{
    public class NodeList
    {
        private ArrayList<ISyntaxNode> list = new ArrayList();

        /// <summary>
        /// Get size of NodeList
        /// </summary>
        /// <returns>Size</returns>
        public int GetSize()
        {
            return list.Count;
        }
    }
}
