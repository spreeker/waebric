package org.cwi.waebric.lexer.actions;

public class AttachSeperatorsAction implements ILexerAction {

	// Special symbols
	private final char SEMICOLON = ';';
	private final char EQUAL = '=';
	private final char SEPERATOR = ' ';
	
	private final int FINISHED = -1;

	public String execute(String text) {
		return attachSeparator(attachSeparator(text, EQUAL), SEMICOLON);
	}
	
	/**
	 * Read text character from right to left, so inserts in the buffer do not change
	 * the indices of successor symbols.
	 * 
	 * @param text
	 * @param symbol
	 * @return
	 */
	private String attachSeparator(String text, char symbol) {
		StringBuffer buffer = new StringBuffer(text);
		int index = text.lastIndexOf(symbol);
		
		while(index != FINISHED) {
			if(index < text.length()-1) {
				// Attach separator to right character
				char right = buffer.charAt(index+1);
				if(right != SEPERATOR) { buffer.insert(index+1, SEPERATOR); }
			}
			
			if(index > 0) {
				// Attach separator to left character
				char left = buffer.charAt(index-1);
				if(left != SEPERATOR) { buffer.insert(index, SEPERATOR); }
				
				// Retrieve index of successor symbol
				index = text.substring(0, index).lastIndexOf(symbol);
			} else {
				// Begin of text, finished processing
				index = FINISHED;
			}
		}
		
		return buffer.toString();
	}
	
}