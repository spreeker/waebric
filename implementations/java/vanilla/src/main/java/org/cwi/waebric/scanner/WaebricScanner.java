package org.cwi.waebric.scanner;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.scanner.token.WaebricToken;
import org.cwi.waebric.scanner.token.WaebricTokenIterator;
import org.cwi.waebric.scanner.token.WaebricTokenSort;
import org.cwi.waebric.scanner.validator.ILexicalValidator;
import org.cwi.waebric.scanner.validator.LexicalException;
import org.cwi.waebric.scanner.validator.RejectValidator;

/**
 * The lexical analyzer, also known as a scanner, reads an input character stream
 * from which it generates a stream of tokens. During this "tokenization" process
 * layout and comments characters are removed to simplify and optimize parsing.
 * 
 * @author Jeroen van Schagen
 * @date 18-05-2009
 */
public class WaebricScanner implements Iterable<WaebricToken> {

	/**
	 * Stream tokenizer used for decomposing characters in tokens
	 */
	private final StreamTokenizer tokenizer;
	
	/**
	 * Collection of token stream validators
	 */
	private List<ILexicalValidator> validators;
	
	/**
	 * Collection of processed tokens
	 */
	private List<WaebricToken> tokens;
	
	/**
	 * Current character
	 */
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
		
		validators = new ArrayList<ILexicalValidator>();
		validators.add(new RejectValidator());
	}
	
	/**
	 * Convert character stream in token stream
	 * 
	 * @return List of scanner exceptions
	 * @throws IOException Fired by next token procedure in stream tokenizer.
	 * @see java.io.StreamTokenizer
	 */
	public List<LexicalException> tokenizeStream() throws IOException {	
		// Scan and store tokens
		tokens.clear();
		current = tokenizer.nextToken();
		while(current != StreamTokenizer.END_OF_FILE) {
			switch(current) {
				case StreamTokenizer.WORD:
					tokenizeWord();
					break;
				case StreamTokenizer.NUMBER:
					tokenizeNumber();
					break;
				case StreamTokenizer.CHARACTER:
					if(tokenizer.getCharacterValue() == '\'') {
						tokenizeSymbol();
					} else if(tokenizer.getCharacterValue() == '"') {
						tokenizeQuote();
					} else {
						tokenizeCharacter();
					} break;
				case StreamTokenizer.LAYOUT: 
					current = tokenizer.nextToken();
					break; // Layout tokens will not be parsed
				case StreamTokenizer.COMMENT: 
					current = tokenizer.nextToken();
					break; // Comment tokens will not be parsed
			}
		}
		
		// Scan for exceptions
		List<LexicalException> exceptions = new ArrayList<LexicalException>();
		for(ILexicalValidator validator : validators) {
			validator.validate(tokens, exceptions);
		}
		return exceptions;
	}

	/**
	 * 
	 * @param exceptions
	 * @throws IOException
	 */
	private void tokenizeWord() throws IOException {
		int lineno = tokenizer.getTokenLineNumber();
		int charno = tokenizer.getTokenCharacterNumber();
		
		String word = "";
		while(current == StreamTokenizer.WORD || current == StreamTokenizer.NUMBER) {
			word += tokenizer.getStringValue();
			current = tokenizer.nextToken();
		}
		
		if(isKeyword(word)) {
			WaebricKeyword type = WaebricKeyword.valueOf(word.toUpperCase());
			WaebricToken keyword = new WaebricToken(type, WaebricTokenSort.KEYWORD, lineno, charno);
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
	private void tokenizeNumber() throws IOException {
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
	private void tokenizeCharacter() throws IOException {
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
	private void tokenizeQuote() throws IOException {
		int lineno = tokenizer.getTokenLineNumber();
		int charno = tokenizer.getTokenCharacterNumber();
		
		current = tokenizer.nextToken(); // Skip " opening character
		
		String data = "";
		while(tokenizer.getCharacterValue() != '"') {
			if(current < 0) {
				// End of file found before closing ", store as separate tokens
				WaebricScanner scanner = new WaebricScanner(new StringReader(data));
				scanner.tokenizeStream();
				tokens.add(new WaebricToken(WaebricSymbol.DQUOTE, WaebricTokenSort.CHARACTER, lineno, charno));
				
				// Attach quote start position to sub-token
				for(WaebricToken token: scanner.getTokens()) {
					token.setLine(lineno + token.getLine() - 1);
					token.setCharacter(charno + token.getCharacter());
					tokens.add(token);
				}
				
				return;
			}
			
			data += tokenizer.toString(); // Build quote data
			current = tokenizer.nextToken(); // Retrieve next token
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
	private void tokenizeSymbol() throws IOException {
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