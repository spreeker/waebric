package org.cwi.waebric.lexer.filter;

public class AttachSeperatorsAction implements ILexerAction {

	private final String[] SPECIAL_SYMBOLS = { ";", "=" };
	private final char SEPERATOR = ' ';
	
	@Override
	public String execute(String text) {
		StringBuffer buffer = new StringBuffer(text);
		
		for(String symbol : SPECIAL_SYMBOLS) {
			int index = text.indexOf(symbol);
			while(index != -1) {
				if(index > 0 && index < text.length()) {
					char left = text.charAt(index-1);
					if(left != SEPERATOR) { buffer.insert(index-1, SEPERATOR); }
					char right = text.charAt(index+1);
					if(right != SEPERATOR) { buffer.insert(index+1, SEPERATOR); }
				}
			}
		}
			
		return buffer.toString();
	}
	
}