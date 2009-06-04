package org.cwi.waebric.parser.ast.module;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.AbstractSyntaxNodeList;
import org.cwi.waebric.parser.ast.StringLiteral;

public class Module extends AbstractSyntaxNode {

	private static final String MODULE_KEYWORD = WaebricKeyword.MODULE.name().toLowerCase();
	
	private ModuleId identifier;
	private AbstractSyntaxNodeList<ModuleElement> elements;
	
	public Module() {
		elements = new AbstractSyntaxNodeList<ModuleElement>();
	}
	
	public ModuleId getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(ModuleId identifier) {
		this.identifier = identifier;
	}
	
	public boolean addElement(ModuleElement element) {
		return elements.add(element);
	}
	
	public AbstractSyntaxNode[] getElements() {
		return elements.getElements();
	}
	
	public AbstractSyntaxNode[] getChildren() {
		return new AbstractSyntaxNode[] {
			new StringLiteral(MODULE_KEYWORD),
			identifier,
			elements
		};
	}

}