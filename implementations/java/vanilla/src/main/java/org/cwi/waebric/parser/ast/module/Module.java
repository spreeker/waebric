package org.cwi.waebric.parser.ast.module;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;
import org.cwi.waebric.scanner.token.WaebricKeyword;

public class Module implements ISyntaxNode {

	private ModuleId identifier;
	private List<IModuleElement> elements;
	
	public Module() {
		elements = new ArrayList<IModuleElement>();
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
	
	@Override
	public ISyntaxNode[] getChildren() {
		List<ISyntaxNode> children = new ArrayList<ISyntaxNode>();
		children.add(new StringLiteral(WaebricKeyword.MODULE.name().toLowerCase()));
		children.add(identifier);
		children.addAll(elements);
		return children.toArray(new ISyntaxNode[0]);
	}

}