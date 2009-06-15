package org.cwi.waebric.parser.ast.token;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;

public class StringLiteral extends AbstractSyntaxNode {

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
	
	public AbstractSyntaxNode[] getChildren() {
		return new AbstractSyntaxNode[] { /* Leaf node */ };
	}
	
	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visit(this);
	}
	
}