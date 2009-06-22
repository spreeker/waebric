package org.cwi.waebric.scanner;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.scanner.token.Position;
import org.cwi.waebric.scanner.token.Token;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.cwi.waebric.scanner.token.WaebricTokenSort;

public class WaebricScanner {
	
	/**
	 * Character value to indicate EOF
	 */
	public static final int EOF = -1;
	
	/**
	 * Default tab character length
	 */
	public static final int TAB_LENGTH = 5;
	
	/**
	 * Current character
	 */
	private int curr;
	
	/**
	 * Value buffer
	 */
	private String buffer;
	
	/**
	 * Current character position
	 */
	private Position cpos;
	
	/**
	 * Current token position
	 */
	private Position tpos;
	
	/**
	 * Collection of tokens
	 */
	private final List<Token> tokens;

	/**
	 * Input stream
	 */
	private final Reader reader;
	
	/**
	 * Construct tokenizer based on reader, in case an invalid reader is given,
	 * a null pointer exception will be thrown.
	 * 
	 * @see Reader
	 * 
	 * @param reader Input character stream
	 * @param exceptions Collection of scan exceptions
	 * @throws IOException Thrown by Reader
	 */
	public WaebricScanner(Reader reader) throws IOException {
		if(reader == null) {
			throw new NullPointerException();
		}
		
		// Initiate position structures
		cpos = new Position();
		tpos = new Position();
		
		this.tokens = new ArrayList<Token>(); // Initiate collection
		this.reader = reader; // Store reader reference
		
		read(); // Buffer first character
	}
	
	/**
	 * Read next character from stream and increment character count.
	 * @return character
	 * @throws IOException
	 */
	private void read() throws IOException {
		curr = reader.read();
		
		// Maintain actual line and character numbers
		if(curr == '\n') { cpos.charno = 0; cpos.lineno++; } // New line
		else if(curr == '\t') { cpos.charno += TAB_LENGTH; } // Tab
		else if(curr >= 0) { cpos.charno++; } // Not end of file
	}
	
	/**
	 * 
	 * @return
	 * @throws IOException 
	 */
	public TokenIterator tokenizeStream() throws IOException {
		if(tokens.size() > 0) { return new TokenIterator(tokens); }
		
		while(curr != EOF) {
			buffer = ""; // Clean buffer
			
			// Store actual token position
			tpos.lineno = cpos.lineno;
			tpos.charno = cpos.charno;
			
			// Process token, based on first character
			if(isLayout(curr)) { processLayout(); }
			else if(curr == '/') { processComment(); }
			else if(curr == '"') { tokenizeText(); }
			else if(curr == '\'') { tokenizeSymbol(); }
			else if(isLetter(curr)) { tokenizeWord(); }
			else if(isNumeral(curr)) { tokenizeNumber(); }
			else { tokenizeCharacter(); }
		}
		
		return new TokenIterator(tokens);
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	private void processLayout() throws IOException {
		read(); // Skip layout characters
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	private void processComment() throws IOException {
		read(); // Retrieve next symbol
		
		if(curr == '*') { // Multiple-line comment /* */
			read(); // Retrieve first comment character
			
			char previous; // Store previous
			do {
				previous = (char) curr;
				read(); // Retrieve next comment character
			} while(! (previous == '*' && curr == '/') && curr != EOF);
			
			read(); // Retrieve next character
		} else if(curr == '/') { // Single-line comment //
			read(); // Retrieve first comment character
			
			do {
				read(); // Retrieve next comment character
			} while(curr != '\n'  && curr != EOF);
		
			read(); // Retrieve next character
		} else { // Symbol character /
			Token slash = new Token('/', WaebricTokenSort.CHARACTER, tpos);
			tokens.add(slash);
		}
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	private void tokenizeWord() throws IOException {
		do {
			buffer += (char) curr;
			read(); // Read next character
		} while(isLetter(curr) || isNumeral(curr) || curr == '-');
		
		// Determine token sort
		if(isKeyword(buffer)) {
			// Retrieve keyword element from enumeration
			WaebricKeyword element = WaebricKeyword.valueOf(buffer.toUpperCase());
			
			// Store keyword
			Token keyword = new Token(element, WaebricTokenSort.KEYWORD, tpos);
			tokens.add(keyword);
		} else {
			// Store word
			Token identifier = new Token(buffer, WaebricTokenSort.IDCON, tpos);
			tokens.add(identifier);
		}
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	private void tokenizeNumber() throws IOException {
		int number = 0; // Integer buffer
		
		while(isNumeral(curr)) {
			number *= 10; // Create space for next character
			number += curr - 48; // '0' equals decimal 48
			read(); // Read next number
		}
		
		// Store natural
		Token natural = new Token(number, WaebricTokenSort.NATCON, tpos);
		tokens.add(natural);
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	private void tokenizeCharacter() throws IOException {
		// Store character
		Token character = new Token((char) curr, WaebricTokenSort.CHARACTER, tpos);
		tokens.add(character);
		
		read(); // Retrieve next character
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	private void tokenizeSymbol() throws IOException {
		read(); // Retrieve first symbol character
		
		while(isSymbolChar(curr)) {
			buffer += (char) curr;
			read(); // Retrieve next symbol character
		}
		
		// Store symbol
		Token symbol = new Token(buffer, WaebricTokenSort.SYMBOLCON, tpos);
		tokens.add(symbol);
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	private void tokenizeText() throws IOException {
		read(); // Retrieve first character
		
		int previous = 0;
		do {
			if(curr == '<') {
				// Embedding character detected
				tokenizeEmbedding(); return;
			}

			previous = curr; // Store current as previous to detect \
			buffer += (char) curr; // Acceptable character, store in buffer
			
			read(); // Retrieve next character
		} while((curr != '"' || previous == '\\') && curr != EOF);
		
		// Store text as token
		Token text = new Token(buffer, WaebricTokenSort.TEXT, tpos);
		tokens.add(text);
		
		read(); // Skip closure " symbol
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	private void tokenizeEmbedding() throws IOException {
		boolean quoted = false; // Character in quote? " char "
		boolean embeded = false; // Character in embed? < char >
		
		int previous = 0;
		do {
			if(curr == EOF) {
				break; // TODO: Throw exception for missing >"
			}

			// Acceptable character, store in buffer
			buffer += (char) curr;
			previous = curr;
			
			if(curr == '"') { quoted = ! quoted; }
			if(curr == '<' && ! quoted) { embeded = true; }
			if(curr == '>' && ! quoted) { embeded = false; }
			
			read(); // Retrieve next character
		} while((curr != '"' || previous == '\\') || embeded);
		
		// Store embedding as token
		Token embedding = new Token(buffer, WaebricTokenSort.EMBEDDING, tpos);
		tokens.add(embedding);
		
		read(); // Skip closure " symbol
	}
	
	/**
	 * @param c
	 * @return
	 */
	public static boolean isLetter(int c) {
		return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
	}
	
	/**
	 * @param c
	 * @return
	 */
	public static boolean isNumeral(int c) {
		return c >= '0' && c <= '9';
	}
	
	/**
	 * @param c
	 * @return
	 */
	public static boolean isLayout(int c) {
		return c == ' ' || c == '\t' || c =='\n' || c == '\r';
	}
	
	/**
	 * @param c
	 * @return 
	 */
	private static boolean isSymbolChar(int c) {
		return c > 31 && c < 127 && c != ' ' && c != ';' && c != ',' && c != '>' && c != '}' && c != ']' && c != ')';
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
			char c = chars[i];
			if(! isTextChar(c)) { 
				if(c == '&' || c == '"') { 
					// Allow \& and \"
					if(i == 0 || chars[i-1] != '\\') {
						// Allow text from XML grammar
						String sub = lexeme.substring(i);
						return sub.matches("&#[0-9]+;.*") 
								|| sub.matches("&#x[0-9a-fA-F]+;.*") 
								|| sub.matches("&[a-zA-Z_:][a-zA-Z0-9.-_:]*;.*");
					}
				} else { return false; }
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
		return c > 31 && c < 128 && c != '<' && c != '&' && c != '"' || c == '\n' || c == '\t' || c == '\r';
	}
	
	/**
	 * @param lexeme
	 * @return
	 */
	public static boolean isStringChars(String lexeme) {
		if(lexeme == null) { return false; }
		char chars[] = lexeme.toCharArray();
		
		for(int i = 0; i < chars.length; i++) {
			char c = chars[i]; // Retrieve current character
			if(! isStringChar(c)) {
				if(c == '\\') { // Allow "\\n" "\\t" "\\\"" "\\\\"
					if(i+1 < chars.length) {
						char peek = chars[i+1];
						if(peek == 'n' || peek == 't' || peek == '"' || peek == '\\') {
							i++; // Check checking \\ and accept
						} else { return false; }
					} else { return false; }
				} else { return false; }
			}
		}
		
		return true;
	}
	
	/**
	 * @param c
	 * @return
	 */
	public static boolean isStringChar(char c) {
		return c > 31 && c != '\n' && c != '\t' && c != '"' && c != '\\';
	}
	
	/**
	 * @param lexeme
	 * @return
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
	 * Retrieve tokens
	 * @return
	 */
	public List<Token> getTokens() {
		return tokens;
	}
	
}