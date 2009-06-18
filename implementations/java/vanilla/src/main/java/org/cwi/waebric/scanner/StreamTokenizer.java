package org.cwi.waebric.scanner;

import java.io.IOException;
import java.io.Reader;

/**
 * Convert a character stream into a stream of tokens.
 * 
 * @author Jeroen van Schagen
 * @date 29-05-2009
 */
public class StreamTokenizer {
	
	/**
	 * Default tab character length
	 */
	public static final int TAB_LENGTH = 5;
	
	// Token type constants
	public static final int END_OF_FILE = -1;
	public static final int CHARACTER = 0;
	public static final int NUMBER = 1;
	public static final int WORD = 2;
	public static final int LAYOUT = 3;
	public static final int COMMENT = 4;
	
	// Current character properties
	private int current;
	private int lineno = 1;
	private int charno = 0;
	
	// Current token properties
	private String sval;
	private char cval;
	private int ival;
	
	private int tlineno = 1;
	private int tcharno = 0;
	
	/**
	 * Character stream
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
	public int nextToken() throws IOException {
		// Reset values
		sval = "";
		cval = 0;
		ival = -1;
		
		// Store token start location
		tlineno = lineno;
		tcharno = charno;
		
		if(current < 0) {
			// End of file
			return END_OF_FILE;
		} else if(current == '/') {
			// Comments token
			return nextComments();
		} else if(isLayout(current)) {
			// Layout character
			return nextLayout();
		} else if(isNumeral(current)) {
			// Number token
			return nextNumber();
		} else if(isLetter(current)) {
			// Word token
			return nextWord();
		} else {
			// Symbol character
			return nextCharacter();
		}
	}
	
	/**
	 * Retrieve next comment token.
	 * 
	 * @return
	 * @throws IOException
	 */
	private int nextComments() throws IOException {
		read(); // Retrieve next symbol
		
		if(current == '*') { // Multiple-line comment /* */
			char previous;
			read(); // Retrieve first comment character
			
			sval = "*/";
			do {
				sval += (char) current;
				previous = (char) current;
				read(); // Retrieve next comment character
			} while(!(previous == '*' && current == '/') && current > 0);
			
			read(); // Retrieve next character
			return COMMENT;
		} else if(current == '/') { // Single-line comment //
			read(); // Retrieve first comment character
			
			sval = "//";
			do {
				sval += (char) current;
				read(); // Retrieve next comment character
			} while(current != '\n'  && current > 0);
			
			read(); // Retrieve next character
			return COMMENT;
		} else { // Symbol character /
			cval = '/';
			return CHARACTER;
		}
	}
	
	/**
	 * Retrieve next layout token.
	 * 
	 * @return
	 * @throws IOException
	 */
	private int nextLayout() throws IOException {
		cval = (char) current;
		read(); // Retrieve next character
		return LAYOUT;
	}
	
	/**
	 * Retrieve next symbol character token.
	 * 
	 * @return
	 * @throws IOException
	 */
	private int nextCharacter() throws IOException {
		cval = (char) current;
		read(); // Retrieve next character
		return CHARACTER;
	}
	
	/**
	 * Retrieve next natural token.
	 * 
	 * @return
	 * @throws IOException
	 */
	private int nextNumber() throws IOException {
		ival = 0;
		
		while(isNumeral(current)) {
			ival *= 10; // Create space for next character
			ival += current - 48; // '0' equals decimal 48
			read(); // Read next number
		}

		return NUMBER;
	}
	
	/**
	 * Retrieve next word token.
	 * 
	 * @return
	 * @throws IOException
	 */
	private int nextWord() throws IOException {
		do {
			sval += (char) current;
			read(); // Read next character
		} while(isLetter(current) || isNumeral(current) || current == '-');
		
		return WORD;
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
		return ival;
	}
	
	/**
	 * Return character value.
	 * 
	 * @return
	 */
	public char getCharacterValue() {
		return cval;
	}
	
	/**
	 * Return string value.
	 * 
	 * @return
	 */
	public String getStringValue() {
		return sval;
	}
	
	/**
	 * 
	 */
	public String toString() {
		if(! sval.equals("")) {
			return sval;
		} else if(cval > 0) {
			return "" + cval;
		} else if(ival >= 0) {
			return "" + ival;
		}
		
		return null;
	}
	
}