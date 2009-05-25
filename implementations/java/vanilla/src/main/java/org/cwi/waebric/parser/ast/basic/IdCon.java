package org.cwi.waebric.parser.ast.basic;

import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;

public class IdCon implements ISyntaxNode {

	private StringLiteral identifier;
	
	public IdCon(String identifier) {
		this.identifier = new StringLiteral(identifier);
	}
	
	public StringLiteral getIdentifier() {
		return identifier;
	}

	public ISyntaxNode[] getChildren() {
		return new StringLiteral[] { identifier };
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
