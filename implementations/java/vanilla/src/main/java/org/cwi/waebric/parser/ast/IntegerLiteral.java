package org.cwi.waebric.parser.ast;

public class IntegerLiteral implements ISyntaxNode {

	private int literal;
	
	public IntegerLiteral(int literal) {
		this.literal = literal;
	}
	
	public int getLiteral() {
		return literal;
	}

	public void setLiteral(int literal) {
		this.literal = literal;
	}

	@Override
	public ISyntaxNode[] getChildren() {
		return null;
	}
	
	@Override
	public String toString() {
		return "" + literal;
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			int value = Integer.parseInt(obj.toString());
			return value == literal;
		} catch(NumberFormatException e) {
			return false;
		}
	}

}