package org.cwi.waebric.parser.ast.module;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;

/**
 * "import" ModuleId -> Import
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public class Import extends AbstractSyntaxNode {
		
	private ModuleId identifier;

	public ModuleId getIdentifier() {
		return identifier;
	}

	public void setIdentifier(ModuleId identifier) {
		this.identifier = identifier;
	}

	public AbstractSyntaxNode[] getChildren() {
		return new AbstractSyntaxNode[] { identifier };
	}
	
	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visit(this);
	}

}