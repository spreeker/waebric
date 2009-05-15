package org.cwi.waebric.lexer;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.lexer.token.WaebricSymbol;
import org.cwi.waebric.lexer.token.WaebricToken;
import org.cwi.waebric.lexer.token.WaebricTokenIdentifier;

public class WaebricRecursiveDescentLexer {
	
	private int curr;
	private String data;
	private List<WaebricToken> tokens;
	
	public WaebricRecursiveDescentLexer() {
		tokens = new ArrayList<WaebricToken>();
	}
	
	public void tokenizeString(String input, int offset) {
		curr = offset;
		data = input;
		tokens.clear();

		// Start parsing
		try {
			program();
		} catch (LexerException e) {
			e.printStackTrace();
		}
	}
	
	public void program() throws LexerException {
		while(curr != data.length()-1) {
			if(data.startsWith(WaebricSymbol.LMULTI_COMMENT, curr) || data.startsWith(WaebricSymbol.SINGLE_COMMENT, curr)) {
				comments();
			} else if(isNextToken(WaebricSymbol.MODULE) || isNextToken(WaebricSymbol.IMPORT)) {
				module();
			} else if(isNextToken(WaebricSymbol.DEF)) {
				def();
			} else {
				throw new LexerException(data, curr);
			}
		}
	}
	
	public void comments() throws LexerException {
		WaebricToken comment = null;
		
		// Multiple line comments
		if(data.startsWith(WaebricSymbol.LMULTI_COMMENT, curr)) {
			int end = data.indexOf(WaebricSymbol.RMULTI_COMMENT, curr);
			if(end != -1) {
				comment = new WaebricToken(WaebricTokenIdentifier.COMMENT, 
						data.substring(curr, end), curr, end);
				curr = end;
			} else {
				comment = new WaebricToken(WaebricTokenIdentifier.COMMENT, 
						data, curr, data.length()-1);
				curr = data.length()-1;
			}
		}
		
		// Single line comments
		if(data.startsWith(WaebricSymbol.SINGLE_COMMENT, curr)) {
			int end = data.indexOf(WaebricSymbol.NEW_LINE, curr);
			if(end != -1) {
				comment = new WaebricToken(WaebricTokenIdentifier.COMMENT, 
						data.substring(curr, end), curr, end);
				curr = end;
			} else {
				comment = new WaebricToken(WaebricTokenIdentifier.COMMENT, 
						data, curr, data.length()-1);
				curr = data.length()-1;
			}
		}
		
		tokens.add(comment);
	}
	
	public void module() throws LexerException {
		WaebricToken module;
		
		if(isNextToken(WaebricSymbol.IMPORT)) {
			int end = data.indexOf(WaebricSymbol.SPACE, curr + WaebricSymbol.IMPORT.length() + 1);
			String identifier = data.substring(curr, end);
			if(isIdentifier(identifier)) {
				module = new WaebricToken(WaebricTokenIdentifier.IMPORT,
						WaebricSymbol.IMPORT + WaebricSymbol.SPACE + identifier, curr, end);
				curr = end + 1;
			} else {
				throw new LexerException(identifier, curr);
			}
		} else if(isNextToken(WaebricSymbol.SITE)) {
			int end = data.indexOf(WaebricSymbol.SPACE, curr + WaebricSymbol.SITE.length() + 1);
			String identifier = data.substring(curr, end);
			if(isIdentifier(identifier)) {
				module = new WaebricToken(WaebricTokenIdentifier.SITE,
						WaebricSymbol.SITE + WaebricSymbol.SPACE  + identifier, curr, end);
				curr = end + 1;
			} else {
				throw new LexerException(identifier, curr);
			}			
		} else if(isNextToken(WaebricSymbol.MODULE)) {
			int end = data.indexOf(WaebricSymbol.SPACE, curr + WaebricSymbol.MODULE.length() + 1);
			String identifier = data.substring(curr, end);
			if(isIdentifier(identifier)) {
				module = new WaebricToken(WaebricTokenIdentifier.MODULE,
						WaebricSymbol.MODULE + WaebricSymbol.SPACE  + identifier, curr, end);
				curr = end + 1;
			} else {
				throw new LexerException(identifier, curr);
			}
		}
	}
	
	public void def() {
		
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
	
	public boolean isNextToken(String symbol) {
		return data.startsWith(symbol + WaebricSymbol.SPACE, curr);
	}

}