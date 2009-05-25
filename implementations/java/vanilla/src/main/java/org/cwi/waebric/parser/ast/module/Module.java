package org.cwi.waebric.parser.ast.module;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;

public class Module implements ISyntaxNode {

	private static final String MODULE_KEYWORD = WaebricKeyword.MODULE.name().toLowerCase();
	
	private ModuleId identifier;
	private ModuleElements elements;
	
	public Module() {
		elements = new ModuleElements();
	}
	
	public ModuleId getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(ModuleId identifier) {
		this.identifier = identifier;
	}
	
	public boolean addElement(IModuleElement element) {
		return elements.add(element);
	}
	
	public IModuleElement[] getElements() {
		return elements.toArray(new IModuleElement[0]);
	}
	
	public ISyntaxNode[] getChildren() {
		return new ISyntaxNode[] {
				new StringLiteral(MODULE_KEYWORD),
				identifier,
				elements
		};
	}

}