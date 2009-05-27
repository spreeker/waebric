package org.cwi.waebric.parser.ast.predicates;

import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;

/**
 * "list" -> Type
 * "record" -> Type
 * "string" -> Type
 * 
 * @author Jeroen van Schagen
 * @date 27-05-2009
 */
public class Type implements ISyntaxNode {
	
	private StringLiteral type;

	public StringLiteral getType() {
		return type;
	}

	public void setType(StringLiteral type) {
		this.type = type;
	}

	public ISyntaxNode[] getChildren() {
		return new ISyntaxNode[] { type };
	}
	
}
