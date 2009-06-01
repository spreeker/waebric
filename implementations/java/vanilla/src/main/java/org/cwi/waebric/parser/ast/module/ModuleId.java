package org.cwi.waebric.parser.ast.module;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.SyntaxNodeList.SyntaxNodeListWithSeparator;
import org.cwi.waebric.parser.ast.basic.IdCon;

public class ModuleId extends AbstractSyntaxNode {

	private SyntaxNodeListWithSeparator<IdCon> identifier;
	
	public ModuleId() {
		identifier = new SyntaxNodeListWithSeparator<IdCon>(WaebricSymbol.PERIOD);
	}
	
	public ISyntaxNode[] getIdentifierElements() {
		return identifier.getElements();
	}

	public boolean addIdentifierElement(IdCon element) {
		return identifier.add(element);
	}

	public ISyntaxNode[] getChildren() {
		return identifier.getChildren();
	}
	
	@Override
	public boolean equals(Object obj) {
		return identifier.equals(obj);
	}

}