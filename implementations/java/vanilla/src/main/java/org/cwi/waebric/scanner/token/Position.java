package org.cwi.waebric.scanner.token;

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
	
}
