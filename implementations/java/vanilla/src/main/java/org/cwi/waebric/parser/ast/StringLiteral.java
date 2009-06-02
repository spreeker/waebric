package org.cwi.waebric.parser.ast;

public class StringLiteral implements ISyntaxNode {

	private String literal;
	
	public StringLiteral(String literal) {
		this.literal = literal;
	}
	
	@Override
	public String toString() {
		return literal.toString();
	}

	@Override
	public boolean equals(Object obj) {
		return literal.equals(obj);
	}
	
	public ISyntaxNode[] getChildren() {
		return null;
	}
	
}