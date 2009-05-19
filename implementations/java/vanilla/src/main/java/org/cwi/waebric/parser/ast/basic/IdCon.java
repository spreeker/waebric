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

	public void setIdentifier(StringLiteral identifier) {
		this.identifier = identifier;
	}

	@Override
	public ISyntaxNode[] getChildren() {
		return new StringLiteral[] { identifier };
	}
	
}
