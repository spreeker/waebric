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
        private bool IsTag;
        private bool Generated;

        #endregion

        #region Public Methods

        public XHTMLElement(String tag, XHTMLElement parent)
        {
            Parent = parent;
            Tag = tag;
            AttributeMap = new Dictionary<String,String>();
            Children = new List<XHTMLElement>();
            Content = "";
            IsTag = true;
            Generated = false;
        }

        public XHTMLElement(String tag, XHTMLElement parent, bool generated)
        {
            Parent = parent;
            Tag = tag;
            AttributeMap = new Dictionary<String, String>();
            Children = new List<XHTMLElement>();
            Content = "";
            IsTag = true;
            Generated = generated;
        }

        /// <summary>
        /// Add attribute to this element
        /// </summary>
        /// <param name="name">Name of attribute</param>
        /// <param name="value">Value of attribute</param>
        public void AddAttribute(String name, String value)
        {
            if (AttributeMap.ContainsKey(name))
            {   //Attribute already exists, so add new value after existing value
                
                //Get current value and create new value
                String tempValue = AttributeMap[name];
                tempValue += " " + value;

                //Remove current value
                AttributeMap.Remove(name);
                
                //Store new value
                AttributeMap.Add(name, tempValue);

                return;
            }

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
        /// Retrieve generated state of element
        /// </summary>
        /// <returns>IsGenerated</returns>
        public bool IsGenerated()
        {
            return Generated;
        }

        /// <summary>
        /// Retrieve tag state of element
        /// </summary>
        /// <returns>State</returns>
        public bool GetTagState()
        {
            return IsTag;
        }

        /// <summary>
        /// Set tag state of element
        /// True is tag
        /// False is no tag
        /// </summary>
        /// <param name="state">State</param>
        public void SetTagState(bool state)
        {
            IsTag = state;
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
