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

}