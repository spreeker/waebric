package org.cwi.waebric.parser.ast.basic;

import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;

public class StrCon implements ISyntaxNode {
	
	private StringLiteral string;

	public StringLiteral getString() {
		return string;
	}

	public void setString(StringLiteral string) {
		this.string = string;
	}

	@Override
	public boolean equals(Object obj) {
		return string.equals(obj);
	}
	
	@Override
	public String toString() {
		return string.toString();
	}
	
	public ISyntaxNode[] getChildren() {
		return new ISyntaxNode[] { string };
	}

}