using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Interpreter
{
    /// <summary>
    /// Element which is part of tree
    /// </summary>
    public class XHTMLElement
    {
        #region Private Members

        private XHTMLElement Parent;
        private List<XHTMLElement> Children;
        private String Tag;
        private Dictionary<String, String> AttributeMap;
        private String Content;

        #endregion

        #region Public Methods

        public XHTMLElement(String tag, XHTMLElement parent)
        {
            Parent = parent;
            Tag = tag;
            AttributeMap = new Dictionary<String,String>();
            Children = new List<XHTMLElement>();
        }

        /// <summary>
        /// Add attribute to this element
        /// </summary>
        /// <param name="name">Name of attribute</param>
        /// <param name="value">Value of attribute</param>
        public void AddAttribute(String name, String value)
        {
            AttributeMap.Add(name, value);
        }

        /// <summary>
        /// Add child element to this element
        /// </summary>
        /// <param name="child">Child element</param>
        public void AddChild(XHTMLElement child)
        {
            Children.Add(child);
        }

        /// <summary>
        /// Adds content to XHTML element
        /// </summary>
        /// <param name="content">Content to add</param>
        public void AddContent(String content)
        {
            Content += content;
        }

        /// <summary>
        /// Get parent of element
        /// </summary>
        /// <returns>Parent XHTMLElement</returns>
        public XHTMLElement GetParent()
        {
            return Parent;
        }

        /// <summary>
        /// Get tag of XHTMLElement
        /// </summary>
        /// <returns>Tag</returns>
        public String GetTag()
        {
            return Tag;
        }

        /// <summary>
        /// Get attributes of XHTMLElement
        /// </summary>
        /// <returns>AttributeMap</returns>
        public Dictionary<String, String> GetAttributes()
        {
            return AttributeMap;
        }

        /// <summary>
        /// Get content of XHTMLElement
        /// </summary>
        /// <returns>Content</returns>
        public String GetContent()
        {
            return Content;
        }

        /// <summary>
        /// Get child elements
        /// </summary>
        /// <returns>Children</returns>
        public List<XHTMLElement> GetChildren()
        {
            return Children;
        }

        #endregion

        #region Private Methods

        #endregion
    }
}
