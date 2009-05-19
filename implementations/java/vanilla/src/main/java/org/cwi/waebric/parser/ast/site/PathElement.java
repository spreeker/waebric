package org.cwi.waebric.parser.ast.site;

import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;

public class PathElement implements ISyntaxNode {

	private StringLiteral literal;
	
	public PathElement(String literal) {
		this.literal = new StringLiteral(literal);
	}

	@Override
	public ISyntaxNode[] getChildren() {
		return new ISyntaxNode[] { literal };
	}
	
}
