package org.cwi.waebric.parser.ast.module;

import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;
import org.cwi.waebric.scanner.token.WaebricKeyword;

public class Import implements IModuleElement {
	
	private ModuleId identifier;

	public ModuleId getIdentifier() {
		return identifier;
	}

	public void setIdentifier(ModuleId identifier) {
		this.identifier = identifier;
	}

	public ISyntaxNode[] getChildren() {
		return new ISyntaxNode[] { 
				new StringLiteral(WaebricKeyword.IMPORT.name().toLowerCase()), 
				identifier
			};
	}
	
	@Override
	public String toString() {
		return identifier.toString();
	}

}