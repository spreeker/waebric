package org.cwi.waebric.scanner;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.WaebricLayout;
import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.scanner.exception.ScannerException;
import org.cwi.waebric.scanner.exception.UnknownTokenException;
import org.cwi.waebric.scanner.token.Token;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.cwi.waebric.scanner.token.TokenSort;

/**
 * The lexical analyzer, also known as a scanner, reads an input character stream
 * from which it generates a stream of tokens. During this "tokenization" process
 * layout and comments characters are removed to simplify and optimize parsing.
 * 
 * @author Jeroen van Schagen
 * @date 18-05-2009
 */
public class WaebricScanner implements Iterable<Token> {

	private StreamTokenizer tokenizer;
	private List<Token> tokens;
	
	/**
	 * Initialize scanner
	 * 
	 * @param reader Input character stream
	 */
	public WaebricScanner(Reader reader) {
		tokenizer = new StreamTokenizer(reader);
		tokens = new ArrayList<Token>();
	}
	
	/**
	 * Convert character stream in token stream
	 * 
	 * @return List of scanner exceptions
	 * @throws IOException Fired by next token procedure in stream tokenizer.
	 * @see java.io.StreamTokenizer
	 */
	public List<ScannerException> tokenizeStream() throws IOException {
		List<ScannerException> exceptions = new ArrayList<ScannerException>();
		
		tokenizer.whitespaceChars(WaebricLayout.NEW_LINE, WaebricLayout.NEW_LINE);
		tokenizer.whitespaceChars(WaebricLayout.TAB, WaebricLayout.TAB);
		
		// Scan and store tokens
		tokens.clear();
		int curr = tokenizer.nextToken();
		while(curr != StreamTokenizer.TT_EOF) {
			switch(curr) {
				case StreamTokenizer.TT_NUMBER:
					tokens.add(new Token(tokenizer.nval, TokenSort.NUMBER, tokenizer.lineno()));
				break;
				case StreamTokenizer.TT_WORD:
					if(isKeyword(tokenizer.sval)) { // Waebric keyword
						WaebricKeyword literal = WaebricKeyword.valueOf(tokenizer.sval.toUpperCase());
						tokens.add(new Token(literal, TokenSort.KEYWORD, tokenizer.lineno()));
					} else if(isIdentifier(tokenizer.sval)) { // Identifier
						tokens.add(new Token(tokenizer.sval, TokenSort.IDENTIFIER, tokenizer.lineno()));
					} else { // Unknown word
						exceptions.add(new UnknownTokenException(tokenizer.sval, tokenizer.lineno()));
					}
				break;
				case StreamTokenizer.TT_EOF: break;
				case StreamTokenizer.TT_EOL: break;
				default:
					char c = (char) curr;
					if(c == WaebricSymbol.DQUOTE) { // Text
						tokens.add(new Token(tokenizer.sval, TokenSort.TEXT, tokenizer.lineno()));
					} else if(isSymbol(c)) { // Symbol
						tokens.add(new Token(c, TokenSort.SYMBOL, tokenizer.lineno()));
					} else { // Unknown character
						exceptions.add(new UnknownTokenException(c, tokenizer.lineno()));
					}
				break;
			}

			// Read next token
			curr = tokenizer.nextToken();
		}
		
		// Report exceptions
		return exceptions;
	}
	
	/**
	 * Retrieve token
	 * 
	 * @param index Token index in structured text
	 * @return token
	 */
	public Token getToken(int index) {
		return tokens.get(index);
	}
	
	/**
	 * Retrieve amount of tokens
	 * 
	 * @return size
	 */
	public int getSize() {
		return tokens.size();
	}
	
	/**
	 * Retrieve token iterator
	 * 
	 * @return iterator
	 */
	public TokenIterator iterator() {
		return new TokenIterator(tokens);
	}
	
	/**
	 * Determine whether a certain text fragment is a literal.
	 * 
	 * @param data Text fragment
	 * @return literal?
	 */
	public static boolean isKeyword(String data) {
		try {
			// Literal should be in enumeration
			WaebricKeyword literal = WaebricKeyword.valueOf(data.toUpperCase());
			return literal != null;
		} catch(IllegalArgumentException e) {
			// Enumeration does not exists
			return false;
		}
	}
	
	/**
	 * Determine whether a certain text fragment is an identifier.
	 * 
	 * @param data Text fragment
	 * @return identifier?
	 */
	public static boolean isIdentifier(String data) {
		if(data == null || data.equals("")) { return false; }
		char[] chars = data.toCharArray();
		
		// The character head should be a letter
		if(!isLetter(chars[0])) { return false; } 
		
		// All characters in the body should be letters or digits
		for(char c : chars) {
			if(!(isLetter(c) || isDigit(c) || c == '.')) { return false; }
		}
		
		return true;
	}
	
	public static boolean isSymbol(String data) {
		if(data == null || data.equals("")) { return false; }
		char[] chars = data.toCharArray();
		
		// Symbols are always only one character long
		if(chars.length != 1) { return false; }
		
		// Only symbols between decimal 32 and 126 are valid
		return isSymbol(chars[0]);
	}
	
	public static boolean isSymbol(char c) {
		int decimal = (int) c;
		return decimal >= 32 && decimal <= 126;
	}
	
	/**
	 * Determine whether a certain character is a letter.
	 * 
	 * @param c Character
	 * @return letter?
	 */
	public static boolean isLetter(char c) {
		return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
	}
	
	/**
	 * Determine whether a certain character is a digit.
	 * 
	 * @param c Character
	 * @return digit?
	 */
	public static boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

}