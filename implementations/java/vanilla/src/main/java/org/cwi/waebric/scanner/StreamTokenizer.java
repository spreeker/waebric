package org.cwi.waebric.scanner;

import java.io.IOException;
import java.io.Reader;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.scanner.token.TokenSort;

/**
 * Tokenizer for Waebric programs, reads input stream and processes
 * it into a token stream.
 * 
 * @author Jeroen van Schagen
 * @date 29-05-2009
 */
class StreamTokenizer {
	
	/**
	 * Default tab character length
	 */
	public static final int TAB_LENGTH = 5;
	
	// Current character properties
	private int current;
	private int lineno = 1;
	private int charno = 0;
	
	// Current token properties
	private String svalue;
	private char cvalue;
	private int ivalue;
	
	private int tlineno = 1;
	private int tcharno = 0;
	
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
	public StreamTokenizer(Reader reader) throws IOException {
		if(reader == null) {
			throw new NullPointerException();
		}
		
		this.reader = reader;
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
		
		// Store token start location
		tlineno = lineno;
		tcharno = charno;
		
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
			return nextText();
		} else if(current == '\'') {
			// Symbol
			return nextSymbol();
		} else if(isNumeral(current)) {
			// Number
			return nextNumber();
		} else if(isLetter(current)) {
			// Identifier
			return nextWord();
		} else {
			// Symbol character
			return nextCharacterSymbol();
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
			char previous;
			read(); // Retrieve first comment character
			do {
				previous = (char) current; // Update previous
				read(); // Retrieve next comment character
			} while(!  (previous == '*' && current == '/'));
			read(); // Retrieve next character
			return nextToken(); // Comments are ignored, thus return next
		} else if(current == '/') { // Single-line comment //
			read(); // Retrieve first comment character
			do {
				read(); // Retrieve next comment character
			} while(current != '\n');
			read(); // Retrieve next character
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
	private TokenSort nextText() throws IOException {
		read(); // Retrieve first text character
		
		while(current != '"') {
			if(current < 0) { return TokenSort.TEXT; }
			svalue += (char) current; // Build string value
			read(); // Retrieve next text character
		}
		
		read(); // Skip closure symbol "
		
		return TokenSort.TEXT;
	}
	
	/**
	 * Retrieve next symbol token.
	 * 
	 * @return
	 * @throws IOException
	 */
	private TokenSort nextSymbol() throws IOException {
		read(); // Retrieve first symbol
		
		while(isSymbol(current)) {
			svalue += (char) current; // Build symbol value
			read(); // Retrieve next symbol
		}
		
		return TokenSort.SYMBOLCON;
	}
	
	/**
	 * Retrieve next symbol character token.
	 * 
	 * @return
	 * @throws IOException
	 */
	private TokenSort nextCharacterSymbol() throws IOException {
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
		while(isNumeral(current)) {
			ivalue *= 10; // Create space for next character
			ivalue += toNumber(current);
			read(); // Read next number
		}
		
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
		int head = current; // Store head letter
		
		read(); // Retrieve next character
		if(isLetter(current) || isNumeral(current)) {
			svalue += (char) head; // Place head in value
			
			while(isLetter(current) || isNumeral(current)) {
				svalue += (char) current;
				read(); // Read next character
			}
			
			// When word is not a keyword it is an identifier
			return isKeyword(svalue) ? TokenSort.KEYWORD : TokenSort.IDCON;
		} else {
			cvalue = (char) head;
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
	 * Return token line number.
	 * 
	 * @return
	 */
	public int getTokenLineNumber() {
		return tlineno;
	}
	
	/**
	 * Return token character number.
	 * 
	 * @return
	 */
	public int getTokenCharacterNumber() {
		return tcharno;
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