package org.cwi.waebric.scanner;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.scanner.token.Token;
import org.cwi.waebric.scanner.token.TokenSort;
import org.cwi.waebric.scanner.token.WaebricLayout;
import org.cwi.waebric.scanner.token.WaebricLiteral;
import org.cwi.waebric.scanner.token.WaebricSymbol;

/**
 * 
 * @author schagen
 *
 */
public class WaebricScanner {

	private StreamTokenizer tokenizer;
	private List<Token> tokens;
	
	/**
	 * 
	 * @param reader
	 */
	public WaebricScanner(Reader reader) {
		tokenizer = new StreamTokenizer(reader);
		tokens = new ArrayList<Token>();
	}
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public List<ScannerException> scanStream() throws IOException {
		List<ScannerException> exceptions = new ArrayList<ScannerException>();
		
		tokenizer.whitespaceChars(WaebricLayout.NEW_LINE, WaebricLayout.NEW_LINE);
		tokenizer.whitespaceChars(WaebricLayout.TAB, WaebricLayout.TAB);
		
		// Scan and store tokens
		tokens.clear();
		int curr = tokenizer.nextToken();
		while(curr != StreamTokenizer.TT_EOF) {
			switch(curr) {
				// Numerals
				case StreamTokenizer.TT_NUMBER:
					try {
						tokens.add(new Token(tokenizer.nval, TokenSort.NUMBER, tokenizer.lineno()));
					} catch(Exception e) {
						exceptions.add(new ScannerException(tokenizer.nval, tokenizer.lineno(), e.getCause()));
					} break;
				// Literals
				case StreamTokenizer.TT_WORD:
					try {
						tokens.add(new Token(tokenizer.sval, TokenSort.LITERAL, tokenizer.lineno()));
					} catch(Exception e) {
						exceptions.add(new ScannerException(tokenizer.sval, tokenizer.lineno(), e.getCause()));
					} break;
				case StreamTokenizer.TT_EOF: break;
				case StreamTokenizer.TT_EOL: break;
				default:
					// Separate text from symbols
					TokenSort sort = (char) curr == WaebricSymbol.DQUOTE ? TokenSort.TEXT : TokenSort.LITERAL;
					try {
						tokens.add(new Token(tokenizer.sval, sort, tokenizer.lineno()));
					} catch(Exception e) {
						exceptions.add(new ScannerException(tokenizer.sval, tokenizer.lineno(), e.getCause()));
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
	 * 
	 * @return
	 */
	public List<Token> getTokens() {
		return tokens;
	}
	
	/**
	 * 
	 * @param data
	 * @return
	 */
	public static boolean isLiteral(String data) {
		try {
			// Literal should be in enumeration
			WaebricLiteral literal = WaebricLiteral.valueOf(data.toUpperCase());
			return literal != null;
		} catch(IllegalArgumentException e) {
			// Enumeration does not exists
			return false;
		}
	}
	
	/**
	 * 
	 * @param data
	 * @return
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
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isLetter(char c) {
		return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
	}
	
	/**
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

}