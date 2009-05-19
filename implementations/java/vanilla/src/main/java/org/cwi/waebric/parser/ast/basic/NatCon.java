package org.cwi.waebric.parser.ast.basic;

import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.IntegerLiteral;

public class NatCon implements ISyntaxNode {

	private IntegerLiteral identifier;
	
	public IntegerLiteral getIdentifier() {
		return identifier;
	}

	public void setIdentifier(IntegerLiteral identifier) {
		this.identifier = identifier;
	}

	@Override
	public ISyntaxNode[] getChildren() {
		return new IntegerLiteral[] { identifier };
	}
	
}

