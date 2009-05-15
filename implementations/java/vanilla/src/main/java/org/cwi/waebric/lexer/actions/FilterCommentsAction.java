package org.cwi.waebric.lexer.actions;

public class FilterCommentsAction implements ILexerAction {

	// Single-line comment symbols
	public final String SLC_START = "//";
	public final String SLC_END = "\n";
	
	// Multi-line comment symbols
	public final String MLC_START = "/*";
	public final String MLC_END = "*/";

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
			String remainder = removeStringAt(text, start, end + SLC_END.length());
			return execute(remainder);
		}
		
		// Filter multi-line comments
		start = text.indexOf(MLC_START);
		if(start != -1) {
			int end = text.indexOf(MLC_END, start);
			String remainder = removeStringAt(text, start, end + MLC_END.length());
			return execute(remainder);
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
		String resultString = "";
		
		if(beginIndex > 0) {
			String left = text.substring(0, beginIndex-1);
			resultString += left;
		}
		
		if(endIndex < text.length()-1) {
			String right = text.substring(endIndex, text.length());
			resultString += right;
		}
		
		return resultString;
	}
	
}
