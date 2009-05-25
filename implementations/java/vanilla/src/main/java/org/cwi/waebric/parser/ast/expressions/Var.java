package org.cwi.waebric.parser.ast.expressions;

import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.basic.IdCon;

public class Var implements ISyntaxNode {

	private IdCon identifier;

	public IdCon getIdentifier() {
		return identifier;
	}

	public void setIdentifier(IdCon identifier) {
		this.identifier = identifier;
	}
	
	@Override
	public boolean equals(Object obj) {
		return identifier.equals(obj);
	}
	
	@Override
	public String toString() {
		return identifier.toString();
	}

	public ISyntaxNode[] getChildren() {
		return new ISyntaxNode[] { identifier };
	}
	
}
