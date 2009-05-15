package org.cwi.waebric.lexer;

import java.util.StringTokenizer;

import org.cwi.waebric.lexer.token.WaebricToken;
import org.cwi.waebric.lexer.token.WaebricTokenIdentifier;

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
			return new WaebricToken(WaebricTokenIdentifier.LBRACKET, word);
		}
		
		else if(word.equals("}")) { 
			return new WaebricToken(WaebricTokenIdentifier.RCBRACKET, word);
		}
		
		else if(word.equals("(")) { 
			return new WaebricToken(WaebricTokenIdentifier.LBRACKET, word);
		}
		
		else if(word.equals(")")) { 
			return new WaebricToken(WaebricTokenIdentifier.RBRACKET, word);
		}
		
		else if(word.equals(";")) { 
			return new WaebricToken(WaebricTokenIdentifier.SEMICOLON, word);
		}
		
		else if(word.equals("module") && isIdentifier(data[index+1])) { 
			return new WaebricToken(WaebricTokenIdentifier.MODULE, word);
		}
		
		else if(word.equals("end")) { 
			return new WaebricToken(WaebricTokenIdentifier.END, word);
		}
		
		else if(word.equals("def")) { 
			return new WaebricToken(WaebricTokenIdentifier.DEF, word);
		}
		
		else if(word.equals("html")) { 
			return new WaebricToken(WaebricTokenIdentifier.HTML, word);
		}
		
		else if(word.equals("head")) { 
			return new WaebricToken(WaebricTokenIdentifier.HEAD, word);
		}
		
		else if(word.equals("body")) { 
			return new WaebricToken(WaebricTokenIdentifier.BODY, word);
		}
		
		else if(word.equals("title")) { 
			return new WaebricToken(WaebricTokenIdentifier.TITLE, word);
		}
		
		else if(word.equals("p")) { 
			return new WaebricToken(WaebricTokenIdentifier.P, word);
		}
		
		else if(isNumber(word)) { 
			return new WaebricToken(WaebricTokenIdentifier.NATCON, word);
		}
		
		else if(isText(word)) { 
			return new WaebricToken(WaebricTokenIdentifier.TEXT, word);
		}
		
		else if(isIdentifier(word)) { 
			if(index > 0) {
				WaebricTokenIdentifier precessor = getToken(data, index-1).getToken();
				
				if(precessor.equals(WaebricTokenIdentifier.MODULE) || precessor.equals(WaebricTokenIdentifier.DEF)) { 
					return new WaebricToken(WaebricTokenIdentifier.IDCON, word);
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