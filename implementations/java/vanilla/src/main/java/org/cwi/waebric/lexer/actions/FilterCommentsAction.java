package org.cwi.waebric.lexer.actions;

public class FilterCommentsAction implements ILexerAction {

	// Single-line comment symbols
	private final String SLC_START = "//";
	private final String SLC_END = "/n";
	
	// Multi-line comment symbols
	private final String MLC_START = "/*";
	private final String MLC_END = "*/";

	/**
	 * Filter comments
	 * 
	 * @param text Text that may contain comments
	 * @return Comments free text
	 */
	public String execute(String text) {
		int start = text.indexOf(SLC_START);
		
		// Filter single-line comments
		if(start != -1) {
			int end = text.indexOf(SLC_END, start);
			return execute(removeStringAt(text, start, end));
		}
		
		// Filter multi-line comments
		start = text.indexOf(MLC_START);
		if(start != -1) {
			int end = text.indexOf(MLC_END, start);
			return execute(removeStringAt(text, start, end));
		}
		
		// No comments have been found
		return text;
	}
	
	/**
	 * Remove substring from text.<br><br>
	 * 
	 * For example:<br>
	 * <code>
	 * 	removeStringAt("well hello there", 6, 11) = "well there"
	 * </code>
	 * 
	 * @param text Text that has to be simplified
	 * @param beginIndex Begin index of substring
	 * @param endIndex End index of substring
	 * @return Text without substring
	 */
	public String removeStringAt(String text, int beginIndex, int endIndex) {
		String subString = "";
		
		if(beginIndex > 0) {
			subString += text.substring(0, beginIndex-1);
		}
		
		if(endIndex < text.length()) {
			subString += text.substring(endIndex+1, text.length());
		}
		
		return subString;
	}
	
}
