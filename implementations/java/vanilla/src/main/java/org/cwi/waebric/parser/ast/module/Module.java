package org.cwi.waebric.parser.ast.module;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.AbstractSyntaxNodeList;
import org.cwi.waebric.parser.ast.StringLiteral;

/**
 * "module" ModuleId ModuleElement* -> Module
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public class Module extends AbstractSyntaxNode {

	private ModuleId identifier;
	private AbstractSyntaxNodeList<ModuleElement> elements;
	
	/**
	 * Construct module
	 */
	public Module() {
		elements = new AbstractSyntaxNodeList<ModuleElement>();
	}
	
	/**
	 * Retrieve module identifier
	 * @see ModuleId
	 * @return Module identifier
	 */
	public ModuleId getIdentifier() {
		return identifier;
	}
	
	/**
	 * Modify module identifier
	 * @see ModuleId
	 * @param identifier Module identifier
	 */
	public void setIdentifier(ModuleId identifier) {
		this.identifier = identifier;
	}
	
	/**
	 * Attach module element
	 * @see ModuleElement
	 * @param element ModuleElement
	 * @return Success?
	 */
	public boolean addElement(ModuleElement element) {
		return elements.add(element);
	}
	
	/**
	 * Retrieve module elements
	 * @see ModuleElement
	 * @return Module elements
	 */
	public AbstractSyntaxNode[] getElements() {
		return elements.toArray(new AbstractSyntaxNode[0]);
	}
	
	@Override
	public AbstractSyntaxNode[] getChildren() {
		return new AbstractSyntaxNode[] {
			new StringLiteral("module"),
			identifier,
			elements
		};
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Module) {
			Module module = (Module) obj;
			return this.identifier.equals(module.getIdentifier());
		}
		
		return false;
	}

}