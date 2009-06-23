package org.cwi.waebric.scanner.token;

import java.util.Iterator;
import java.util.List;

import org.cwi.waebric.WaebricKeyword;


/**
 * A token is a categorized block of text. This block of text, corresponding to the 
 * token is known as a lexeme.
 * 
 * @see http://en.wikipedia.org/wiki/Token_(parser)
 * 
 * @author Jeroen van Schagen
 * @date 18-05-2009
 */
public abstract class Token {

	protected int lineno;
	protected int charno;

	public Token(int lineno, int charno) {
		this.lineno = lineno;
		this.charno = charno;
	}
	
	/**
	 * Retrieve data
	 * @return
	 */
	public abstract Object getLexeme();
	
	/**
	 * Retrieve token sort
	 * @return
	 */
	public abstract WaebricTokenSort getSort();

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
			Token token = (Token) obj; // Cast object
			if(token.getLine() != this.getLine()) { return false; }
			else if(token.getCharacter() != this.getCharacter()) { return false; }
			else return token.getLexeme().equals(this.getLexeme());
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return "\"" + getLexeme().toString() + "\" " + getSort().name()
			+ " (line: " + lineno + ", character: " + charno + ")";
	}
	
	/**
	 * Identifier token
	 * @author Jeroen van Schagen
	 * @date 23-06-2009
	 */
	public static class IdentifierToken extends Token {

		public String identifier;
		
		public IdentifierToken(String identifier, int lineno, int charno) {
			super(lineno, charno);
			this.identifier = identifier;
		}
		
		@Override
		public String getLexeme() {
			return identifier;
		}
		
		@Override
		public WaebricTokenSort getSort() {
			return WaebricTokenSort.IDCON;
		}
		
	}
	
	/**
	 * Keyword token
	 * @author Jeroen van Schagen
	 * @date 23-06-2009
	 */
	public static class KeywordToken extends Token {

		public WaebricKeyword keyword;
		
		public KeywordToken(WaebricKeyword keyword, int lineno, int charno) {
			super(lineno, charno);
			this.keyword = keyword;
		}
		
		@Override
		public WaebricKeyword getLexeme() {
			return keyword;
		}
		
		@Override
		public WaebricTokenSort getSort() {
			return WaebricTokenSort.KEYWORD;
		}
		
	}
	
	/**
	 * Natural token
	 * @author Jeroen van Schagen
	 * @date 23-06-2009
	 */
	public static class NaturalToken extends Token {

		public Integer number;
		
		public NaturalToken(int number, int lineno, int charno) {
			super(lineno, charno);
			this.number = new Integer(number);
		}
		
		@Override
		public Integer getLexeme() {
			return number;
		}
		
		@Override
		public WaebricTokenSort getSort() {
			return WaebricTokenSort.NATCON;
		}
		
	}
	
	/**
	 * Symbol token
	 * @author Jeroen van Schagen
	 * @date 23-06-2009
	 */
	public static class SymbolToken extends Token {
		
		public String symbol;
		
		public SymbolToken(String symbol, int lineno, int charno) {
			super(lineno, charno);
			this.symbol = symbol;
		}
		
		@Override
		public Object getLexeme() {
			return symbol;
		}
		
		@Override
		public WaebricTokenSort getSort() {
			return WaebricTokenSort.SYMBOLCON;
		}
		
	}
	
	/**
	 * Character token
	 * @author Jeroen van Schagen
	 * @date 23-06-2009
	 */
	public static class CharacterToken extends Token {

		public Character character;
		
		public CharacterToken(char character, int lineno, int charno) {
			super(lineno, charno);
			this.character = new Character(character);
		}
		
		@Override
		public Character getLexeme() {
			return character;
		}
		
		@Override
		public WaebricTokenSort getSort() {
			return WaebricTokenSort.CHARACTER;
		}

	}
	
	/**
	 * Text token
	 * @author Jeroen van Schagen
	 * @date 23-06-2009
	 */
	public static class TextToken extends Token {
		
		private String text;
		
		public TextToken(String text, int lineno, int charno) {
			super(lineno, charno);
			this.text = text;
		}
		
		@Override
		public Object getLexeme() {
			return text;
		}
		
		@Override
		public WaebricTokenSort getSort() {
			return WaebricTokenSort.TEXT;
		}
		
	}
	
	/**
	 * Embedding token
	 * @author Jeroen van Schagen
	 * @date 23-06-2009
	 */
	public static class EmbeddingToken extends Token implements Iterable<Token> {

		private List<Token> content;
		
		public EmbeddingToken(List<Token> content, int lineno, int charno) {
			super(lineno, charno);
			this.content = content;
		}
		
		@Override
		public List<Token> getLexeme() {
			return content;
		}
		
		@Override
		public Iterator<Token> iterator() {
			return new TokenIterator(content);
		}
		
		@Override
		public WaebricTokenSort getSort() {
			return WaebricTokenSort.EMBEDDING;
		}

	}

}