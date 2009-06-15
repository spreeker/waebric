package org.cwi.waebric.parser.ast.token;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;

public class IntegerLiteral extends AbstractSyntaxNode {

	private int literal;
	
	public IntegerLiteral(int literal) {
		this.literal = literal;
	}
	
	public IntegerLiteral(String identifier) throws NumberFormatException {
		this.literal = (int) Double.parseDouble(identifier);
	}
	
	public int toInteger() {
		return literal;
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

	public AbstractSyntaxNode[] getChildren() {
		return new AbstractSyntaxNode[] { /* Leaf node */ };
	}

	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visit(this);
	}
	
}