package org.cwi.waebric.parser.ast.module;

import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.basic.IdCon;

public class ModuleId implements ISyntaxNode {

	private IdCon identifier;
	
	public ModuleId(String identifier) {
		this.identifier = new IdCon(identifier);
	}
	
	public IdCon getIdentifier() {
		return identifier;
	}

	public void setIdentifier(IdCon identifier) {
		this.identifier = identifier;
	}

	@Override
	public ISyntaxNode[] getChildren() {
		return new IdCon[] { identifier };
	}
	
	@Override
	public String toString() {
		return identifier.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return identifier.equals(obj);
	}

}
