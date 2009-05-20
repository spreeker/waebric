package org.cwi.waebric.parser.ast.module;

import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.SyntaxNodeList.SyntaxNodeListWithSeparator;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.scanner.token.WaebricSymbol;

public class ModuleId implements ISyntaxNode {

	private SyntaxNodeListWithSeparator<IdCon> identifiers;
	
	public ModuleId() {
		identifiers = new SyntaxNodeListWithSeparator<IdCon>("" + WaebricSymbol.PERIOD);
	}
	
	public IdCon[] getIdentifiers() {
		return (IdCon[]) identifiers.getElements();
	}

	public boolean addIdentifier(IdCon identifier) {
		return identifiers.add(identifier);
	}

	public ISyntaxNode[] getChildren() {
		return identifiers.getChildren();
	}
	
	@Override
	public String toString() {
		return identifiers.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return identifiers.equals(obj);
	}

}
