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
	private WaebricTokenSort sort;
	private int lineno;
	private int charno;
	
	/**
	 * Initialize token
	 * 
	 * @param lexeme Block of text
	 * @param sort Token type
	 * @param line Line number
	 */
	public Token(Object lexeme, WaebricTokenSort sort, int lineno, int charno) {
		this.lexeme = lexeme;
		this.sort = sort;
		this.lineno = lineno;
		this.charno = charno;
	}		
	
	/**
	 * Initialize token
	 * 
	 * @param lexeme Block of text
	 * @param sort Token type
	 * @param line Line number
	 */
	public Token(Object lexeme, WaebricTokenSort sort, Position position) {
		this(lexeme, sort, position.lineno, position.charno);
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
		return lineno;
	}
	
	/**
	 * Modify line number
	 * @param lineno
	 */
	public void setLine(int lineno) {
		this.lineno = lineno;
	}
	
	/**
	 * Retrieve character number
	 * @return
	 */
	public int getCharacter() {
		return charno;
	}
	
	/**
	 * Modify character number
	 * @param charno
	 */
	public void setCharacter(int charno) {
		this.charno = charno;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Token) {
			Token token = (Token) obj;
			if(token.getSort() != this.getSort()) { return false; }
			if(token.getLine() != this.getLine()) { return false; }
			if(token.getCharacter() != this.getCharacter()) { return false; }
			return token.getLexeme().equals(this.getLexeme());
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return "\"" + lexeme.toString() + "\" " + sort.name() + 
		" (line: " + getLine() + ", character: " + getCharacter() + ")";
	}

}