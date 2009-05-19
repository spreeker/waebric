package org.cwi.waebric.scanner;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.scanner.token.HTMLKeyword;
import org.cwi.waebric.scanner.token.Token;
import org.cwi.waebric.scanner.token.TokenSort;
import org.cwi.waebric.scanner.token.WaebricLayout;
import org.cwi.waebric.scanner.token.WaebricKeyword;
import org.cwi.waebric.scanner.token.WaebricSymbol;

/**
 * The lexical analyzer, also known as a scanner, reads an input character stream
 * from which it generates a stream of tokens. During this "tokenization" process
 * layout and comments characters are removed to simplify and optimize parsing.
 * 
 * @author Jeroen van Schagen
 * @date 18-05-2009
 */
public class WaebricScanner {

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
					try {
						tokens.add(new Token(tokenizer.nval, TokenSort.NATCON, tokenizer.lineno()));
					} catch(Exception e) {
						exceptions.add(new ScannerException(tokenizer.nval, tokenizer.lineno(), e.getCause()));
					} break;
				case StreamTokenizer.TT_WORD:
					try {
						// Separate keywords from identifiers
						if(isWaebricKeyword(tokenizer.sval)) {
							WaebricKeyword literal = WaebricKeyword.valueOf(tokenizer.sval.toUpperCase());
							tokens.add(new Token(literal, TokenSort.KEYWORD, tokenizer.lineno()));
						} else if(isHTMLKeyword(tokenizer.sval)) {
							HTMLKeyword literal = HTMLKeyword.valueOf(tokenizer.sval.toUpperCase());
							tokens.add(new Token(literal, TokenSort.KEYWORD, tokenizer.lineno()));
						} else if(isIdentifier(tokenizer.sval)) {
							tokens.add(new Token(tokenizer.sval, TokenSort.IDCON, tokenizer.lineno()));
						} else {
							exceptions.add(new ScannerException(tokenizer.sval, tokenizer.lineno(), null));
						}
					} catch(Exception e) {
						exceptions.add(new ScannerException(tokenizer.sval, tokenizer.lineno(), e.getCause()));
					} break;
				case StreamTokenizer.TT_EOF: break;
				case StreamTokenizer.TT_EOL: break;
				default:
					// Separate text from symbols
					if((char) curr == WaebricSymbol.DQUOTE) {
						try {
							tokens.add(new Token(tokenizer.sval, TokenSort.TEXT, tokenizer.lineno()));
						} catch(Exception e) {
							exceptions.add(new ScannerException(tokenizer.sval, tokenizer.lineno(), e.getCause()));
						}	
					} else {
						char symbol = (char) curr;
						try {
							tokens.add(new Token(symbol, TokenSort.SYMBOL, tokenizer.lineno()));
						} catch(Exception e) {
							exceptions.add(new ScannerException(symbol, tokenizer.lineno(), e.getCause()));
						}
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
	 * Retrieve token stream
	 * 
	 * @return tokens
	 */
	public List<Token> getTokens() {
		return tokens;
	}
	
	/**
	 * Determine whether a certain text fragment is a literal.
	 * 
	 * @param data Text fragment
	 * @return literal?
	 */
	public static boolean isWaebricKeyword(String data) {
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
	 * Determine whether a certain text fragment is a literal.
	 * 
	 * @param data Text fragment
	 * @return literal?
	 */
	public static boolean isHTMLKeyword(String data) {
		try {
			// Literal should be in enumeration
			HTMLKeyword literal = HTMLKeyword.valueOf(data.toUpperCase());
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
		// Identifiers should have a size of 1+
		if(data == null || data.equals("")) { return false; }
		char[] chars = data.toCharArray();
		
		// The first char of an identifier should be a letter
		if(!isLetter(chars[0])) { return false; }
		
		// All characters in an identifier should be letters or digits
		for(char c : chars) {
			if(!(isLetter(c) || isDigit(c))) { return false; }
		}
		
		return true;
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