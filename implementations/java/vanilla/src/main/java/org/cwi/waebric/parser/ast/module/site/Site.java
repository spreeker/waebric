package org.cwi.waebric.parser.ast.module.site;

import org.cwi.waebric.parser.ast.SyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;

/**
 * "site" Mappings "end" -> Site
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public class Site extends SyntaxNode {

	private Mappings mappings;

	public Mappings getMappings() {
		return mappings;
	}

	public void setMappings(Mappings mappings) {
		this.mappings = mappings;
	}

	public SyntaxNode[] getChildren() {
		return new SyntaxNode[] { 
			mappings
		};
	}
	
	@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}

}