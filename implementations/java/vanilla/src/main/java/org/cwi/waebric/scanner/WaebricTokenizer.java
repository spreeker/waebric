package org.cwi.waebric.scanner;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.scanner.exception.ScannerException;
import org.cwi.waebric.scanner.exception.UnknownTokenException;
import org.cwi.waebric.scanner.token.TokenSort;

/**
 * Tokenizer for Waebric programs, reads input stream and processes
 * it into a token stream.
 * 
 * @author Jeroen van Schagen
 * @date 29-05-2009
 */
public class WaebricTokenizer {
	
	/**
	 * Default tab character length
	 */
	public static final int TAB_LENGTH = 5;
	
	// Value of current token
	private String svalue;
	private char cvalue;
	private int ivalue;
	
	// Location of current token
	private int lineno = 0;
	private int charno = 0;
	
	// Current character decimal
	private int current;
	
	private final Reader reader;
	private List<ScannerException> exceptions;
	
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
	public WaebricTokenizer(Reader reader, List<ScannerException> exceptions) throws IOException {
		if(reader == null) {
			throw new NullPointerException();
		}
		
		this.reader = reader;
		this.exceptions = exceptions;
		
		read(); // Buffer first character
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
	 * Retrieve next token, token specific attributes can be retrieved
	 * using the access methods available to this class.
	 * 
	 * @return sort
	 * @throws IOException
	 */
	public TokenSort nextToken() throws IOException {
		// Reset values
		svalue = "";
		cvalue = 0;
		ivalue = 0;
		
		if(current < 0) {
			// End of file
			return TokenSort.EOF;
		} else if(current == '/') {
			// Comments
			return nextComments();
		} else if(isLayout(current)) {
			// Layout
			return nextLayout();
		} else if(current == '"') {
			// String
			return nextString();
		} else if(current == '\'') {
			// Symbol
			return nextSymbol();
		} else if(isNumeral(current)) {
			// Number
			return nextNumber();
		} else if(isLetter(current)) {
			// Identifier
			return nextWord();
		} else if(isSymbol(current)) {
			// Symbol character
			return nextSymbolCharacter();
		} else {
			// Unknown
			exceptions.add(new UnknownTokenException("" + (char) current, lineno));
			return TokenSort.UNKNOWN;
		}
	}
	
	/**
	 * Retrieve next comment token.
	 * 
	 * @return
	 * @throws IOException
	 */
	private TokenSort nextComments() throws IOException {
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
	
	/**
	 * Retrieve next layout token.
	 * 
	 * @return
	 * @throws IOException
	 */
	private TokenSort nextLayout() throws IOException {
		read(); // Retrieve next character
		return nextToken(); // Skip separator and return next token instead
	}
	
	/**
	 * Retrieve next string token.
	 * 
	 * @return
	 * @throws IOException
	 */
	private TokenSort nextString() throws IOException {
		read(); // Retrieve first text character
		
		do { 
			svalue += (char) current; // Build string value
			read(); // Retrieve next text character
		} while(current != '"');
		
		read(); // Skip double quote
		return TokenSort.STRCON;
	}
	
	/**
	 * Retrieve next symbol token.
	 * 
	 * @return
	 * @throws IOException
	 */
	private TokenSort nextSymbol() throws IOException {
		read(); // Retrieve first symbol
		
		do {
			svalue += (char) current; // Build symbol value
			read(); // Retrieve next symbol
		} while(isSymbol(current));
		
		return TokenSort.SYMBOLCON;
	}
	
	/**
	 * Retrieve next symbol character token.
	 * 
	 * @return
	 * @throws IOException
	 */
	private TokenSort nextSymbolCharacter() throws IOException {
		cvalue = (char) current;
		read(); // Retrieve next character
		return TokenSort.SYMBOLCHAR;
	}
	
	/**
	 * Retrieve next natural token.
	 * 
	 * @return
	 * @throws IOException
	 */
	private TokenSort nextNumber() throws IOException {
		do {
			ivalue *= 10; // Create space for next character
			ivalue += toNumber(current);
			read(); // Read next number
		} while(isNumeral(current));
		
		// NatCon
		return TokenSort.NATCON;
	}
	
	/**
	 * Retrieve next word token.
	 * 
	 * @return
	 * @throws IOException
	 */
	private TokenSort nextWord() throws IOException {
		char head = (char) current; // Store head
		
		read(); // Retrieve next character
		if(isLetter(current) || isNumeral(current)) {
			svalue += head; // Place head in value
			
			do {
				svalue += (char) current;
				read(); // Read next character
			} while(isLetter(current) || isNumeral(current));
			
			// When word is not a keyword it is an identifier
			return isKeyword(svalue) ? TokenSort.KEYWORD : TokenSort.IDCON;
		} else {
			cvalue = head;
			read(); // Read next character
			return TokenSort.SYMBOLCHAR;
		}
	}

	/**
	 * Check if character is a letter.
	 * 
	 * @param c
	 * @return
	 */
	private boolean isLetter(int c) {
		return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
	}
	
	/**
	 * Check if character is a numeral.
	 * 
	 * @param c
	 * @return
	 */
	private boolean isNumeral(int c) {
		return c >= '0' && c <= '9';
	}
	
	/**
	 * Check if character is a layout.
	 * 
	 * @param c
	 * @return
	 */
	private boolean isLayout(int c) {
		return c == ' ' || c == '\t' || c =='\n' || c == '\r';
	}
	
	/**
	 * Check if character is a symbol.
	 * 
	 * @param c
	 * @return 
	 */
	private boolean isSymbol(int c) {
		return c > 31 && c < 127 && ! isLayout(c) && c != ';' && c != ',' && c != '>';
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
	 * Convert character to an integer value.
	 * 
	 * @param decimal
	 * @return
	 */
	private int toNumber(int decimal) {
		return decimal - 48; // '0' is positioned at decimal 48
	}
	
	/**
	 * Return current line number.
	 * 
	 * @return
	 */
	public int getLineNumber() {
		return lineno;
	}
	
	/**
	 * Return current character number.
	 * 
	 * @return
	 */
	public int getCharacterNumber() {
		return charno;
	}
	
	/**
	 * Return integer value.
	 * 
	 * @return
	 */
	public int getIntegerValue() {
		return ivalue;
	}
	
	/**
	 * Return character value.
	 * 
	 * @return
	 */
	public char getCharacterValue() {
		return cvalue;
	}
	
	/**
	 * Return string value.
	 * 
	 * @return
	 */
	public String getStringValue() {
		return svalue;
	}
	
}