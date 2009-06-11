package org.cwi.waebric.parser.ast.statement.predicate;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;
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
public class Type extends AbstractSyntaxNode {
	
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

	public AbstractSyntaxNode[] getChildren() {
		return new AbstractSyntaxNode[] { type };
	}
	
	@Override
	public void accept(INodeVisitor visitor, Object[] args) {
		visitor.visit(this, args);
	}
	
}