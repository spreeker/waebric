package org.cwi.waebric.parser.ast.markup;

import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.basic.IdCon;

public class Designator implements ISyntaxNode {

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

	public boolean addAttribute(Attribute attribute) {
		return attributes.add(attribute);
	}

	@Override
	public ISyntaxNode[] getChildren() {
		return new ISyntaxNode[] { identifier, attributes };
	}

}