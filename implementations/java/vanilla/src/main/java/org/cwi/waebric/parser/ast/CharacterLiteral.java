package org.cwi.waebric.parser.ast;

public class CharacterLiteral implements ISyntaxNode {

	private char literal;
	
	public CharacterLiteral(char literal) {
		this.literal = literal;
	}

	public char getCharacter() {
		return literal;
	}

	public ISyntaxNode[] getChildren() {
		return null;
	}
	
	@Override
	public boolean equals(Object obj) {
		String data = obj.toString();
		if(data.length() > 1) { return false; }
		return literal == data.charAt(0);
	}
	
	@Override
	public String toString() {
		return "" + literal;
	}
	
}