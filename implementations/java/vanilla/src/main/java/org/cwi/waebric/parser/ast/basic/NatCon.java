package org.cwi.waebric.parser.ast.basic;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.IntegerLiteral;

public class NatCon extends AbstractSyntaxNode {

	private IntegerLiteral literal;
	
	public NatCon(int identifier) {
		this.literal = new IntegerLiteral(identifier);
	}
	
	public NatCon(String identifier) {
		this.literal = new IntegerLiteral(identifier);
	}
	
	public IntegerLiteral getLiteral() {
		return literal;
	}

	@Override
	public boolean equals(Object obj) {
		return literal.equals(obj);
	}
	
	public AbstractSyntaxNode[] getChildren() {
		return new IntegerLiteral[] { literal };
	}

}