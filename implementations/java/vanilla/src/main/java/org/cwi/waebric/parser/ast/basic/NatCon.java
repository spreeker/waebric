package org.cwi.waebric.parser.ast.basic;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.token.IntegerLiteral;

/**
 * 
 * @author Jeroen van Schagen
 *
 */
public class NatCon extends AbstractSyntaxNode {

	/**
	 * Integer literal.
	 */
	private IntegerLiteral value;
	
	/**
	 * Construct empty natural.
	 */
	public NatCon() { this(0); }
	
	/**
	 * Construct natural based on integer.
	 * @param identifier
	 */
	public NatCon(int identifier) {
		this.value = new IntegerLiteral(identifier);
	}
	
	/**
	 * Construct natural based on string. Its value needs to a valid 
	 * integer value, else the default value '0' will be set.
	 * @param identifier
	 */
	public NatCon(String identifier) {
		this.value = new IntegerLiteral(identifier);
	}
	
	/**
	 * Retrieve literal
	 * @return
	 */
	public IntegerLiteral getLiteral() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		return value.equals(obj);
	}

	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public AbstractSyntaxNode[] getChildren() {
		return new IntegerLiteral[] { value };
	}

}