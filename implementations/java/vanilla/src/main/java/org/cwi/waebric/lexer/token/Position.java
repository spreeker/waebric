package org.cwi.waebric.lexer.token;

public class Position {

	public int lineno;
	public int charno;
	
	public Position() {
		this(1,0);
	}
	
	public Position(int lineno, int charno) {
		this.lineno = lineno;
		this.charno = charno;
	}
	
	public Position(Position position) {
		this.lineno = position.lineno;
		this.charno = position.charno;
	}
	
}
