package org.cwi.waebric.parser.ast.markup;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.ISyntaxNode;
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

	public ISyntaxNode[] getChildren() {
		return new ISyntaxNode[] { identifier, attributes };
	}

}