package org.cwi.waebric.parser.model.module;

import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.model.basic.IdCon;

public class ModuleId implements ISyntaxNode {

	private IdCon identifier;
	
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

}
