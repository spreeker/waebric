using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Common
{
    /// <summary>
    /// Enumeration which contains all allowed XHTML tags
    /// Taglist from W3C HTML 4.01 / XHTML 1.0 Reference
    /// </summary>
    public enum XHTMLTags
    {
        //Basic tags
        HTML,
        BODY,
        H1,
        H2,
        H3,
        H4,
        H5,
        H6,
        P,
        BR,
        HR,

        //Char Format
        B,
        FONT,
        I,
        EM,
        BIG,
        STRONG,
        SMALL,
        SUP,
        SUB,
        BDO,
        U,

        //Output
        PRE,
        CODE,
        TT,
        KBD,
        VAR,
        DFN,
        SAMP,
        XMP,

        //Blocks
        ACRONYM,
        ABBR,
        ADDRESS,
        BLOCKQUOTE,
        CENTER,
        Q,
        CITE,
        INS,
        DEL,
        S,
        STRIKE,

        //Links
        A,
        LINK,

        //Frames
        FRAME,
        FRAMESET,
        NOFRAMES,
        IFRAME,

        //Input
        FORM,
        INPUT,
        TEXTAREA,
        BUTTON,
        SELECT,
        OPTGROUP,
        OPTION,
        LABEL,
        FIELDSET,
        LEGEND,
        ISINDEX,

        //Lists
        UL,
        OL,
        LI,
        DIR,
        DL,
        DT,
        DD,
        MENU,

        //Images
        IMG,
        MAP,
        AREA,

        //Tables
        TABLE,
        CAPTION,
        TH,
        TR,
        TD,
        THEAD,
        TBODY,
        TFOOT,
        COL,
        COLGROUP,

        //Styles
        STYLE,
        DIV,
        SPAN,

        //Meta Info
        HEAD,
        TITLE,
        META,
        BASE,
        BASEFONT,

        //Programming
        SCRIPT,
        NOSCRIPT,
        APPLET,
        OBJECT,
        PARAM
    }
}
