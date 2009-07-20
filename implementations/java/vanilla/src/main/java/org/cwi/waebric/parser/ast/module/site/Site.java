package org.cwi.waebric.parser.ast.module.site;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;

/**
 * "site" Mappings "end" -> Site
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public class Site extends AbstractSyntaxNode {

	private Mappings mappings;

	public Mappings getMappings() {
		return mappings;
	}

	public void setMappings(Mappings mappings) {
		this.mappings = mappings;
	}

	public AbstractSyntaxNode[] getChildren() {
		return new AbstractSyntaxNode[] { 
			mappings
		};
	}
	
	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visit(this);
	}

}