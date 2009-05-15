package org.cwi.waebric.lexer;

import java.util.StringTokenizer;

public class WaebricTokenizer {
	
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
	
	public WaebricToken[] tokenizeInput(String input, int offset) {
		String[] data = separateWords(input);
		WaebricToken[] token = new WaebricToken[data.length - offset];
		
		for(int i = offset; i < data.length; i++) {
			try {
				token[i] = getToken(data, i);
				System.out.println("[" + i + "]" + token[i].toString());
			} catch (LexerException e) {
				e.printStackTrace();
			}
		}
		
		return token;
	}
	
	public WaebricToken getToken(String[] data, int index) throws LexerException {
		String word = data[index];
		
		if(word.equals("{")) { 
			return new WaebricToken(WaebricTokenType.LBRACKET, word);
		}
		
		else if(word.equals("}")) { 
			return new WaebricToken(WaebricTokenType.RCBRACKET, word);
		}
		
		else if(word.equals("(")) { 
			return new WaebricToken(WaebricTokenType.LBRACKET, word);
		}
		
		else if(word.equals(")")) { 
			return new WaebricToken(WaebricTokenType.RBRACKET, word);
		}
		
		else if(word.equals(";")) { 
			return new WaebricToken(WaebricTokenType.SEMICOLON, word);
		}
		
		else if(word.equals("module") && isIdentifier(data[index+1])) { 
			return new WaebricToken(WaebricTokenType.MODULE, word);
		}
		
		else if(word.equals("end")) { 
			return new WaebricToken(WaebricTokenType.END, word);
		}
		
		else if(word.equals("def")) { 
			return new WaebricToken(WaebricTokenType.DEF, word);
		}
		
		else if(word.equals("html")) { 
			return new WaebricToken(WaebricTokenType.HTML, word);
		}
		
		else if(word.equals("head")) { 
			return new WaebricToken(WaebricTokenType.HEAD, word);
		}
		
		else if(word.equals("body")) { 
			return new WaebricToken(WaebricTokenType.BODY, word);
		}
		
		else if(word.equals("title")) { 
			return new WaebricToken(WaebricTokenType.TITLE, word);
		}
		
		else if(word.equals("p")) { 
			return new WaebricToken(WaebricTokenType.P, word);
		}
		
		else if(isNumber(word)) { 
			return new WaebricToken(WaebricTokenType.NATCON, word);
		}
		
		else if(isText(word)) { 
			return new WaebricToken(WaebricTokenType.TEXT, word);
		}
		
		else if(isIdentifier(word)) { 
			if(index > 0) {
				WaebricTokenType precessor = getToken(data, index-1).getToken();
				
				if(precessor.equals(WaebricTokenType.MODULE) || precessor.equals(WaebricTokenType.DEF)) { 
					return new WaebricToken(WaebricTokenType.IDCON, word);
				}
			}
		}
		
		// Unprocessed
		throw new LexerException(word, index);
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