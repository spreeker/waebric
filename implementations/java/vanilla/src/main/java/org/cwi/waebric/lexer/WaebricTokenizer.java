package org.cwi.waebric.lexer;

import java.util.StringTokenizer;

public class WaebricTokenizer {
	
	public WaebricToken[] tokenizeInput(String input, int offset) {
		String[] data = separateWords(input);
		WaebricToken[] tokens = new WaebricToken[data.length - offset];
		
		for(int i = offset; i < data.length; i++) {
			String word = data[i];
			if(word.equals("{")) tokens[i] = WaebricToken.LCBRACKET;
			else if(word.equals("}")) tokens[i] = WaebricToken.RCBRACKET;
			else if(word.equals("(")) tokens[i] = WaebricToken.LBRACKET;
			else if(word.equals(")")) tokens[i] = WaebricToken.RBRACKET;
			else if(word.equals("module") && isIdentifier(data[i+1])) tokens[i] = WaebricToken.MODULE;
			else if(word.equals("end")) tokens[i] = WaebricToken.END;
			else if(isIdentifier(word)) tokens[i] = WaebricToken.IDCON;
			else if(isNumber(word)) tokens[i] = WaebricToken.NATCON;
			else if(isText(word)) tokens[i] = WaebricToken.TEXT;
			else tokens[i] = WaebricToken.UNKNOWN;
		}
		
		return tokens;
	}
	
	/**
	 * Separate string into words.
	 * 
	 * @see java.util.StringTokenizer
	 * @param input
	 * @return words
	 */
	public String[] separateWords(String input) {
		StringTokenizer separator = new StringTokenizer(input);
		String[] tokens =  new String[separator.countTokens()];
		for(int i = 0; separator.hasMoreElements(); i++) {
			tokens[i] = separator.nextToken();
		}
		
		return tokens;
	}
	
	public boolean isText(String text) {
		// Text should start and end with double quotes
		return text.startsWith("\"") && text.endsWith("\"");
	}
	
	public boolean isIdentifier(String text) {
		// Identifiers should have a size of 1+
		if(text == null || text.equals("")) { return false; }
		char[] chars = text.toCharArray();
		
		// The first char of an identifier should be a letter
		if(!isLetter(chars[0])) { return false; }
		
		// All characters in an identifier should be letters or digits
		for(char c : chars) {
			if(!(isLetter(c) || !isDigit(c))) { return false; }
		}
		
		return true;
	}
	
	public boolean isNumber(String text) {
		// Numbers should have a size of 1+
		if(text == null || text.equals("")) { return false; }

		// All characters in a number should be digits, or a . for decimals
		for(char c : text.toCharArray()) {
			if(!isDigit(c) || c == '.') { return false; }
		}
		
		return true;
	}
	
	public boolean isLetter(char c) {
		return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
	}
	
	public boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}
	
}