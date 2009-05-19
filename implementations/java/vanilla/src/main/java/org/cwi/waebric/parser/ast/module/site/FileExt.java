package org.cwi.waebric.parser.ast.module.site;

import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;

public class FileExt implements ISyntaxNode {

	private StringLiteral literal;
	
	public FileExt(String literal) {
		this.literal = new StringLiteral(literal);
	}

	@Override
	public ISyntaxNode[] getChildren() {
		return new ISyntaxNode[] { literal };
	}
	
}