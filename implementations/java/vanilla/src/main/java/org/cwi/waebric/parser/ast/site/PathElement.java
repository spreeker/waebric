package org.cwi.waebric.parser.ast.site;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;

public class PathElement extends AbstractSyntaxNode {

	private StringLiteral literal;
	
	public PathElement(String literal) {
		this.literal = new StringLiteral(literal);
	}

	public AbstractSyntaxNode[] getChildren() {
		return new AbstractSyntaxNode[] { literal };
	}
	
	@Override
	public String toString() {
		return literal.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return literal.equals(obj);
	}
	
}
