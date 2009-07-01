package org.cwi.waebric.parser.ast.markup;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.basic.IdCon;

/**
 * Designator
 * 
 * Grammar:<br>
 * <code>
 * 	IdCon Attribute* -> Designator
 * </code>
 * 
 * @author Jeroen van Schagen
 * @date 25-05-2009
 */
public class Designator extends AbstractSyntaxNode {

	protected IdCon identifier;
	protected Attributes attributes;

	public Designator(IdCon identifier) {
		this(identifier, new Attributes());
	}
	
	public Designator(IdCon identifier, Attributes attributes) {
		this.identifier = identifier;
		this.attributes = attributes;
	}
	
	public IdCon getIdentifier() {
		return identifier;
	}

	public void setIdentifier(IdCon identifier) {
		this.identifier = identifier;
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

	public AbstractSyntaxNode[] getChildren() {
		return new AbstractSyntaxNode[] { identifier, attributes };
	}
	
	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visit(this);
	}

}