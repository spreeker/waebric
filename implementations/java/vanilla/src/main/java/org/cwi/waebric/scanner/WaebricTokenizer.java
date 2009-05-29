package org.cwi.waebric.scanner;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.cwi.waebric.scanner.exception.ScannerException;
import org.cwi.waebric.scanner.exception.UnknownTokenException;
import org.cwi.waebric.scanner.token.TokenSort;

/**
 * Home-made super awesome tokenizer.
 * 
 * @author Jeroen van Schagen
 * @date 29-05-2009
 */
public class WaebricTokenizer {
	
	public static final int TAB_LENGTH = 5;
	
	// Data
	private String svalue;
	private char cvalue;
	private int ivalue;
	
	// Positioning
	private int lineno = 0;
	private int charno = 0;
	
	private int current;
	
	private final Reader reader;
	private List<ScannerException> exceptions;
	
	public WaebricTokenizer(Reader reader, List<ScannerException> exceptions) throws IOException {
		this.reader = reader;
		this.exceptions = exceptions;
		read();
	}

	public TokenSort nextToken() throws IOException {
		// Reset values
		svalue = "";
		cvalue = 0;
		ivalue = 0;
		
		// Check for end-of-file
		if(current < 0) { return TokenSort.EOF; }
	
		// Check for comments
		if(current == '/') {
			read(); // Retrieve next symbol
			if(current == '*') { // Multiple-line comment /* */
				read(); // Retrieve first comment character
				char previous;
				do {
					previous = (char) current; // Update previous
					read(); // Retrieve next comment character
				} while(!  (previous == '*' && current == '/'));
				return nextToken(); // Comments are ignored, thus return next
			} else if(current == '/') { // Single-line comment //
				read(); // Retrieve first comment character
				do {
					read(); // Retrieve next comment character
				} while(current != '\n');
				return nextToken(); // Comments are ignored, thus return next
			} else { // Symbol character /
				cvalue = '/';
				return TokenSort.SYMBOLCHAR;
			}
		}
		
		if(isSeparator(current)) {
			read(); // Read next character
			return nextToken(); // Skip separator and return next token instead
		}
		
//		if(current == '"') {
//			current = read(); // Read next character
//			
//			do {
//				svalue += (char) current;
//				current = read(); // Read next character
//			} while(current != '"');
//
//			// StringCon
//			return TokenSort.STRCON;
//		} 
//		
//		else if(current == '\'') {
//			do {
//				current = read(); // Read next character
//			} while(isSymbol(peek(1)));
//			
//			// SymbolCon
//			return TokenSort.SYMBOLCON;
//		}
//		
//		else if(isNumber(current)) {
//			ivalue = toNumber(current);
//			
//			do {
//				current = read(); // Read next number
//				ivalue *= 10; // Create space for next character
//				ivalue += toNumber(current);
//			} while(isNumber(peek(1)));
//			
//			// NatCon
//			return TokenSort.NATCON;
//		}
//		
//		else if(isLetter(current)) {
//			svalue = "" + (char) current;
//
//			do {
//				current = read(); // Read next character
//				svalue += (char) current;
//			} while(isLetter(peek(1)) || isNumber(peek(1)));
//			
//			return TokenSort.IDCON;
//		}
//		
//		else if(isSymbol(current)) {
//			cvalue = (char) current;
//			return TokenSort.SYMBOLCHAR;
//		}
		
		exceptions.add(new UnknownTokenException("" + (char) current, lineno));
		return TokenSort.UNKNOWN;
	}
	
	private int toNumber(int decimal) {
		return decimal - 48; // '0' is positioned at decimal 48
	}
	
	private boolean isNumber(int c) {
		return c >= '0' && c <= '9';
	}
	
	private boolean isLetter(int c) {
		return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
	}
	
	private boolean isSeparator(int c) {
		return c == ' ' || c == '\t' || c =='\n' || c == '\r';
	}
	
	private boolean isSymbol(int c) {
		return c > 31 && c != ' ' && c != '\t' && c != '\n' && c != '\r' && 
			c != ';' && c != ',' && c != '>' && c < 127;
	}

	/**
	 * Read next character from stream and increment character count.
	 * @return character
	 * @throws IOException
	 */
	private void read() throws IOException {
		current = reader.read();
		
		// Maintain actual line and character numbers
		if(current == '\n') { charno = 0; lineno++; } // New line
		else if(current == '\t') { charno += TAB_LENGTH; } // Tab
		else if(current >= 0) { charno++; } // Not end of file
	}
	
	/**
	 * Return current line number.
	 * @return
	 */
	public int getLineNumber() {
		return lineno;
	}
	
	/**
	 * Return current character number.
	 * @return
	 */
	public int getCharacterNumber() {
		return charno;
	}
	
	/**
	 * Return integer value.
	 * @return
	 */
	public int getIntegerValue() {
		return ivalue;
	}
	
	/**
	 * Return character value.
	 * @return
	 */
	public char getCharacterValue() {
		return cvalue;
	}
	
	/**
	 * Return string value.
	 * @return
	 */
	public String getStringValue() {
		return svalue;
	}
	
}