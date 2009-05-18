package org.cwi.waebric.scanner.token;

/**
 * A token is a categorized block of text. This block of text, corresponding to the 
 * token is known as a lexeme.
 * 
 * @see http://en.wikipedia.org/wiki/Token_(parser)
 * 
 * @author Jeroen van Schagen
 * @date 18-05-2009
 */
public class Token {

	private Object lexeme;
	private TokenSort sort;
	private int line;
	
	/**
	 * Initialize token
	 * 
	 * @param lexeme Block of text
	 * @param sort Token type
	 * @param line Line number
	 */
	public Token(Object lexeme, TokenSort sort, int line) {
		this.lexeme = lexeme;
		this.sort = sort;
		this.line = line;
	}

	/**
	 * Retrieve lexeme
	 * @return
	 */
	public Object getLexeme() {
		return lexeme;
	}

	/**
	 * Retrieve token sort
	 * @return
	 */
	public TokenSort getSort() {
		return sort;
	}

	/**
	 * Retrieve line number
	 * @return
	 */
	public int getLine() {
		return line;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Token) {
			Token token = (Token) obj;
			if(token.getSort() != this.sort) { return false; }
			if(token.getLine() != this.line) { return false; }
			return token.getLexeme().equals(this.lexeme);
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return "\"" + lexeme.toString() + "\" " + sort.name() + " (line: " + line + ")";
	}
	
}
