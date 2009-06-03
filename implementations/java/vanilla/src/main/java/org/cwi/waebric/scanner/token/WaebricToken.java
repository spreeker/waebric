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
public class WaebricToken {

	private Object lexeme;
	private WaebricTokenSort sort;
	private int line;
	private int character;
	
	/**
	 * Initialize token
	 * 
	 * @param lexeme Block of text
	 * @param sort Token type
	 * @param line Line number
	 */
	public WaebricToken(Object lexeme, WaebricTokenSort sort, int line, int character) {
		this.lexeme = lexeme;
		this.sort = sort;
		this.line = line;
		this.character = character;
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
	public WaebricTokenSort getSort() {
		return sort;
	}

	/**
	 * Retrieve line number
	 * @return
	 */
	public int getLine() {
		return line;
	}
	
	/**
	 * Retrieve character number
	 * @return
	 */
	public int getCharacter() {
		return character;
	}
	
	/**
	 * Modify line number
	 * @param line
	 */
	public void setLine(int line) {
		this.line = line;
	}

	/**
	 * Modify chararacter number
	 * @param character
	 */
	public void setCharacter(int character) {
		this.character = character;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof WaebricToken) {
			WaebricToken token = (WaebricToken) obj;
			if(token.getSort() != this.sort) { return false; }
			if(token.getLine() != this.line) { return false; }
			if(token.getCharacter() != this.character) { return false; }
			return token.getLexeme().equals(this.lexeme);
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return "\"" + lexeme.toString() + "\" " + sort.name() + 
		" (line: " + line + ", character: " + character + ")";
	}

}