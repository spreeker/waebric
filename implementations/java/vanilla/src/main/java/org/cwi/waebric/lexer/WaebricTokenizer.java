package org.cwi.waebric.lexer;

public class WaebricTokenizer {
	
	public WaebricToken[] tokenizeInput(String[] input, int offset) {
		WaebricToken[] tokens = new WaebricToken[input.length - offset];
		
		for(int i = offset; i < input.length; i++) {
			String data = input[i];
			if(data.equals("{")) tokens[i] = WaebricToken.LBRACKET;
			else if(data.equals("}")) tokens[i] = WaebricToken.RBRACKET;
			else if(data.equals("module") && isIdentifier(input[i+1])) tokens[i] = WaebricToken.MODULE;
			else if(data.equals("end")) tokens[i] = WaebricToken.END;
			else if(isIdentifier(data)) tokens[i] = WaebricToken.IDENTIFIER;
			else if(isNumber(data)) tokens[i] = WaebricToken.NUMERAL;
		}
		
		return tokens;
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

		// All characters in a number should be digits
		for(char c : text.toCharArray()) {
			if(!isDigit(c)) { return false; }
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