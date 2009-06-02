package org.cwi.waebric.scanner;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.scanner.exception.ScannerException;
import org.cwi.waebric.scanner.token.WaebricToken;
import org.cwi.waebric.scanner.token.WaebricTokenIterator;
import org.cwi.waebric.scanner.token.WaebricTokenSort;

/**
 * The lexical analyzer, also known as a scanner, reads an input character stream
 * from which it generates a stream of tokens. During this "tokenization" process
 * layout and comments characters are removed to simplify and optimize parsing.
 * 
 * @author Jeroen van Schagen
 * @date 18-05-2009
 */
public class WaebricScanner implements Iterable<WaebricToken> {

	private StreamTokenizer tokenizer;
	private List<WaebricToken> tokens;
	private int current;
	
	/**
	 * Initialize scanner
	 * 
	 * @param reader Input character stream
	 * @throws IOException 
	 */
	public WaebricScanner(Reader reader) throws IOException {
		tokenizer = new StreamTokenizer(reader);
		tokens = new ArrayList<WaebricToken>();
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
		current = tokenizer.nextToken();
		while(current != StreamTokenizer.END_OF_FILE) {
			switch(current) {
				case StreamTokenizer.WORD:
					tokenizeWord(exceptions);
					break;
				case StreamTokenizer.NUMBER:
					tokenizeNumber(exceptions);
					break;
				case StreamTokenizer.CHARACTER:
					if(tokenizer.getCharacterValue() == '\'') {
						tokenizeSymbol(exceptions);
					} else if(tokenizer.getCharacterValue() == '"') {
						tokenizeQuote(exceptions);
					} else {
						tokenizeCharacter(exceptions);
					} break;
				case StreamTokenizer.LAYOUT: 
					current = tokenizer.nextToken();
					break; // Layout tokens will not be parsed
				case StreamTokenizer.COMMENT: 
					current = tokenizer.nextToken();
					break; // Comment tokens will not be parsed
			}
		}
		
		// Report exceptions
		return exceptions;
	}

	/**
	 * 
	 * @param exceptions
	 * @throws IOException
	 */
	private void tokenizeWord(List<ScannerException> exceptions) throws IOException {
		int lineno = tokenizer.getTokenLineNumber();
		int charno = tokenizer.getTokenCharacterNumber();
		
		String word = "";
		while(current == StreamTokenizer.WORD || current == StreamTokenizer.NUMBER) {
			word += tokenizer.getStringValue();
			current = tokenizer.nextToken();
		}
		
		if(isKeyword(word)) {
			WaebricToken keyword = new WaebricToken(word, WaebricTokenSort.KEYWORD, lineno, charno);
			tokens.add(keyword);
		} else {
			WaebricToken identifier = new WaebricToken(word, WaebricTokenSort.IDCON, lineno, charno);
			tokens.add(identifier);
		}
	}
	
	/**
	 * 
	 * @param exceptions
	 * @throws IOException
	 */
	private void tokenizeNumber(List<ScannerException> exceptions) throws IOException {
		WaebricToken number = new WaebricToken(
				tokenizer.getIntegerValue(), WaebricTokenSort.NATCON, 
				tokenizer.getTokenLineNumber(), tokenizer.getTokenCharacterNumber()
			); // Construct number token
		
		tokens.add(number);
		current = tokenizer.nextToken(); // Jump to next token
	}
	
	/**
	 * 
	 * @param exceptions
	 * @throws IOException
	 */
	private void tokenizeCharacter(List<ScannerException> exceptions) throws IOException {
		WaebricToken character = new WaebricToken(
				tokenizer.getCharacterValue(), WaebricTokenSort.CHARACTER, 
				tokenizer.getTokenLineNumber(), tokenizer.getTokenCharacterNumber()
			); // Construct token
		
		tokens.add(character);
		current = tokenizer.nextToken(); // Jump to next token
	}
	
	/**
	 * " * "
	 * 
	 * @param exceptions
	 * @throws IOException
	 */
	private void tokenizeQuote(List<ScannerException> exceptions) throws IOException {
		int lineno = tokenizer.getTokenLineNumber();
		int charno = tokenizer.getTokenCharacterNumber();
		
		current = tokenizer.nextToken(); // Skip " opening character
		
		String data = "";
		while(tokenizer.getCharacterValue() != '"') {
			if(current < 0) { return; } // End of stream reached
			data += tokenizer.toString();
			current = tokenizer.nextToken();
		}

		current = tokenizer.nextToken(); // Skip " closure character
		
		WaebricToken quote = new WaebricToken(data, WaebricTokenSort.QUOTE, lineno, charno);
		tokens.add(quote);
	}
	
	/**
	 * ' SymbolChar*
	 * 
	 * @param exceptions
	 * @throws IOException
	 */
	private void tokenizeSymbol(List<ScannerException> exceptions) throws IOException {
		int lineno = tokenizer.getTokenLineNumber();
		int charno = tokenizer.getTokenCharacterNumber();
		
		current = tokenizer.nextToken(); // Skip ' opening character
		
		String data = "";
		while(isSymbolChars(tokenizer.toString())) {
			data += tokenizer.toString();
			current = tokenizer.nextToken();
		}
		
		WaebricToken symbol = new WaebricToken(data, WaebricTokenSort.SYMBOLCON, lineno, charno);
		tokens.add(symbol);
	}
	
//	private void tokenizeText(List<ScannerException> exceptions) throws IOException {
//		String lexeme = tokenizer.getStringValue();
//		
//		if(isString(lexeme)) {
//			// String: one valid word
//			Token string = new Token(lexeme, TokenSort.STRCON, tokenizer.getTokenLineNumber(), tokenizer.getTokenCharacterNumber());
//			tokens.add(string);
//		} else if(isText(lexeme)) {
//			// Text: sequence of valid words
//			Token text = new Token(lexeme, TokenSort.TEXT, tokenizer.getTokenLineNumber(), tokenizer.getTokenCharacterNumber());
//			tokens.add(text);
//		} else {
//			// No valid text or string, delegate tokenization to next level
//			WaebricScanner scanner = new WaebricScanner(new StringReader(lexeme));
//			List<ScannerException> e = scanner.tokenizeStream();
//			exceptions.addAll(e);
//			tokens.add(new Token(WaebricSymbol.DQUOTE, TokenSort.SYMBOLCHAR, tokenizer.getTokenLineNumber(), tokenizer.getTokenLineNumber()));
//			tokens.addAll(scanner.getTokens());
//			tokens.add(new Token(WaebricSymbol.DQUOTE, TokenSort.SYMBOLCHAR, tokenizer.getLineNumber(), tokenizer.getCharacterNumber()));
//		}
//	}

	/**
	 * Retrieve token
	 * 
	 * @param index Token index in structured text
	 * @return token
	 */
	public WaebricToken getToken(int index) {
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
	public List<WaebricToken> getTokens() {
		return tokens;
	}
	
	/**
	 * Retrieve token iterator
	 * 
	 * @return iterator
	 */
	public WaebricTokenIterator iterator() {
		return new WaebricTokenIterator(tokens);
	}
	
	/**
	 * 
	 * @param lexeme
	 * @return
	 */
	public static boolean isSymbolChars(String lexeme) {
		if(lexeme == null) { return false; }
		char chars[] = lexeme.toCharArray();

		for(char c: chars) {
			if(! isSymbolChar(c)) { return false; }
		}
		
		return true;
	}
	
	/**
	 * Check if character is a symbol.
	 * 
	 * @param c
	 * @return 
	 */
	private static boolean isSymbolChar(int c) {
		return c > 31 && c < 127 && c != ' ' && c != ';' && c != ',' && c != '>';
	}

	/**
	 * Check if lexeme is a keyword.
	 * 
	 * @param lexeme Token value
	 * @return 
	 */
	public boolean isKeyword(String lexeme) {
		try {
			// Literal should be in enumeration
			WaebricKeyword literal = WaebricKeyword.valueOf(lexeme.toUpperCase());
			return literal != null;
		} catch(IllegalArgumentException e) {
			// Enumeration does not exists
			return false;
		}
	}
	
	/**
	 * 
	 * @param lexeme
	 * @return
	 */
	public static boolean isTextChars(String lexeme) {
		if(lexeme == null) { return false; }
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
	public static boolean isStringChars(String lexeme) {
		if(lexeme == null) { return false; }
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