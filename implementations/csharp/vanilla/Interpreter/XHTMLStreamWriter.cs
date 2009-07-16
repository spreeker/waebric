using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Web.UI;
using System.IO;

namespace Interpreter
{
    /// <summary>
    /// Class which provides facilities for writing XHTML to a stream
    /// </summary>
    public class XHTMLStreamWriter
    {
        #region Private Members

        private XhtmlTextWriter XhtmlWriter;
        private Dictionary<String, String> AttributeMap;

        #endregion

        #region Public Methods

        public XHTMLStreamWriter(TextWriter writer)
        {
            XhtmlWriter = new XhtmlTextWriter(writer);
            AttributeMap = new Dictionary<String, String>();
        }
        
        /// <summary>
        /// Add the specified attribute to an buffer before WriteTag has been called.
        /// </summary>
        /// <param name="name">Name of attribute</param>
        /// <param name="value">Value of attribute</param>
        public void AddAttribute(String name, String value)
        {
            AttributeMap.Add(name, value);
        }

        /// <summary>
        /// Write specified tag to stream, including added attributes.
        /// When it is an empty tag like br, closing is also performed, otherwise CloseTag should be called.
        /// </summary>
        /// <param name="tag">Tag to write</param>
        public void WriteTag(String tag)
        {
            //Write tag
            XhtmlWriter.WriteBeginTag(tag);

            //Write attributes
            foreach (KeyValuePair<String, String> pair in AttributeMap)
            {
                XhtmlWriter.WriteAttribute(pair.Key, pair.Value);
            }
            AttributeMap.Clear();

            //Determine closing type
            if (IsEmptyElement(tag))
            {   //Use /> closing
                XhtmlWriter.Write(HtmlTextWriter.SelfClosingTagEnd);
            }
            else
            {   //Use > closing
                XhtmlWriter.Write(HtmlTextWriter.TagRightChar);
            }

            //\n to make layout better
            XhtmlWriter.WriteLine();

            //Flush XHTML writer buffer
            XhtmlWriter.Flush();
        }

        /// <summary>
        /// Close specified tag, emptytags are ignored
        /// </summary>
        /// <param name="tag">Tag to close</param>
        public void CloseTag(String tag)
        {
            if (!IsEmptyElement(tag))
            {
                XhtmlWriter.WriteEndTag(tag);
                XhtmlWriter.WriteLine();
                XhtmlWriter.Flush();
            }
        }

        #endregion

        #region Private Methods

        /// <summary>
        /// Method which determines if the tag needs alternative closing, like <img />, <br />, <hr />
        /// </summary>
        /// <param name="tag">Tag to check</param>
        /// <returns>True if empty element, otherwise false</returns>
        private bool IsEmptyElement(String tag)
        {
            String[] xhtmlEmptyTags = Enum.GetNames(typeof(EmptyXHTMLElement));
            foreach (String item in xhtmlEmptyTags)
            {
                if (item.Equals(tag.ToUpper()))
                {
                    return true;
                }
            }
            return false;
        }

        /// <summary>
        /// Enumeration which contains empty XHTML elements
        /// </summary>
        private enum EmptyXHTMLElement
        {
            AREA,
            BASE,
            BASEFONT,
            BR,
            FRAME,
            HR,
            IMG,
            INPUT,
            LINK,
            META
        };

        #endregion
    }
}
