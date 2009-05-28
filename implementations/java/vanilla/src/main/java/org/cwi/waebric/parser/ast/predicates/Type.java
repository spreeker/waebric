package org.cwi.waebric.parser.ast.predicates;

import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;

/**
 * Type represents a type definition.<br><br>
 * 
 * Grammar:<br>
 * <code>
 * 	"list" -> Type<br>
 * 	"record" -> Type<br>
 * 	"string" -> Type<br>
 * </code>
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
	
	@Override
	public boolean equals(Object obj) {
		return type.equals(obj);
	}
	
	@Override
	public String toString() {
		return type.toString();
	}

	public ISyntaxNode[] getChildren() {
		return new ISyntaxNode[] { type };
	}
	
}
