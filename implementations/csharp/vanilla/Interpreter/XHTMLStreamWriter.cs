using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Web.UI;
using System.IO;
using Common;

namespace Interpreter
{
    /// <summary>
    /// Class which provides facilities for writing XHTML to a stream
    /// </summary>
    public class XHTMLStreamWriter
    {
        #region Private Members

        private XHTMLElement Root;
        private XhtmlTextWriter XhtmlWriter;

        #endregion

        #region Public Methods

        /// <summary>
        /// Enumeration containing XHTML 1.0 DocTypes
        /// </summary>
        public enum DocType {
            STRICT,
            TRANSITIONAL,
            FRAMESET
        };

        /// <summary>
        /// Create an XHTMStreamWriter which writes to specified writer in specified XHTML DocType
        /// </summary>
        /// <param name="writer">Writer to write to</param>
        /// <param name="docType">DocType of XHTML document</param>
        public XHTMLStreamWriter(TextWriter writer, DocType docType, XHTMLElement tree)
        {
            XhtmlWriter = new XhtmlTextWriter(writer, "    ");
            Root = tree;

            //Write doctype before starting document
            WriteDocType(docType);
        }

        /// <summary>
        /// Writes the complete tree to an HTML file
        /// </summary>
        public void WriteStream()
        {
            Visit(Root, 0);
        }

        /// <summary>
        /// Visit XHTMLElement and write it to stream
        /// </summary>
        /// <param name="element">Element to visit</param>
        /// <param name="level">Level of indenting</param>
        public void Visit(XHTMLElement element, int level)
        {
            if (element.GetTag() == "cdata")
            {   //CData need different handling
                WriteCData(element);
            }
            else if (element.GetTag() == "comment")
            {   //Write comment
                WriteComment(element);
            }
            else
            {   //Normal XHTML tag handling
                
                XhtmlWriter.Indent = level;
                XhtmlWriter.BeginRender();

                //Check if element is tag, if not write tag, otherwise handle as XHTML tag
                if (!element.GetTagState())
                {
                    XhtmlWriter.BeginRender();
                    
                    //Get escape chars out of this tag
                    CharIterator charIterator = new CharIterator();
                    String tag = charIterator.ParseText(element.GetTag());
                    XhtmlWriter.Write(tag);
                    
                    XhtmlWriter.EndRender();
                    XhtmlWriter.Flush();
                    return;
                }

                XhtmlWriter.WriteBeginTag(element.GetTag());
                foreach (KeyValuePair<String, String> pair in element.GetAttributes())
                {
                    XhtmlWriter.WriteAttribute(pair.Key, pair.Value, false);
                }

                if (IsEmptyElement(element.GetTag()))
                {   //Nothing inside element, so write tag end
                    XhtmlWriter.Write(XhtmlTextWriter.SelfClosingTagEnd);
                    XhtmlWriter.WriteLine();
                }
                else if (IsXHTMLTag(element.GetTag()))
                {
                    //Write tag opening closing
                    XhtmlWriter.Write(XhtmlTextWriter.TagRightChar);

                    //Write content
                    XhtmlWriter.Write(element.GetContent());
                    if (element.GetChildren().Count != 0)
                    {
                        XhtmlWriter.WriteLine();
                    }

                    //Visit children
                    foreach (XHTMLElement child in element.GetChildren())
                    {
                        Visit(child, level + 1);
                    }

                    //Write closing tag
                    XhtmlWriter.WriteEndTag(element.GetTag());
                    XhtmlWriter.WriteLine();
                    XhtmlWriter.Flush();
                }
                else
                {
                    //Just write it
                    XhtmlWriter.Write(element.GetTag());
                    XhtmlWriter.EndRender();
                    XhtmlWriter.Flush();
                }
            }
        }
        /// <summary>
        /// Write CData
        /// </summary>
        /// <param name="element">Element containing CData</param>
        public void WriteCData(XHTMLElement element)
        {
            XhtmlWriter.BeginRender();
            XhtmlWriter.Write("<![CDATA[" + element.GetContent() + "]]>");
            XhtmlWriter.WriteLine();
            XhtmlWriter.EndRender();
            XhtmlWriter.Flush();
        }

        /// <summary>
        /// Write comment <!-- this is a comment -->
        /// </summary>
        /// <param name="element">Element containing element</param>
        public void WriteComment(XHTMLElement element)
        {
            //Write comment open tag
            XhtmlWriter.BeginRender();
            XhtmlWriter.Write("<!--");

            //Lets parse the text, because the XhtmlWriter handles layout chars incorrectly           
            CharIterator charIterator = new CharIterator();
            String content = charIterator.ParseText(element.GetContent());
            XhtmlWriter.Write(content);

            //Close comment tag
            XhtmlWriter.Write("-->");
            XhtmlWriter.WriteLine();
            XhtmlWriter.EndRender();
            XhtmlWriter.Flush();
        }

        #endregion

        #region Private Methods

        /// <summary>
        /// Method which writes doctype to stream
        /// </summary>
        /// <param name="type">DocType of XHTML document</param>
        private void WriteDocType(DocType type)
        {
            XhtmlWriter.BeginRender();
            switch (type)
            {
                case DocType.STRICT:
                    XhtmlWriter.Write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
                    break;
                case DocType.TRANSITIONAL:
                    XhtmlWriter.Write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
                    break;
                case DocType.FRAMESET:
                    XhtmlWriter.Write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Frameset//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd\">");
                    break;
            }
            XhtmlWriter.WriteLine();
            XhtmlWriter.EndRender();
            XhtmlWriter.Flush();
        }

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
        /// Detect if this tag is an valid XHTML tag
        /// </summary>
        /// <param name="tag">Tag to check</param>
        /// <returns>IsXHTMLTag</returns>
        private bool IsXHTMLTag(String tag)
        {
            String[] xhtmlTags = Enum.GetNames(typeof(XHTMLTags));
            foreach (String item in xhtmlTags)
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
