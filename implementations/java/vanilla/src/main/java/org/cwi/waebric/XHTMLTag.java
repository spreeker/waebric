package org.cwi.waebric;

/**
 * http://www.w3schools.com/tags/default.asp
 * @author Jeroen van Schagen
 * @date 11-06-2009
 */
public enum XHTMLTag {

	A, 
	ABBR, 
	ACRONYM, 
	ADDRESS, 
	
	@Deprecated
	APPLET, 
	
	AREA, 
	B, 
	BASE,
	
	@Deprecated
	BASEFONT,
	
	BDO, 
	BIG, 
	BLOCKQUOTE, 
	BODY, 
	BR, 
	BUTTON,
	CAPTION, 
	
	@Deprecated
	CENTER, 
	
	CITE, 
	CODE, 
	COL, 
	COLGROUP,
	DD, 
	DEL,
	
	@Deprecated
	DIR, 
	
	DIV, 
	DFN, 
	DL, 
	DT, 
	EM, 
	FIELDSET, 
	
	@Deprecated
	FONT, 
	
	FORM,
	FRAME,
	FRAMESET, 
	H1, H2, H3, H4, H5, H6, 
	HEAD, 
	HR,
	
	/**
	 * Defines a HTML document, should be placed at top.
	 */
	HTML, 
	
	I, 
	IFRAME, 
	IMG, 
	INPUT, 
	INS, 
	
	@Deprecated
	ISINDEX, 
	
	KBD,
	LABEL, 
	LEGEND, 
	LI, 
	LINK, 
	MAP, 
	
	@Deprecated
	MENU, 
	
	META, 
	NOFRAMES,
	NOSCRIPT, 
	OBJECT, 
	OL, 
	OPTGROUP,
	OPTION, 
	P, 
	PARAM,
	PRE, 
	Q, 
	
	@Deprecated
	S, 
	
	SAMP, 
	SCRIPT, 
	SELECT,
	SMALL, 
	SPAN,
	
	@Deprecated
	STRIKE,
	
	STRONG, 
	STYLE, 
	SUB, 
	SUP, 
	TABLE,
	TBODY,
	TD,
	TEXTAREA,
	TFOOT,
	TH,
	THEAD,
	TITLE,
	TR,
	TT,
	
	@Deprecated
	U,
	
	UL, 
	VAR, 
	
	@Deprecated
	XMP;
	
	public static boolean isXHTMLTag(String data) {
		try {
			// Literal should be in enumeration
			XHTMLTag tag = XHTMLTag.valueOf(data.toUpperCase());
			return tag != null;
		} catch(IllegalArgumentException e) {
			// Enumeration does not exists
			return false;
		}
	}
	
}