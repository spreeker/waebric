package org.cwi.waebric.parser.ast.module;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;

public class Import extends ModuleElement {
	
	private static final String IMPORT_KEYWORD = WaebricKeyword.IMPORT.name().toLowerCase();
	
	private ModuleId identifier;

	public ModuleId getIdentifier() {
		return identifier;
	}

	public void setIdentifier(ModuleId identifier) {
		this.identifier = identifier;
	}

	public AbstractSyntaxNode[] getChildren() {
		return new AbstractSyntaxNode[] { 
			new StringLiteral(IMPORT_KEYWORD), 
			identifier
		};
	}

}