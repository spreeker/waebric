package org.cwi.waebric.parser.ast.basic;

import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.IntegerLiteral;

public class NatCon implements ISyntaxNode {

	private IntegerLiteral identifier;
	
	public NatCon(int identifier) {
		this.identifier = new IntegerLiteral(identifier);
	}
	
	public NatCon(String identifier) {
		this.identifier = new IntegerLiteral(identifier);
	}
	
	public IntegerLiteral getIdentifier() {
		return identifier;
	}

	public void setIdentifier(IntegerLiteral identifier) {
		this.identifier = identifier;
	}

	public ISyntaxNode[] getChildren() {
		return new IntegerLiteral[] { identifier };
	}
	
	@Override
	public String toString() {
		return identifier.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return identifier.equals(obj);
	}
	
}