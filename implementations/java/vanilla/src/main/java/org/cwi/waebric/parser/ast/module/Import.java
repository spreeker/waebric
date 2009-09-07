package org.cwi.waebric.parser.ast.module;

import org.cwi.waebric.parser.ast.SyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;

/**
 * "import" ModuleId -> Import
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public class Import extends SyntaxNode {
		
	private ModuleId identifier;

	public ModuleId getIdentifier() {
		return identifier;
	}

	public void setIdentifier(ModuleId identifier) {
		this.identifier = identifier;
	}

	public SyntaxNode[] getChildren() {
		return new SyntaxNode[] { identifier };
	}
	
	@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}

}