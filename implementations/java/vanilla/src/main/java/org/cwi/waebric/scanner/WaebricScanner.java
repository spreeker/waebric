package org.cwi.waebric.scanner;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.scanner.exception.ScannerException;
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

	private WaebricTokenizer tokenizer;
	private List<Token> tokens;
	
	/**
	 * Initialize scanner
	 * 
	 * @param reader Input character stream
	 * @throws IOException 
	 */
	public WaebricScanner(Reader reader) throws IOException {
		tokenizer = new WaebricTokenizer(reader);
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
		
		// Scan and store tokens
		tokens.clear();
		TokenSort current = tokenizer.nextToken();
		while(current != TokenSort.EOF) {
			if(current == TokenSort.NATCON) {
				Token token = new Token(tokenizer.getIntegerValue(), current, tokenizer.getLineNumber(), tokenizer.getCharacterNumber());
				tokens.add(token);
			} else if(current == TokenSort.SYMBOLCHAR) {
				Token token = new Token(tokenizer.getCharacterValue(), current, tokenizer.getLineNumber(), tokenizer.getCharacterNumber());
				tokens.add(token);
			} else if(current == TokenSort.KEYWORD) {
				WaebricKeyword keyword = WaebricKeyword.valueOf(tokenizer.getStringValue().toUpperCase());
				Token token = new Token(keyword, current, tokenizer.getLineNumber(), tokenizer.getCharacterNumber());
				tokens.add(token);
			} else if(current == TokenSort.IDCON || current == TokenSort.STRCON || current == TokenSort.SYMBOLCON) {
				Token token = new Token(tokenizer.getStringValue(), current, tokenizer.getLineNumber(), tokenizer.getCharacterNumber());
				tokens.add(token);
			}
			
			// Retrieve next token
			current = tokenizer.nextToken();
		}
		
		// Report exceptions
		return exceptions;
	}
	
	static boolean isKeyword(String sval) {
		// TODO Auto-generated method stub
		return false;
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
			if(!(isLetter(c) || isDigit(c))) { return false; }
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