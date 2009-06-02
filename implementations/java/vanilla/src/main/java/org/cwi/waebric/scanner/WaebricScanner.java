package org.cwi.waebric.scanner;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.WaebricSymbol;
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

	private StreamTokenizer tokenizer;
	private List<Token> tokens;
	
	/**
	 * Initialize scanner
	 * 
	 * @param reader Input character stream
	 * @throws IOException 
	 */
	public WaebricScanner(Reader reader) throws IOException {
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
		
		// Scan and store tokens
		tokens.clear();
		TokenSort current = tokenizer.nextToken();
		while(current != TokenSort.EOF) {
			if(current == TokenSort.NATCON) {
				Token token = new Token(tokenizer.getIntegerValue(), current, tokenizer.getTokenLineNumber(), tokenizer.getTokenCharacterNumber());
				tokens.add(token);
			} else if(current == TokenSort.SYMBOLCHAR) {
				Token token = new Token(tokenizer.getCharacterValue(), current, tokenizer.getTokenLineNumber(), tokenizer.getTokenCharacterNumber());
				tokens.add(token);
			} else if(current == TokenSort.KEYWORD) {
				WaebricKeyword keyword = WaebricKeyword.valueOf(tokenizer.getStringValue().toUpperCase());
				Token token = new Token(keyword, current, tokenizer.getTokenLineNumber(), tokenizer.getTokenCharacterNumber());
				tokens.add(token);
			} else if(current == TokenSort.IDCON || current == TokenSort.SYMBOLCON) {
				Token token = new Token(tokenizer.getStringValue(), current, tokenizer.getTokenLineNumber(), tokenizer.getTokenCharacterNumber());
				tokens.add(token);
			} else if(current == TokenSort.TEXT) {
				tokenizeText(exceptions);
			}
			
			// Retrieve next token
			current = tokenizer.nextToken();
		}
		
		// Report exceptions
		return exceptions;
	}
	
	private void tokenizeText(List<ScannerException> exceptions) throws IOException {
		String lexeme = tokenizer.getStringValue();
		
		if(isString(lexeme)) {
			// String: one valid word
			Token string = new Token(lexeme, TokenSort.STRCON, tokenizer.getTokenLineNumber(), tokenizer.getTokenCharacterNumber());
			tokens.add(string);
		} else if(isText(lexeme)) {
			// Text: sequence of valid words
			Token text = new Token(lexeme, TokenSort.TEXT, tokenizer.getTokenLineNumber(), tokenizer.getTokenCharacterNumber());
			tokens.add(text);
		} else {
			// No valid text or string, delegate tokenization to next level
			WaebricScanner scanner = new WaebricScanner(new StringReader(lexeme));
			List<ScannerException> e = scanner.tokenizeStream();
			exceptions.addAll(e);
			tokens.add(new Token(WaebricSymbol.DQUOTE, TokenSort.SYMBOLCHAR, tokenizer.getTokenLineNumber(), tokenizer.getTokenLineNumber()));
			tokens.addAll(scanner.getTokens());
			tokens.add(new Token(WaebricSymbol.DQUOTE, TokenSort.SYMBOLCHAR, tokenizer.getLineNumber(), tokenizer.getCharacterNumber()));
		}
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
	 * Retrieve token list
	 * 
	 * @return
	 */
	public List<Token> getTokens() {
		return tokens;
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
	 * 
	 * @param lexeme
	 * @return
	 */
	public static boolean isText(String lexeme) {
		char chars[] = lexeme.toCharArray();
		
		for(int i = 0; i < chars.length; i++) {
			char c = chars[i]; // Retrieve current character
			if(! isTextChar(c)) {
				// Allow "\\&" "\\""
				if(c == '&' || c == '"') {
					if(i > 0) {
						char previous = chars[i-1];
						if(previous == '\\') {
							i++; // Skip checking & or ' and accept
						} else {
							return false; // Incorrect occurrence of & or " character
						}
					} else {
						return false; // Incorrect occurrence of & or " character
					}
				} else {
					return false; // Incorrect character
				}
			}
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isTextChar(char c) {
		return c > 31 && c < 128 && c != '<' && c != '&' && c != '"' 
			|| c == '\n' || c == '\t' || c == '\r';
	}
	
	/**
	 * 
	 * @param lexeme
	 * @return
	 */
	public static boolean isString(String lexeme) {
		char chars[] = lexeme.toCharArray();
		
		for(int i = 0; i < chars.length; i++) {
			char c = chars[i]; // Retrieve current character
			if(! isStringChar(c)) {
				// Allow "\\n" "\\t" "\\\"" "\\\\"
				if(c == '\\') {
					if(i+1 < chars.length) {
						char peek = chars[i+1];
						if(peek == 'n' || peek == 't' || peek == '"' || peek == '\\') {
							i++; // Check checking \\ and accept
						} else {
							return false; // Invalid occurrence of '\\'
						}
					} else {
						return false; // Invalid occurrence of '\\'
					}
				} else {
					return false; // Invalid string symbol found
				}
			}
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isStringChar(char c) {
		return c > 31 && c != '\n' && c != '\t' && c != '"' && c != '\\';
	}

}